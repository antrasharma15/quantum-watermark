package quantum;

public class Step2Test {
    public static void main(String[] args) {
        System.out.println("--- Test 1: Encode 150 in 8 qubits ---");
        PixelRegister p = new PixelRegister(150);
        System.out.println("Pixel 150 -> " + p);
        System.out.println("Decoded back: " + p.measure());

        System.out.println("\n--- Test 2: X gate on the last qubit (c0) ---");
        PixelRegister q = new PixelRegister(150);
        System.out.println("Before  : " + q + " = 150");
        q.applyX(0);
        System.out.println("After X : " + q + " = " + q.measure());

        System.out.println("\n--- Test 3: Encode/decode check for all 256 values ---");
        boolean allOk = true;
        for (int v = 0; v <= 255; v++) {
            if (new PixelRegister(v).measure() != v) { allOk = false; System.out.println("FAIL at " + v); }
        }
        System.out.println(allOk ? "All 256 values encoded and decoded correctly" : "Something went wrong");

        System.out.println("\n--- Test 4: What happens if X is applied to the first (MSB) qubit? ---");
        PixelRegister r = new PixelRegister(150);
        r.applyX(7);
        System.out.println("150 with c7 flipped -> " + r.measure() + " (a huge change!)");
    }
}