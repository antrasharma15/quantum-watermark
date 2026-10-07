package quantum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Quantum-LSB watermarking for video, frame by frame.
 *
 * A video is just a sequence of images (frames). The pipeline is:
 *   1. FFmpeg splits the video into PNG frames (lossless).
 *   2. Every N-th frame is watermarked with ImageWatermarker (quantum LSB).
 *   3. FFmpeg joins the frames back into a LOSSLESS video (FFV1 codec, .mkv),
 *      and copies the original audio.
 *
 * Lossless output is required: a normal lossy codec (H.264) would change the
 * pixel values and destroy the last-qubit watermark.
 *
 * FFmpeg must be installed and on the PATH (or set -Dffmpeg.path / -Dffprobe.path).
 */
public class Videowatermarker {
    private static final String FFMPEG  = System.getProperty("ffmpeg.path", "ffmpeg");
    private static final String FFPROBE = System.getProperty("ffprobe.path", "ffprobe");
    private static final int SCAN_FRAMES = 60;   // how many frames extract() will check

    /** Summary of an embedding run. */
    public record Result(int totalFrames, int markedFrames) { }

    /** Embed 'message' into every 'interval'-th frame of 'input'; write lossless video to 'output' (.mkv). */
    public static Result embed(File input, File output, String message, int interval,
                               Consumer<String> log) throws IOException, InterruptedException {
        if (interval < 1) throw new IllegalArgumentException("Interval must be at least 1");
        Path tmp = Files.createTempDirectory("qwm_frames");
        try {
            log.accept("Splitting video into frames...");
            run(List.of(FFMPEG, "-y", "-hide_banner", "-loglevel", "error",
                    "-i", input.getAbsolutePath(), "-fps_mode", "passthrough",
                    tmp.resolve("frame_%06d.png").toString()));

            List<Path> frames = listPngs(tmp);
            if (frames.isEmpty()) throw new IOException("No frames could be read from the video.");
            log.accept("Found " + frames.size() + " frames. Watermarking every " + interval + "-th frame...");

            int marked = 0;
            for (int i = 0; i < frames.size(); i += interval) {
                BufferedImage frame = ImageIO.read(frames.get(i).toFile());
                BufferedImage out = ImageWatermarker.embed(frame, message);   // quantum LSB
                ImageIO.write(out, "png", frames.get(i).toFile());            // overwrite frame
                marked++;
                if (marked % 10 == 0) log.accept("Watermarked " + marked + " frames...");
            }

            String fps = probeFrameRate(input);
            log.accept("Rebuilding lossless video at " + fps + " fps...");
            run(List.of(FFMPEG, "-y", "-hide_banner", "-loglevel", "error",
                    "-framerate", fps, "-i", tmp.resolve("frame_%06d.png").toString(),
                    "-i", input.getAbsolutePath(),
                    "-map", "0:v", "-map", "1:a?",
                    "-c:v", "ffv1", "-pix_fmt", "bgr0", "-c:a", "copy",
                    output.getAbsolutePath()));
            log.accept("Done.");
            return new Result(frames.size(), marked);
        } finally {
            deleteRecursively(tmp);
        }
    }

    /** Looks through the first frames of a video and returns the first hidden message found. */
    public static String extract(File video, Consumer<String> log) throws IOException, InterruptedException {
        Path tmp = Files.createTempDirectory("qwm_extract");
        try {
            log.accept("Reading first frames...");
            run(List.of(FFMPEG, "-y", "-hide_banner", "-loglevel", "error",
                    "-i", video.getAbsolutePath(), "-fps_mode", "passthrough",
                    "-frames:v", String.valueOf(SCAN_FRAMES),
                    tmp.resolve("f_%03d.png").toString()));

            List<Path> frames = listPngs(tmp);
            for (int i = 0; i < frames.size(); i++) {
                try {
                    String message = ImageWatermarker.extract(ImageIO.read(frames.get(i).toFile()));
                    log.accept("Watermark found in frame " + i + ".");
                    return message;
                } catch (RuntimeException notMarked) {
                    // this frame has no valid watermark, try the next one
                }
            }
            throw new IllegalStateException("No valid watermark found in the first " + frames.size() + " frames.");
        } finally {
            deleteRecursively(tmp);
        }
    }

    /** Demo helper: compress to lossy H.264 MP4 (used to show that the watermark does not survive). */
    public static void compressToMp4(File input, File output) throws IOException, InterruptedException {
        run(List.of(FFMPEG, "-y", "-hide_banner", "-loglevel", "error",
                "-i", input.getAbsolutePath(), "-c:v", "libx264", "-pix_fmt", "yuv420p", output.getAbsolutePath()));
    }

    // ---------------------------------------------------------------- helpers

    /** Frame rate as a fraction string such as "30/1" or "30000/1001". */
    private static String probeFrameRate(File video) throws IOException, InterruptedException {
        String out = run(List.of(FFPROBE, "-v", "error", "-select_streams", "v:0",
                "-show_entries", "stream=r_frame_rate", "-of", "default=noprint_wrappers=1:nokey=1",
                video.getAbsolutePath())).trim();
        return out.isEmpty() ? "25" : out;
    }

    private static List<Path> listPngs(Path dir) throws IOException {
        try (Stream<Path> s = Files.list(dir)) {
            List<Path> list = new ArrayList<>(s.filter(p -> p.toString().endsWith(".png")).toList());
            list.sort(Comparator.comparing(Path::toString));   // frame_000001, frame_000002, ...
            return list;
        }
    }

    /** Runs a command, returns its output, throws a readable error if it fails. */
    private static String run(List<String> command) throws IOException, InterruptedException {
        Process p;
        try {
            p = new ProcessBuilder(command).redirectErrorStream(true).start();
        } catch (IOException e) {
            throw new IOException("Could not start '" + command.get(0) + "'. Is FFmpeg installed and on the PATH?", e);
        }
        String output = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int code = p.waitFor();
        if (code != 0) {
            String tail = output.length() > 600 ? output.substring(output.length() - 600) : output;
            throw new IOException("FFmpeg failed (exit code " + code + "): " + tail);
        }
        return output;
    }

    private static void deleteRecursively(Path dir) {
        try (Stream<Path> s = Files.walk(dir)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        } catch (IOException ignored) { }
    }
}