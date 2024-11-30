import java.util.List;

public class Process {
private int processID = 1;
private List<String> instructions;
private int programCounter;
private int  memoryBounds [];

    public Process(List<String> instructions,int programCounter, int[] memoryBounds) {
        this.processID = processID++;
        this.instructions = instructions;
        this.programCounter = programCounter;
        this.memoryBounds = memoryBounds;
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
