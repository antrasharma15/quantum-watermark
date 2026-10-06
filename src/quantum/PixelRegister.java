package quantum;

/**
 * 8 qubits that together store ONE gray pixel (0-255).
 * NEQR idea: pixel value 150 -> binary 10010110 -> |c7 c6 c5 c4 c3 c2 c1 c0>
 * qubits[0] = c0 (last / least significant), qubits[7] = c7 (first / most significant)
 */
public class PixelRegister {
    private final Qubit[] qubits = new Qubit[8];

    /** ENCODE: pixel value -> 8 qubits. */
    public PixelRegister(int pixelValue) {
        if (pixelValue < 0 || pixelValue > 255)
            throw new IllegalArgumentException("Pixel must be 0-255, got " + pixelValue);
        for (int i = 0; i < 8; i++) {
            int bit = (pixelValue >> i) & 1;   // pick i-th bit
            qubits[i] = new Qubit(bit);        // |0> or |1>
        }
    }

    /** Apply X gate (quantum NOT) on qubit number 'position' (0 = last qubit). */
    public void applyX(int position) {
        qubits[position].apply(Gates.X);
    }

    /** DECODE: measure all 8 qubits -> rebuild the pixel value. */
    public int measure() {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            value |= qubits[i].measure() << i;  // put measured bit back at place i
        }
        return value;
    }

    /** Measure ONLY one qubit (e.g. c0) and return its bit. The other qubits are untouched. */
    public int measureQubit(int position) {
        return qubits[position].measure();
    }

    /** Peek at one qubit's value (only valid if it is |0> or |1>), without collapse. */
    public int peekBit(int position) {
        return qubits[position].probOne() > 0.5 ? 1 : 0;
    }

    /** Print as |10010110> style. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 7; i >= 0; i--) sb.append(peekBit(i));
        return sb.append(">").toString();
    }
}