import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Arrays;

public class Teleport1 {

    public static void main(String[] args) {
        // Initilizing steps in the Quantum Program.
        Step step1 = new Step();
        Step step2 = new Step();
        Step step3 = new Step();
        Step step4 = new Step();
        Step step5 = new Step();
        Step step6 = new Step();
        Step step7 = new Step();

        // Putting the second into the superposition state.
        step1.addGate(new Hadamard(1));

        // Creating a Quantum Entanglement between the last 2 Qubits.
        step2.addGate(new Cnot(1,2));

        // Creating a Quantum Entanglement between the first 2 Qubits.
        step3.addGate(new Cnot(0,1));

        // Putting the first into the superposition state.
        step4.addGate(new Hadamard(0));

        // Measuring the first 2 Qubits.
        step5.addGate(new Measurement(0));
        step5.addGate(new Measurement(1));

        // Adding an X-Gate if the result of the second Qubit is 1.
        step6.addGate(new Cnot(1,2));

        // Adding a Z-Gate if the result of the first Qubit is 1.
        step7.addGate(new Cz(0,2));

        // Setting up the Quantum Program.
        Program program = new Program(3);
        program.addStep(step1);
        program.addStep(step2);
        program.addStep(step3);
        program.addStep(step4);
        program.addStep(step5);
        program.addStep(step6);
        program.addStep(step7);

        // Altering the initial value of the first Qubit.
        program.initializeQubit(0, 0);

        // Connecting to a local simulator.
        SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();

        // Running the Program.
        Result res = sqee.runProgram(program);

        // Measuring a single Qubit.
        Qubit[] qubits = res.getQubits();
        Arrays.asList(qubits).forEach(q -> Teleport1.measure(q));

        // Rendering circuit as an image.
        Renderer.renderProgram(program);
        Renderer.showProbabilities(program, 1000);
    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }
}
