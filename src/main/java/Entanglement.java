import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Arrays;

public class Entanglement {

    public static void main(String[] args) {
        // Setting up the Quantum Program.
        Program program = new Program(2);
        Step step1 = new Step();
        step1.addGate(new Cnot(0,1));
        program.addStep(step1);

        // Connecting to a local simulator.
        SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();

        // Running the Program.
        Result res = sqee.runProgram(program);

        // Measuring a single Qubit.
        Qubit[] qubits = res.getQubits();
        Arrays.asList(qubits).forEach(q -> Entanglement.measure(q));

        // Rendering circuit as an image.
        Renderer.renderProgram(program);
    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }
}
