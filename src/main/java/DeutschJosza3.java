import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Random;
import java.util.Arrays;

public class DeutschJosza3 {

    static final int N = 3;
    static final int RUNS = 3;

    public static void main(String[] args) {
        // Connecting to a local simulator.
        QuantumExecutionEnvironment simulator = new SimpleQuantumExecutionEnvironment();

        // Running the same program multiple times.
        for (int i = 0; i < RUNS; i++) {
            // Setting up the Quantum Program.
            Program program = new Program(N + 1);

            // Initilizing steps in the Quantum Program.
            Step step1 = new Step();
            Step step2 = new Step();
            Step step3 = new Step();
            Step step4 = new Step();

            // Adding a not gate to only the last Qubit, which
            // is the helper Qubit.
            step1.addGate(new X(N));

            // Adding a Hadamard gate to all the Qubits to put
            // them into a Superposition state.
            for (int j = 0; j < N+1; j++) {
                step2.addGate(new Hadamard(j));
            }

            // Adding a random Oracle gate to the Program, so
            // that we can test the robustness of the Deutsch-Jozsa
            // algorithm in front of randomness.
            Oracle oracle = createOracle();
            step3.addGate(oracle);

            // Applying a Hadamard gate to all the Qubits except
            // the last one, which is discarded, since was just a helper Qubit.
            for (int j = 0; j < N; j++) {
                step4.addGate(new Hadamard(j));
            }

            // Adding gates to the Quantum Program.
            program.addStep(step1);
            program.addStep(step2);
            program.addStep(step3);
            program.addStep(step4);

            // Running the Program.
            Result res = simulator.runProgram(program);

            // Measuring a single Qubit.
            Qubit[] qubits = res.getQubits();
            Arrays.asList(qubits).forEach(q -> DeutschJosza3.measure(q));

            // Rendering circuit as an image.
            if (i == RUNS - 1) {
                Renderer.renderProgram(program);
                Renderer.showProbabilities(program, 1000);
            }
        }


    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }

    private static Oracle createOracle() {
        // Initializing an empty matrix of size NxN.
        int dim = 2 << N;
        int half = dim / 2;
        Complex[][] matrix = new Complex[dim][dim];

        // Building the Oracle based on a random Number.
        Random random = new Random();
        int choice = random.nextInt(2);
        String type = "balanced";
        if (choice == 0) {
            type = "constant";
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Qubit | Function: "+ choice + " Type: " + type);

        // Generating a matrix that should represent the Oracle that is unknown
        // to the Program executing the Deutsch-Jozsa Algorithm.
        switch (choice) {

            // Building a constant function that returns 1 every time.
            case 0:
                for (int i = 0; i < dim; i ++) {
                    matrix[i][i] = Complex.ONE;
                }
                return new Oracle(matrix);

            // Creating a balanced function that returns 1 50% of the time.
            case 1:
                for (int i = 0; i < dim; i ++) {
                    if (i % 2 == 0) {
                        matrix[i][i] = Complex.ONE;
                    } else {
                        if (i < half) {
                            matrix[i][i + half] = Complex.ONE;
                        } else{
                            matrix[i][i - half] = Complex.ONE;
                        }
                    }
                }
                return new Oracle(matrix);

            // Invalid Oracle index.
            default:
                throw new IllegalArgumentException("Wrong index in oracle: " + choice);
        }
    }
}
