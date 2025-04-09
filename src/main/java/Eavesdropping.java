import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Arrays;

public class Eavesdropping {

    public static void main(String[] args) {
        // Initilizing steps in the Quantum Program.
        Step step1 = new Step();
        Step step2 = new Step();
        Step step3 = new Step();
        Step step4 = new Step();

        // Applying the X-Gate to 2 Qubits.
        step1.addGate(new X(0));
        step1.addGate(new X(3));

        // Applying the Hadamard gate once by Alice before sending the message.
        step2.addGate(new Hadamard(0));
        step2.addGate(new Hadamard(1));

        // Eve applies random Hadamard gates to try to get the message.
        step3.addGate(new Hadamard(0));
        step3.addGate(new Hadamard(3));

        // Eve measures the Qubits to get one result.
        step4.addGate(new Measurement(0));
        step4.addGate(new Measurement(1));
        step4.addGate(new Measurement(2));
        step4.addGate(new Measurement(3));

        // Setting up the Quantum Program.
        Program program = new Program(4);
        program.addStep(step1);
        program.addStep(step2);
        program.addStep(step3);
        program.addStep(step4);

        // Altering the initial value of the first Qubit.
        program.initializeQubit(0, 1);
        program.initializeQubit(1, 1);
        program.initializeQubit(2, 1);
        program.initializeQubit(3, 1);

        // Connecting to a local simulator.
        SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();

        // Running the Program.
        Result res = sqee.runProgram(program);

        // Measuring a single Qubit.
        Qubit[] qubits = res.getQubits();
        Arrays.asList(qubits).forEach(q -> Eavesdropping.measure(q));

        // Rendering circuit as an image.
        Renderer.renderProgram(program);
        Renderer.showProbabilities(program, 1000);

    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }
}
