import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Arrays;

public class Bell2 {

    public static void main(String[] args) {
        // Setting up the Quantum Program.
        Program program = new Program(2);

        // Putting the first into the superposition state.
        Step step1 = new Step();
        step1.addGate(new Hadamard(0));
        program.addStep(step1);

        // Creating a Quantum Entanglement between the 2 Qubits.
        Step step2 = new Step();
        step2.addGate(new Cnot(0,1));
        program.addStep(step2);

        // Adding an additional X gate to the second Qubit.
        Step step3 = new Step();
        step3.addGate(new X(1));
        program.addStep(step3);

        // Connecting to a local simulator.
        SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();

        // Running the Program.
        Result res = sqee.runProgram(program);

        // Measuring a single Qubit.
        Qubit[] qubits = res.getQubits();
        Arrays.asList(qubits).forEach(q -> Bell2.measure(q));

        // Rendering circuit as an image.
        Renderer.renderProgram(program);
        Renderer.showProbabilities(program, 1000);
    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }
}
