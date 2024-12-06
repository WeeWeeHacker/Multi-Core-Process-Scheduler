import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Process {
private static final Set<Integer> existingProcessIDs = new HashSet<>();
private final int processID ;
private List<String> instructions;
private int BurstTime;
private int programCounter;
private int  memoryBounds [];

    private Process(int processID,List<String> instructions,int programCounter, int[] memoryBounds,int BurstTime ) {
        this.processID = processID;
        this.instructions = instructions;
        this.programCounter = programCounter;
        this.memoryBounds = memoryBounds;
        this.BurstTime = BurstTime;
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

    public void decreaseBurstTime(int n){
        BurstTime = Math.max(0,getBurstTime()-n);

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
        return BurstTime;
    }


    public String getNextInstruction() {
        if (programCounter < instructions.size()) {
            return instructions.get(programCounter++);
        } else {
            return null;
        }
    }

    public void markAsComplete() {
        programCounter += instructions.size();
    }

    public boolean isComplete() {
        return programCounter >= instructions.size();
    }
}
