import java.util.*;
import java.util.concurrent.*;

class Scheduler {
    private List<Process> processList;
    private Queue<Process> processQueue;
    private static final int QUANTUM = 2; // Time slice for Round Robin
    private ScheduledExecutorService executorService;

    public Scheduler(List<Process> processes) {
        // Initialize the process list and the queue, sorted by burst time
        this.processList = new ArrayList<>(processes);
        this.processQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getBurstTime));
        this.processQueue.addAll(processes);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startScheduling() {
        Runnable scheduleTask = () -> {
            if (!processQueue.isEmpty()) {
                // Sort the queue based on burst time to simulate SJF behavior
                List<Process> sortedQueue = new ArrayList<>(processQueue);
                sortedQueue.sort(Comparator.comparingInt(Process::getBurstTime));
                processQueue = new PriorityQueue<>(sortedQueue);

                // Extract the process with the shortest burst time
                Process currentProcess = processQueue.poll();
                if (currentProcess != null) {
                    System.out.println("Currently executing Process ID: " + currentProcess.getProcessID());

                    // Execute up to the quantum (2 clock cycles)
                    for (int i = 0; i < QUANTUM; i++) {
                        if (!currentProcess.isComplete()) {
                            String instruction = currentProcess.getNextInstruction();
                            if (instruction != null) {
                                System.out.println("Executing instruction: " + instruction + " from Process ID: " + currentProcess.getProcessID());
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    // Decrease the burst time after execution
                    currentProcess.decreaseBurstTime(2);

                    // If the process is still not complete, add it back to the queue
                    if (!currentProcess.isComplete() && currentProcess.getBurstTime() > 0) {
                        processQueue.offer(currentProcess);
                    } else {
                        // Print completion message for the current process
                        System.out.println("Process ID: " + currentProcess.getProcessID() + " completed.");
                    }
                }
            } else {
                // Shutdown the executor when all processes are complete
                executorService.shutdown();
                System.out.println("All processes completed.");
            }
        };

        // Schedule the task to run every second (or adjust as needed for clock cycle)
        executorService.scheduleAtFixedRate(scheduleTask, 0, 2, TimeUnit.MILLISECONDS);
    }

}
