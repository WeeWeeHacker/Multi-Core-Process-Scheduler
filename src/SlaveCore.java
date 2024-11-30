public class SlaveCore {
// ana 3mlt el shared memory w 3mlt instruction parser, ento e3mlo el process
    private int coreID;
    private Process currentProcess;
    private SharedMemory sharedMemory;
    private boolean busy;


    public SlaveCore(int coreID, SharedMemory sharedMemory) {
        this.coreID = coreID;
        this.sharedMemory = sharedMemory;
        this.busy = false;
    }

    public int getCoreID() {
        return coreID;
    }

    public boolean isBusy() {
        return busy;
    }

    public void executeProcess(Process process) {
        busy = true;
        this.currentProcess = process;
        System.out.println("Slave " + coreID + " executing process " + process.getProcessID());
        while (process!=null) {
            String instruction = process.getNextInstruction();
            System.out.println("Executing instruction: " + instruction);
        }
        busy = false;
    }
}
