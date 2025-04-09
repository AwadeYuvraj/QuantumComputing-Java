import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;
import org.redfx.strangefx.render.Renderer;

import java.util.Arrays;

public class Adder100 {

    public static void main(String[] args) {
        // Initilizing steps in the Quantum Program.
        Step step1 = new Step();
        Step step2 = new Step();
        Step step3 = new Step();
        Step step4 = new Step();

        // Creating the first adder.
        step1.addGate(new Toffoli(0,1,2));
        step2.addGate(new Cnot(0,1));

        // Creating the second adder.
        step3.addGate(new Toffoli(2,3,4));
        step4.addGate(new Cnot(2,3));

        // Setting up the Quantum Program.
        Program program = new Program(5);
        program.addStep(step1);
        program.addStep(step2);
        program.addStep(step3);
        program.addStep(step4);

        // Initializing the states to swap.
        program.initializeQubit(0, 0);
        program.initializeQubit(1, 1);
        program.initializeQubit(3, 1);

        // Connecting to a local simulator.
        SimpleQuantumExecutionEnvironment sqee = new SimpleQuantumExecutionEnvironment();

        // Running the Program.
        Result res = sqee.runProgram(program);

        // Measuring a single Qubit.
        Qubit[] qubits = res.getQubits();
        Arrays.asList(qubits).forEach(q -> Adder100.measure(q));

        // Rendering circuit as an image.
        Renderer.renderProgram(program);
        Renderer.showProbabilities(program, 1000);
    }

    private static void measure(Qubit q) {
        System.out.println("Qubit | Probability: "+q.getProbability()+", Mesured: "+ q.measure());
    }
}
