package quantum;

import java.io.File;
import java.util.List;

/**
 * Usage:  java quantum.Step6Test [inputVideo] [message] [interval]
 * With no arguments it creates a 5-second sample video first.
 */
public class Step6test {
    public static void main(String[] args) throws Exception {
        String message = args.length > 1 ? args[1] : "hy i am antra sharma";
        int interval   = args.length > 2 ? Integer.parseInt(args[2]) : 5;

        File input;
        if (args.length > 0) {
            input = new File(args[0]);
        } else {
            input = new File("sample.mp4");
            System.out.println("No video given. Creating sample.mp4 ...");
            Process p = new ProcessBuilder(List.of("ffmpeg", "-y", "-hide_banner", "-loglevel", "error",
                    "-f", "lavfi", "-i", "testsrc=duration=5:size=320x240:rate=30",
                    "-c:v", "libx264", "-pix_fmt", "yuv420p", input.getPath()))
                    .redirectErrorStream(true).start();
            p.getInputStream().readAllBytes();
            p.waitFor();
        }
        File output = new File("watermarked_video.mkv");

        System.out.println("\n--- Test 1: Embed into video ---");
        long t0 = System.currentTimeMillis();
        Videowatermarker.Result r = Videowatermarker.embed(input, output, message, interval, System.out::println);
        System.out.println("Total frames : " + r.totalFrames());
        System.out.println("Marked frames: " + r.markedFrames() + " (every " + interval + "-th frame)");
        System.out.println("Time taken   : " + (System.currentTimeMillis() - t0) / 1000.0 + " s");
        System.out.println("Saved        : " + output.getAbsolutePath());

        System.out.println("\n--- Test 2: Extract from the watermarked video ---");
        System.out.println("Extracted    : \"" + Videowatermarker.extract(output, System.out::println) + "\"");

        System.out.println("\n--- Test 3: Compress to lossy MP4 and try to extract ---");
        File lossy = new File("compressed.mp4");
        Videowatermarker.compressToMp4(output, lossy);
        try {
            System.out.println("Extracted    : \"" + Videowatermarker.extract(lossy, s -> { }) + "\"");
        } catch (Exception e) {
            System.out.println("Extraction FAILED: " + e.getMessage());
        }
    }
}