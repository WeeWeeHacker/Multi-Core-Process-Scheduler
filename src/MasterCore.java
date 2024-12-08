//!!!!!Master Core recognizes text files and initializes cores, but processess aren't assigned properly to cores!!!!!
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterCore {
    private SharedMemory sharedMemory;
    private SlaveCore slaveCore1;
    private SlaveCore slaveCore2;
    private Scheduler scheduler;
    private ExecutionLogger logger;

    public MasterCore(List<File> programFiles) throws IOException {
        //Initialize shared memory and cores
        this.sharedMemory = new SharedMemory();
        this.slaveCore1 = SlaveCore.createSlaveCore(1, sharedMemory);
        this.slaveCore2 = SlaveCore.createSlaveCore(2, sharedMemory);
        this.logger = new ExecutionLogger();

        //Parse programs and create processes
        List<Process> processes = parsePrograms(programFiles);
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
        //Start the scheduler to execute processes based on the selected algorithm
        scheduler.startScheduling();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable statusLogger = () -> {
            System.out.println("Current State of Shared Memory: " + sharedMemory.getState());
            System.out.println("Currently executing on Slave Core 1: " + (slaveCore1.isBusy() ? "Process ID " + slaveCore1.getCoreID() : "Idle"));
            System.out.println("Currently executing on Slave Core 2: " + (slaveCore2.isBusy() ? "Process ID " + slaveCore2.getCoreID() : "Idle"));
        };

        // Schedule status logging every 2 milliseconds
        executorService.scheduleAtFixedRate(statusLogger, 0, 2, TimeUnit.MILLISECONDS);

        // Wait for completion and print execution logs
        executorService.schedule(() -> {
            logger.printLogs();
            executorService.shutdown();
        }, 10, TimeUnit.SECONDS); // Adjust as necessary based on process length
    }

    public static void main(String[] args) {
        List<File> programFiles = List.of(
                new File("src/Resources/program_1.txt"),
                new File("src/Resources/Program_2.txt"),
                new File("src/Resources/Program_3.txt")
        );

        try {
            MasterCore masterCore = new MasterCore(programFiles);
            masterCore.startExecution();
        } catch (IOException e) {
            System.out.println("Error parsing programs: " + e.getMessage());
        }
    }
}
