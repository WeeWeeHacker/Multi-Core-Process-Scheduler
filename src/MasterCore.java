import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterCore {
    private final SharedMemory sharedMemory;
    private final SlaveCore slaveCore1;
    private final SlaveCore slaveCore2;
    private final Scheduler scheduler;
    private final ExecutionLogger logger;

    public MasterCore(List<File> programFiles) throws IOException {
        // Initialize shared memory and slave cores
        this.sharedMemory = new SharedMemory();
        this.slaveCore1 = new SlaveCore(1, sharedMemory);
        this.slaveCore2 = new SlaveCore(2, sharedMemory);
        this.logger = new ExecutionLogger();

        // Parse programs and create processes
        List<Process> processes = parsePrograms(programFiles);

        // Assign processes to slave cores using a scheduler
        this.scheduler = new Scheduler(processes, slaveCore1, slaveCore2);
    }

    private List<Process> parsePrograms(List<File> programFiles) throws IOException {
        List<Process> processes = new ArrayList<>();
        for (File file : programFiles) {
            InstructionParser parser = new InstructionParser(file);
            processes.addAll(parser.parseInstructions());
        }
        return processes;
    }

    public void startExecution() {
        scheduler.startScheduling();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        Runnable statusLogger = () -> {
            System.out.println("Current State of Shared Memory: " + sharedMemory.getState());
            System.out.println("Slave Core 1 Status: " + (slaveCore1.isBusy() ? "Busy (Process ID " + slaveCore1.getCoreID() + ")" : "Idle"));
            System.out.println("Slave Core 2 Status: " + (slaveCore2.isBusy() ? "Busy (Process ID " + slaveCore2.getCoreID() + ")" : "Idle"));
            System.out.println("-----------------------------------------------------------------------------------------------------------");

            // Check if all processes are completed and terminate the statusLogger
            if (scheduler.isQueueEmpty() && !slaveCore1.isBusy() && !slaveCore2.isBusy()) {
                System.out.println("All processes are complete. Shutting down logger...");
                executorService.shutdown();
            }
        };

        executorService.scheduleAtFixedRate(statusLogger, 0, 2, TimeUnit.MILLISECONDS);

        executorService.schedule(() -> {
            if (!executorService.isShutdown()) {
                logger.printLogs();
                executorService.shutdown();
            }
        }, 10, TimeUnit.SECONDS);

        try {
            if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) {
        List<File> programFiles = List.of(
                new File("src/Resources/program_1.txt"),
                new File("src/Resources/program_2.txt"),
                new File("src/Resources/program_3.txt")
        );

        try {
            MasterCore masterCore = new MasterCore(programFiles);
            masterCore.startExecution();
        } catch (IOException e) {
            System.out.println("Error parsing programs: " + e.getMessage());
        }
    }
}