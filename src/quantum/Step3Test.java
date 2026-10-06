package quantum;

public class Step3Test {
    public static void main(String[] args) {
        System.out.println("--- Test 1: Embed one bit ---");
        System.out.println("Pixel 150 (c0=0), embed 1 -> " + QuantumWatermarker.embedBit(150, 1));
        System.out.println("Pixel 150 (c0=0), embed 0 -> " + QuantumWatermarker.embedBit(150, 0));

        System.out.println("\n--- Test 2: The 255 case (c0 is already 1) ---");
        System.out.println("Pixel 255, embed 1 -> " + QuantumWatermarker.embedBit(255, 1) + " (no change needed)");
        System.out.println("Pixel 255, embed 0 -> " + QuantumWatermarker.embedBit(255, 0) + " (flipped)");

        System.out.println("\n--- Test 3: Extract the bit back ---");
        int marked = QuantumWatermarker.embedBit(150, 1);
        System.out.println("Marked pixel " + marked + " -> extracted bit = " + QuantumWatermarker.extractBit(marked));

        System.out.println("\n--- Test 4: Hide a text message in 16 pixels ---");
        String message = "HI";
        int[] pixels = {150, 200, 255, 0, 17, 99, 128, 64, 33, 250, 12, 180, 77, 5, 222, 101};
        int[] marked16 = QuantumWatermarker.embedMessage(pixels, message);

        int maxChange = 0;
        for (int i = 0; i < pixels.length; i++) {
            maxChange = Math.max(maxChange, Math.abs(pixels[i] - marked16[i]));
        }
        System.out.println("Original pixels : " + java.util.Arrays.toString(pixels));
        System.out.println("Watermarked     : " + java.util.Arrays.toString(marked16));
        System.out.println("Max change in any pixel: " + maxChange);
        System.out.println("Extracted message: " + QuantumWatermarker.extractMessage(marked16, message.length()));
    }
}