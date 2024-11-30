import java.util.*;
import java.io.*;

public class InstructionParser {

    private File inputFile;

    public InstructionParser(File inputFile) {
        this.inputFile = inputFile;
    }


    public List<Process> parseInstructions() throws IOException {
        List<Process> processes = new ArrayList<>();
        Scanner scanner = new Scanner(inputFile);
        int processID = 1; //?????
        while (scanner.hasNextLine()) {
            String[] program = scanner.nextLine().split(";");
            List<String> instructions = Arrays.asList(program);
            processes.add(Process.createProcess(processID++,instructions,0, new int[] { 0, 100 })); //logic issue
        }
        scanner.close();
        return processes;
    }

    public String categorizeInstruction(String instruction) {
        if (instruction.startsWith("assign")) return "Assignment";
        if (instruction.startsWith("print")) return "Print";
        if (instruction.startsWith("add") || instruction.startsWith("subtract") ||
                instruction.startsWith("multiply") || instruction.startsWith("divide")) return "Arithmetic Operation";
        return "Unknown";
    }
}
