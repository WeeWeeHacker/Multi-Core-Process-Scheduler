import java.util.*;
import java.io.*;

public class InstructionParser {

    private final File inputFile;

    public InstructionParser(File inputFile) {
        this.inputFile = inputFile;
    }

    public List<Process> parseInstructions() throws IOException {
        List<Process> processes = new ArrayList<>();
        try (Scanner scanner = new Scanner(inputFile)) {
            int processID = 1;
            int memoryStart = 0;
            int memorySize = 100;

            while (scanner.hasNextLine()) {
                List<String> instructions = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        instructions.add(line);
                    }
                }
                int burstTime = calculateBurstTime(instructions);
                int[] memoryBounds = {memoryStart, memoryStart + memorySize - 1};
                memoryStart += memorySize;
                processes.add(Process.createProcess(processID++, instructions, 0, memoryBounds, burstTime));
            }
        }
        return processes;
    }

    private int calculateBurstTime(List<String> instructions) {
        return instructions.size() * 10;
    }

    public String categorizeInstruction(String instruction) {
        if (instruction == null || instruction.isBlank()) return "Unknown";
        if (instruction.startsWith("assign")) return "Assignment";
        if (instruction.startsWith("print")) return "Print";
        if (instruction.matches("^(add|subtract|multiply|divide).*")) return "Arithmetic Operation";
        return "Unknown";
    }
}