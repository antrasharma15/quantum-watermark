package quantum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Usage:  java quantum.Step4Test [inputImage] [message]
 * With no arguments it creates a sample image and uses a default message.
 */
public class Step4Test {
    public static void main(String[] args) throws Exception {
        String message = args.length > 1 ? args[1] : "hy i am antra sharma";

        BufferedImage original;
        if (args.length > 0) {
            original = ImageIO.read(new File(args[0]));
            if (original == null) throw new IllegalArgumentException("Could not read image: " + args[0]);
        } else {
            original = makeSampleImage(300, 200);
        }
        System.out.println("Image size : " + original.getWidth() + " x " + original.getHeight());
        System.out.println("Capacity   : " + ImageWatermarker.capacityBytes(original) + " characters (bytes)");

        System.out.println("\n--- Test 1: Embed and save as PNG (lossless) ---");
        BufferedImage marked = ImageWatermarker.embed(original, message);
        ImageIO.write(marked, "png", new File("watermarked.png"));
        System.out.println("Saved watermarked.png");
        System.out.printf("PSNR       : %.2f dB (above 40 dB = invisible to the eye)%n",
                ImageWatermarker.psnr(original, marked));

        System.out.println("\n--- Test 2: Reload from file and extract ---");
        BufferedImage reloaded = ImageIO.read(new File("watermarked.png"));
        System.out.println("Extracted  : \"" + ImageWatermarker.extract(reloaded) + "\"");

        System.out.println("\n--- Test 3: Save as JPEG (lossy) and try to extract ---");
        ImageIO.write(marked, "jpg", new File("watermarked.jpg"));
        BufferedImage jpg = ImageIO.read(new File("watermarked.jpg"));
        try {
            System.out.println("Extracted  : \"" + ImageWatermarker.extract(jpg) + "\"");
        } catch (Exception e) {
            System.out.println("Extraction FAILED: " + e.getMessage());
        }

        System.out.println("\n--- Test 4: Message too large ---");
        try {
            ImageWatermarker.embed(original, "x".repeat(ImageWatermarker.capacityBytes(original) + 1));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected   : " + e.getMessage());
        }
    }

    /** Simple colourful gradient so we have something to test on. */
    private static BufferedImage makeSampleImage(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                img.setRGB(x, y, ((x * 255 / w) << 16) | ((y * 255 / h) << 8) | ((x + y) * 255 / (w + h)));
        return img;
    }
}