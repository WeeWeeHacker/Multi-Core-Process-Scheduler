import java.util.*;

class Scheduler {
    private Queue<Process> processQueue;
    private static final int quantum = 2;

    public Scheduler(List<Process> processes) {
        this.processQueue = new LinkedList<>(processes); //?Process list probably belongs in master-core?
    }

    public void addProcess(Process Process){
        processQueue.offer(Process);
    }

    public Process scheduleNext() {
        if (processQueue.isEmpty()) {
            return null; // No processes left to schedule
        }
        Process currentProcess = processQueue.poll();
        for (int i = 0; i < quantum; i++) {
            if (!currentProcess.isComplete()) {
                String instruction = currentProcess.getNextInstruction();
                System.out.println("Executing instruction: " + instruction + " from Process ID: " + currentProcess.getProcessID());
            } else {
                break;
            }
        }
        if (!currentProcess.isComplete()) {
            processQueue.offer(currentProcess); // Re-add the process if it's not complete
        }
        return currentProcess;
    }

    public boolean hasPendingProcesses() {
        return !processQueue.isEmpty();
    }

    //*Test Code
    /*
    public static void main(String[] args) {
        Process process1 = Process.createProcess(1, Arrays.asList("Instruction1", "Instruction2", "Instruction3"), 0, new int[]{0, 100});
        Process process2 = Process.createProcess(2, Arrays.asList("InstructionA", "InstructionB"), 0, new int[]{101, 200});
        Process process3 = Process.createProcess(3, Arrays.asList("Op1", "Op2", "Op3", "Op4"), 0, new int[]{201, 300});

        Scheduler scheduler = new Scheduler(Arrays.asList(process1, process2, process3));

        while (scheduler.hasPendingProcesses()) {
            scheduler.scheduleNext();
        }
    }
    */
}
