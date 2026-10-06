package quantum;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Test 1: X gate (quantum NOT) ---");
        Qubit q = new Qubit();            // |0>
        System.out.println("Start    : " + q);
        q.apply(Gates.X);
        System.out.println("After X  : " + q);

        System.out.println("\n--- Test 2: Hadamard (superposition) ---");
        Qubit h = new Qubit();
        h.apply(Gates.H);
        System.out.println("After H  : " + h);

        System.out.println("\n--- Test 3: Measure H-qubit 1000 times ---");
        int zeros = 0, ones = 0;
        for (int i = 0; i < 1000; i++) {
            Qubit t = new Qubit();
            t.apply(Gates.H);
            if (t.measure() == 0) zeros++; else ones++;
        }
        System.out.println("0 aaya: " + zeros + " baar, 1 aaya: " + ones + " baar");

        System.out.println("\n--- Test 4: Collapse ---");
        Qubit c = new Qubit();
        c.apply(Gates.H);
        System.out.println("Measure se pehle : " + c);
        int r = c.measure();
        System.out.println("Result           : " + r);
        System.out.println("Measure ke baad  : " + c);
    }
}