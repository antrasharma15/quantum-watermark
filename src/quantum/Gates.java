package quantum;

/** Standard single-qubit gates as 2x2 matrices. */
public class Gates {
    private static final double S = 1 / Math.sqrt(2);

    /** X gate = quantum NOT: swaps |0> and |1>. */
    public static final Complex[][] X = {
            { new Complex(0, 0), new Complex(1, 0) },
            { new Complex(1, 0), new Complex(0, 0) }
    };

    /** Hadamard gate: puts |0> or |1> into 50-50 superposition. */
    public static final Complex[][] H = {
            { new Complex(S, 0), new Complex(S, 0) },
            { new Complex(S, 0), new Complex(-S, 0) }
    };
}