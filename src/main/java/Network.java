import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Arrays;

public class Network {

    public static void main(String[] args) {
        // Defining the network size.
        int NETWORK_SIZE = 5;
        int QUBITS_PER_NODE = 2;

        // Defining the message to send.
        double MESSAGE = .21;

        // Setting up the Quantum Program.
        Program program = new Program(1 + QUBITS_PER_NODE * NETWORK_SIZE);

        // Concatenating multiple Quantum Teleporters.
        for (int node = 0; node < NETWORK_SIZE; node++) {

            // Storing the current node positions
            int first = QUBITS_PER_NODE * node + 0;
            int second = QUBITS_PER_NODE * node + 1;
            int third = QUBITS_PER_NODE * node + 2;

            // Initilizing steps in the Quantum Program.
            Step step1 = new Step();
            Step step2 = new Step();
            Step step3 = new Step();
            Step step4 = new Step();
            Step step5 = new Step();
            Step step6 = new Step();
            Step step7 = new Step();

            // Putting the second into the superposition state.
            step1.addGate(new Hadamard(second));
            step2.addGate(new Cnot(second, third));
            step3.addGate(new Cnot(first, second));
            step4.addGate(new Hadamard(first));
            step5.addGate(new Measurement(first));
            step5.addGate(new Measurement(second));
            step6.addGate(new Cnot(second, third));
            step7.addGate(new Cz(first, third));

            // Setting up the Quantum Program.
            program.addStep(step1);
            program.addStep(step2);
            program.addStep(step3);
            program.addStep(step4);
            program.addStep(step5);
            program.addStep(step6);
            program.addStep(step7);

        }

        // Altering the initial value of the first Qubit.
        program.initializeQubit(0, MESSAGE);

        // Connecting to a local simulator.
        SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();

        // Running the Program.
        Result res = sqee.runProgram(program);

        // Measuring a single Qubit.
        Qubit[] qubits = res.getQubits();
        Arrays.asList(qubits).forEach(q -> Network.measure(q));

        // Rendering circuit as an image.
        Renderer.renderProgram(program);
        Renderer.showProbabilities(program, 1000);
    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }
}
