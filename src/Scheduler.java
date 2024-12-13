import java.util.*;
import java.util.concurrent.*;

public class Scheduler {
    private final Queue<Process> processQueue;
    private final SlaveCore slaveCore1;
    private final SlaveCore slaveCore2;

    public Scheduler(List<Process> processes, SlaveCore slaveCore1, SlaveCore slaveCore2) {
        this.processQueue = new ConcurrentLinkedQueue<>(processes);
        this.slaveCore1 = slaveCore1;
        this.slaveCore2 = slaveCore2;
    }

    public boolean isQueueEmpty() {
        return processQueue.isEmpty();
    }

    public void startScheduling() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> {
            if (processQueue.isEmpty()) {
                executor.shutdown();
                System.out.println("All processes completed.");
                return;
            }

            Process process = processQueue.poll();
            if (process != null) {
                if (!slaveCore1.isBusy()) {
                    slaveCore1.executeProcess(process, () -> {
                        if (!process.isComplete()) {
                            processQueue.offer(process);
                        } else {
                            System.out.println("Process ID " + process.getProcessID() + " completed.");
                        }
                    });
                } else if (!slaveCore2.isBusy()) {
                    slaveCore2.executeProcess(process, () -> {
                        if (!process.isComplete()) {
                            processQueue.offer(process);
                        } else {
                            System.out.println("Process ID " + process.getProcessID() + " completed.");
                        }
                    });
                } else {
                    processQueue.offer(process); // If all cores are busy, re-queue the process
                }
            }
        }, 0, 2, TimeUnit.MILLISECONDS);
    }

}


