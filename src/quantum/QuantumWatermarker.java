package quantum;

/**
 * Quantum-LSB watermarking (simulated).
 * Each pixel is stored as 8 qubits (NEQR style). One watermark bit is hidden
 * in the last qubit (c0) of each pixel.
 */
public class QuantumWatermarker {

    /** Embed ONE bit into one pixel. Returns the new pixel value. */
    public static int embedBit(int pixel, int watermarkBit) {
        PixelRegister reg = new PixelRegister(pixel);   // encode pixel -> 8 qubits
        int currentBit = reg.peekBit(0);                // what is c0 now?
        if (currentBit != watermarkBit) {
            reg.applyX(0);                              // flip c0 only if it differs
        }
        return reg.measure();                           // decode qubits -> pixel
    }

    /** Extract ONE bit from one pixel by measuring its last qubit. */
    public static int extractBit(int pixel) {
        PixelRegister reg = new PixelRegister(pixel);
        return reg.measureQubit(0);
    }

    /** Embed a text message into an array of pixels (8 bits per character). */
    public static int[] embedMessage(int[] pixels, String message) {
        int totalBits = message.length() * 8;
        if (totalBits > pixels.length)
            throw new IllegalArgumentException("Need " + totalBits + " pixels, have " + pixels.length);

        int[] result = pixels.clone();
        for (int i = 0; i < totalBits; i++) {
            int ch  = message.charAt(i / 8);            // which character
            int bit = (ch >> (7 - i % 8)) & 1;          // which bit of it (MSB first)
            result[i] = embedBit(pixels[i], bit);
        }
        return result;
    }

    /** Extract a text message of the given length from the pixels. */
    public static String extractMessage(int[] pixels, int length) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < length; c++) {
            int ch = 0;
            for (int b = 0; b < 8; b++) {
                ch = (ch << 1) | extractBit(pixels[c * 8 + b]);
            }
            sb.append((char) ch);
        }
        return sb.toString();
    }
}