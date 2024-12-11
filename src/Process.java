// Updated Process class
import java.util.*;

public class Process {
    private static final Set<Integer> existingProcessIDs = new HashSet<>();
    private final int processID;
    private final List<String> instructions;
    private int burstTime;
    private int programCounter;
    private final int[] memoryBounds;

    private Process(int processID, List<String> instructions, int programCounter, int[] memoryBounds, int burstTime) {
        this.processID = processID;
        this.instructions = instructions;
        this.programCounter = programCounter;
        this.memoryBounds = memoryBounds;
        this.burstTime = burstTime;
    }

    public static Process createProcess(int processID, List<String> instructions, int programCounter, int[] memoryBounds, int burstTime) {
        if (memoryBounds == null || memoryBounds.length != 2 || memoryBounds[0] >= memoryBounds[1]) {
            throw new IllegalArgumentException("Invalid memory bounds");
        }
        while (existingProcessIDs.contains(processID)) {
            processID++;
        }
        existingProcessIDs.add(processID);
        return new Process(processID, instructions, programCounter, memoryBounds, burstTime);
    }

    public void decreaseBurstTime(int n) {
        burstTime = Math.max(0, burstTime - n);
    }

    public int getProcessID() {
        return processID;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int[] getMemoryBounds() {
        return memoryBounds;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public String getNextInstruction() {
        return programCounter < instructions.size() ? instructions.get(programCounter++) : null;
    }

    public boolean isComplete() {
        return programCounter >= instructions.size();
    }
}
