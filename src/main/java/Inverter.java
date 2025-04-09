import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Arrays;

public class Inverter {

    public static void main(String[] args) {
        // Initilizing steps in the Quantum Program.
        Step step1 = new Step();
        Step step2 = new Step();
        Step step3 = new Step();

        // Adding 3 consecutive CNOT gates.
        step1.addGate(new Cnot(0,1));
        step2.addGate(new Cnot(1,0));
        step3.addGate(new Cnot(0,1));

        // Setting up the Quantum Program.
        Program program = new Program(2);
        program.addStep(step1);
        program.addStep(step2);
        program.addStep(step3);

        // Initializing the states to swap.
        program.initializeQubit(0, .2);
        program.initializeQubit(1, .5);

        // Connecting to a local simulator.
        SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();

        // Running the Program.
        Result res = sqee.runProgram(program);

        // Measuring a single Qubit.
        Qubit[] qubits = res.getQubits();
        Arrays.asList(qubits).forEach(q -> Inverter.measure(q));

        // Rendering circuit as an image.
        Renderer.renderProgram(program);
        Renderer.showProbabilities(program, 1000);
    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }
}
