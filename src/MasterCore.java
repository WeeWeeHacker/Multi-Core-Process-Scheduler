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
        this.slaveCore1 = SlaveCore.createSlaveCore(1, sharedMemory);
        this.slaveCore2 = SlaveCore.createSlaveCore(2, sharedMemory);
        this.logger = new ExecutionLogger();

        // Parse programs and create processes
        List<Process> processes = parsePrograms(programFiles);

        // Assign processes to slave cores using a scheduler
        this.scheduler = new Scheduler(processes);
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
        // Start the scheduler and assign processes to slave cores
        scheduler.startScheduling();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        // Monitor the state of the cores and shared memory
        Runnable statusLogger = () -> {
            System.out.println("Current State of Shared Memory: " + sharedMemory.getState());
            System.out.println("Slave Core 1 Status: " + (slaveCore1.isBusy() ? "Busy (Process ID " + slaveCore1.getCoreID() + ")" : "Idle"));
            System.out.println("Slave Core 2 Status: " + (slaveCore2.isBusy() ? "Busy (Process ID " + slaveCore2.getCoreID() + ")" : "Idle"));
        };

        // Schedule status updates every 2 milliseconds
        executorService.scheduleAtFixedRate(statusLogger, 0, 2, TimeUnit.MILLISECONDS);

        // Finalize execution and print logs
        executorService.schedule(() -> {
            logger.printLogs();
            executorService.shutdown();
        }, 10, TimeUnit.SECONDS); // Adjust time as necessary based on workload
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
