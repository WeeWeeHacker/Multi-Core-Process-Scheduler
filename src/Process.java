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
    public static Process createProcess(int processID, List<String> instructions, int programCounter, int[] memoryBounds,int burstTime) {
        int newProcessID = processID;

        while (existingProcessIDs.contains(newProcessID)) {
            newProcessID++;
        }

        existingProcessIDs.add(newProcessID);

        return new Process(newProcessID, instructions, programCounter, memoryBounds,burstTime);
    }

    public void decreaseBurstTime(int n){
        if ( n > BurstTime ){
            BurstTime = 0;
        }
        else {
            BurstTime -= n;
        }

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
