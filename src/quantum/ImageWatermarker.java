package quantum;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

/**
 * Applies quantum-LSB watermarking to a real image.
 * Layout: [32-bit message length in bytes][message bits ...]
 * One bit is hidden per pixel, in the BLUE channel's last qubit (c0).
 * Pixels are visited in row-major order (left to right, top to bottom).
 */
public class ImageWatermarker {
    private static final int HEADER_BITS = 32;

    /** Max number of message bytes this image can hold. */
    public static int capacityBytes(BufferedImage img) {
        long totalBits = (long) img.getWidth() * img.getHeight();
        return (int) Math.max(0, (totalBits - HEADER_BITS) / 8);
    }

    /** Returns a NEW watermarked image; the original is not modified. */
    public static BufferedImage embed(BufferedImage src, String message) {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        if (data.length == 0)
            throw new IllegalArgumentException("Message is empty");
        if (data.length > capacityBytes(src))
            throw new IllegalArgumentException("Message too large: " + data.length
                    + " bytes, image can hold " + capacityBytes(src) + " bytes");

        int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        out.getGraphics().drawImage(src, 0, 0, null);   // copy source into out

        int bitIndex = 0;
        // 1) header: message length, 32 bits, MSB first
        for (int i = 31; i >= 0; i--) {
            writeBit(out, bitIndex++, (data.length >> i) & 1);
        }
        // 2) message bits, MSB first
        for (byte b : data) {
            for (int i = 7; i >= 0; i--) {
                writeBit(out, bitIndex++, (b >> i) & 1);
            }
        }
        return out;
    }

    /** Reads the hidden message back from a watermarked image. */
    public static String extract(BufferedImage img) {
        int bitIndex = 0;
        int length = 0;
        for (int i = 0; i < HEADER_BITS; i++) {
            length = (length << 1) | readBit(img, bitIndex++);
        }
        if (length <= 0 || length > capacityBytes(img))
            throw new IllegalStateException("No valid watermark found (bad length header: " + length + ")");

        byte[] data = new byte[length];
        for (int j = 0; j < length; j++) {
            int value = 0;
            for (int i = 0; i < 8; i++) {
                value = (value << 1) | readBit(img, bitIndex++);
            }
            data[j] = (byte) value;
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    /** Embed one bit into the blue channel of the n-th pixel. */
    private static void writeBit(BufferedImage img, int n, int bit) {
        int x = n % img.getWidth(), y = n / img.getWidth();
        int rgb  = img.getRGB(x, y);
        int blue = rgb & 0xFF;
        int newBlue = QuantumWatermarker.embedBit(blue, bit);   // quantum step
        img.setRGB(x, y, (rgb & 0xFFFFFF00) | newBlue);
    }

    /** Read one bit from the blue channel of the n-th pixel. */
    private static int readBit(BufferedImage img, int n) {
        int x = n % img.getWidth(), y = n / img.getWidth();
        int blue = img.getRGB(x, y) & 0xFF;
        return QuantumWatermarker.extractBit(blue);              // quantum step
    }

    /** PSNR in dB between two images. Higher = more similar (above ~40 dB is invisible). */
    public static double psnr(BufferedImage a, BufferedImage b) {
        double sum = 0;
        int w = a.getWidth(), h = a.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = a.getRGB(x, y), q = b.getRGB(x, y);
                for (int shift = 0; shift <= 16; shift += 8) {
                    int d = ((p >> shift) & 0xFF) - ((q >> shift) & 0xFF);
                    sum += d * d;
                }
            }
        }
        double mse = sum / (3.0 * w * h);
        return mse == 0 ? Double.POSITIVE_INFINITY : 10 * Math.log10(255.0 * 255.0 / mse);
    }
}