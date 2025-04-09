import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Random;
import java.util.Arrays;

public class Grover {

    static final int DIMENSIONS = 3;
    static final int SOLUTION = 2;

    // Connecting to a local simulator.
    static QuantumExecutionEnvironment simulator = new SimpleQuantumExecutionEnvironment();

    public static void main(String[] args) {

        // Estimating the optimal amount of iterations.
        int N = 1 << DIMENSIONS;
        double runs = Math.PI * Math.sqrt(N) / 4;
        System.out.println("Qubits | Qubits: " + DIMENSIONS + " Encoded: " + Math.pow(2, DIMENSIONS) + " Solution: " + SOLUTION + " Runs: " + runs);

        // Initializing a Quantum Program.
        Program p = new Program(DIMENSIONS);

        // Applying a Hadamard gate to all the Qubits.
        Step s0 = new Step();
        for (int i = 0; i < DIMENSIONS; i++) {
            s0.addGate(new Hadamard(i));
        }
        p.addStep(s0);

        // Creating an unknown Oracle.
        Oracle oracle = createOracle();
        oracle.setCaption("O");

        // Creating a Diffusor Oracle.
        Oracle difOracle = createDiffusor();
        difOracle.setCaption("D");

        // Applying the Oracle and Diffusor multiple times.
        for (int i = 1; i < runs; i++) {
            Step s1 = new Step("Oracle " + i);
            s1.addGate(oracle);
            Step s2 = new Step("Diffusion " + i);
            s2.addGate(difOracle);
            Step s3 = new Step("Prob " + i);
            s3.addGate(new ProbabilitiesGate(0));
            p.addStep(s1);
            p.addStep(s2);
            p.addStep(s3);
        }

        // Initializing a Quantum Program.
        Result res = simulator.runProgram(p);

        // Measuring the probability distribution at the end.
        Complex[] probability = res.getProbability();

        // Measuring the probability distribution in the middle.
        System.out.println("----------------------------------------------------");
        for (int i = 1; i < runs; i++) {
            Complex[] ip0 = res.getIntermediateProbability(3 * i);
            System.out.println("Correct Solution Probability after step " + i + ": " + ip0[SOLUTION].abssqr());
        }
        System.out.println("----------------------------------------------------");

        // Measuring the probability distribution in the middle.
        for (int i = 1; i < runs; i++) {
            System.out.println("Probability distribution at step: " + i);
            Qubit[] qubits = res.getQubits();
            Complex[] ip0 = res.getIntermediateProbability(3 * i);
            Arrays.asList(ip0).forEach(q -> System.out.println("p: " + q.abssqr()));
            System.out.println("----------------------------------------------------");
        }

    }

    // Creating an unknown Oracle gate.
    private static Oracle createOracle() {
        int N = 1 << DIMENSIONS;
        Complex[][] matrix = new Complex[N][N];
        for (int i = 0; i < N;i++) {
            for (int j = 0 ; j < N; j++) {
                if (i != j) {
                    matrix[i][j] = Complex.ZERO;
                } else {
                    if (i == SOLUTION) {
                        matrix[i][j] = Complex.ONE.mul(-1);
                    } else {
                        matrix[i][j] = Complex.ONE;
                    }
                }
            }
        }
        return new Oracle(matrix);
    }

    // Creating a Diffusor Oracle that interferes the probability
    // distribution with the optimal solution of the problem.
    private static Oracle createDiffusor() {
        int N = 1 << DIMENSIONS;
        double N2 = 2./N;
        Complex[][] matrix = new Complex[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = (i == j ? new Complex(N2 - 1) : new Complex(N2));
            }
        }
        return new Oracle(matrix);
    }
}
