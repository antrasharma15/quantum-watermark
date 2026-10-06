package quantum;

import java.util.Random;

/** One qubit: state = alpha|0> + beta|1>. */
public class Qubit {
    private Complex alpha, beta;
    private static final Random RNG = new Random();

    /** New qubit starts as |0>. */
    public Qubit() {
        this.alpha = new Complex(1, 0);
        this.beta  = new Complex(0, 0);
    }

    /** Qubit starting as |0> or |1>. */
    public Qubit(int bit) {
        this();
        if (bit == 1) { alpha = new Complex(0, 0); beta = new Complex(1, 0); }
    }

    /** Apply a 2x2 gate matrix [[a,b],[c,d]] to this qubit. */
    public void apply(Complex[][] g) {
        Complex newAlpha = g[0][0].mul(alpha).add(g[0][1].mul(beta));
        Complex newBeta  = g[1][0].mul(alpha).add(g[1][1].mul(beta));
        alpha = newAlpha;
        beta  = newBeta;
    }

    /** Measurement: randomly give 0 or 1 using |alpha|^2, then COLLAPSE. */
    public int measure() {
        int result = (RNG.nextDouble() < alpha.prob()) ? 0 : 1;
        alpha = new Complex(result == 0 ? 1 : 0, 0);
        beta  = new Complex(result == 1 ? 1 : 0, 0);
        return result;
    }

    public double probZero() { return alpha.prob(); }
    public double probOne()  { return beta.prob(); }

    @Override
    public String toString() {
        return alpha + "|0> + " + beta + "|1>   P(0)=" + String.format("%.2f", probZero())
                + " P(1)=" + String.format("%.2f", probOne());
    }
}