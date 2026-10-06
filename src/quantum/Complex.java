package quantum;

/** Complex number: real + imaginary*i. Qubit amplitudes can be complex. */
public class Complex {
    public final double re, im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public Complex add(Complex o)  { return new Complex(re + o.re, im + o.im); }
    public Complex mul(Complex o)  { return new Complex(re * o.re - im * o.im, re * o.im + im * o.re); }
    public Complex scale(double k) { return new Complex(re * k, im * k); }

    /** |z|^2 = probability when z is an amplitude. */
    public double prob() { return re * re + im * im; }

    @Override
    public String toString() { return String.format("(%.3f%+.3fi)", re, im); }
}