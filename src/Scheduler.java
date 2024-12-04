import java.util.*;

class Scheduler {
    private Queue<Process> processQueue;
    private static final int quantum = 2; // Quantum for Round Robin

    // Constructor for Hybrid SJF with Round Robin
    public Scheduler(List<Process> processes) {
        this.processQueue = new LinkedList<>(processes);
    }

    //?Master-core uses
    public void addProcess(Process process) {
        processQueue.offer(process);
    }

    public Process scheduleNext() {
        if (processQueue.isEmpty()) {
            System.out.println("All processes have been finished");
            return null; //No processes left to schedule
        }

        //Sorts procs according to burst-time; gets the shortest
        List<Process> processList = new ArrayList<>(processQueue);
        processList.sort(Comparator.comparingInt(Process::getBurstTime));
        Process currentProcess = processList.getFirst();
        processQueue = new LinkedList<>(processList);
        //process execution
        for (int i = 0; i < quantum; i++) {
            if (!currentProcess.isComplete()) {
                String instruction = currentProcess.getNextInstruction();
                System.out.println("Executing instruction: " + instruction + " from Process ID: " + currentProcess.getProcessID());
            } else {
                break; //If process is complete, stop execution
            }
        }
        //If not complete, re-add to queue
        if (!currentProcess.isComplete()) {
            processQueue.offer(currentProcess);
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
