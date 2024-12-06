import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SlaveCore {
    private static final Set<Integer> existingcoreIDs = new HashSet<>();
    private final int coreID;
    private Process currentProcess;
    private final SharedMemory sharedMemory;
    private boolean busy;

   private SlaveCore(int coreID, SharedMemory sharedMemory) {
        this.coreID = coreID;
        this.sharedMemory = sharedMemory;
        this.busy = false;
    }

    public SlaveCore createSlaveCore(int coreID, SharedMemory sharedMemory){
        while (existingcoreIDs.contains(coreID)) {
            coreID++;
        }
        existingcoreIDs.add(coreID);

        return new SlaveCore(coreID,sharedMemory);

    }

    public int getCoreID() {
        return coreID;
    }

    public boolean isBusy() {
        return busy;
    }

    public void executeProcess(Process process) {
        if (process == null) {
            System.out.println("No process to execute on Slave " + coreID);
            return;
        }

        busy = true;
        this.currentProcess = process;

        System.out.println("Slave " + coreID + " started executing process " + process.getProcessID());

        while (!process.isComplete()) {
            String instruction = process.getNextInstruction();
            if (instruction != null) {
                System.out.println("Executing instruction: " + instruction);
                executeInstruction(instruction);
            } else {
                System.out.println("Instruction is null. Skipping...");
            }
        }

        System.out.println("Slave " + coreID + " completed process " + process.getProcessID());

        // Mark the core as free
        this.currentProcess = null;
        busy = false;
    }

    private void executeInstruction(String instruction) {
        String category = categorizeInstruction(instruction);

        switch (category) {
            case "Assignment":
                handleAssignment(instruction);
                break;
            case "Print":
                handlePrint(instruction);
                break;
            case "Arithmetic Operation":
                handleArithmetic(instruction);
                break;
            default:
                System.out.println("Unknown instruction: " + instruction);
        }
    }

    private String categorizeInstruction(String instruction) {
        if (instruction.startsWith("assign")) return "Assignment";
        if (instruction.startsWith("print")) return "Print";
        if (instruction.startsWith("add") || instruction.startsWith("subtract") ||
                instruction.startsWith("multiply") || instruction.startsWith("divide")) return "Arithmetic Operation";
        return "Unknown";
    }

    private void handleAssignment(String instruction) {

        String[] parts = instruction.split("=");
        if (parts.length == 2) {
            String variable = parts[0].split(" ")[1].trim();
            int value = Integer.parseInt(parts[1].trim());
            sharedMemory.write(variable, value); // Assuming sharedMemory has a write method
            System.out.println("Assigned " + value + " to " + variable);
        } else {
            System.out.println("Invalid assignment instruction: " + instruction);
        }
    }

    private void handlePrint(String instruction) {
        // Example: "print x"
        String variable = instruction.split(" ")[1].trim();
        Integer value = (Integer) sharedMemory.read(variable); // Assuming sharedMemory has a read method
        if (value != null) {
            System.out.println("Value of " + variable + ": " + value);
        } else {
            System.out.println("Variable " + variable + " not found.");
        }
    }

    private void handleArithmetic(String instruction) {
        // Example: "add x, 10"
        String[] parts = instruction.split(" ");
        if (parts.length >= 3) {
            String operation = parts[0];
            String[] operands = parts[1].split(",");
            String variable = operands[0].trim();
            int operand = Integer.parseInt(operands[1].trim());

            Integer currentValue = (Integer) sharedMemory.read(variable);
            if (currentValue == null) {
                System.out.println("Variable " + variable + " not found for arithmetic operation.");
                return;
            }

            int newValue;
            switch (operation) {
                case "add":
                    newValue = currentValue + operand;
                    break;
                case "subtract":
                    newValue = currentValue - operand;
                    break;
                case "multiply":
                    newValue = currentValue * operand;
                    break;
                case "divide":
                    if (operand == 0) {
                        System.out.println("Division by zero is not allowed.");
                        return;
                    }
                    newValue = currentValue / operand;
                    break;
                default:
                    System.out.println("Unknown arithmetic operation: " + operation);
                    return;
            }

            sharedMemory.write(variable, newValue);
            System.out.println("Updated " + variable + " to " + newValue);
        } else {
            System.out.println("Invalid arithmetic instruction: " + instruction);
        }
    }


        public static void main(String[] args) {

            SharedMemory sharedMemory = new SharedMemory();


            List<String> instructions = Arrays.asList(
                    "assign x = 5",
                    "print x",
                    "add x, 10",
                    "print x",
                    "subtract x, 3",
                    "print x",
                    "divide x, 0", // This will test division by zero
                    "multiply x, 2",
                    "print x"
            );


            Process process1 = Process.createProcess(1, instructions, 0, new int[]{0, 100}, 10);


            SlaveCore slaveCore = new SlaveCore(1, sharedMemory);

            System.out.println("Starting Process Execution...");
            slaveCore.executeProcess(process1);

            System.out.println("\nFinal Shared Memory State:");
            System.out.println(sharedMemory.getState());
        }
    }




