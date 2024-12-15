import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlaveCore {
    private final int coreID;
    private Process currentProcess;
    private final SharedMemory sharedMemory;
    private boolean busy;
    private final ExecutorService executor;

    public SlaveCore(int coreID, SharedMemory sharedMemory) {
        this.coreID = coreID;
        this.sharedMemory = sharedMemory;
        this.busy = false;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public int getCoreID() {
        return coreID;
    }

    public boolean isBusy() {
        return busy;
    }

    public void executeProcess(Process process, Runnable onComplete) {
        if (process == null) {
            System.out.println("No process to execute.");
            return;
        }

        busy = true;

        // Start a new thread for asynchronous execution
        new Thread(() -> {
            try {
                System.out.println("Slave " + coreID + " started executing process " + process.getProcessID());

                while (!process.isComplete()) {
                    String instruction = process.getNextInstruction();
                    if (instruction != null) {
                        System.out.println("Slave " + coreID + " executing instruction: " + instruction);
                        executeInstruction(instruction); // Log instruction execution
                    }
                }

                System.out.println("Slave " + coreID + " completed process " + process.getProcessID());
            } finally {
                busy = false;
                if (onComplete != null) {
                    onComplete.run(); // Callback for notifying completion
                }
            }
        }).start();
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
        if (instruction.matches("^(add|subtract|multiply|divide).*")) return "Arithmetic Operation";
        return "Unknown";
    }


    private void handleAssignment(String instruction) {
        // Example: "assign x = 5" or "assign z = add a, b" or "assign z = add a b"
        String[] parts = instruction.split("=");
        if (parts.length != 2) {
            System.out.println("Invalid assignment instruction: " + instruction);
            return;
        }

        String[] leftSideParts = parts[0].trim().split(" ");
        if (leftSideParts.length < 2) {
            System.out.println("Invalid assignment syntax on the left-hand side: " + instruction);
            return;
        }

        String variable = leftSideParts[1].trim();
        String rightSide = parts[1].trim();

        if (rightSide.startsWith("add") || rightSide.startsWith("subtract") ||
                rightSide.startsWith("multiply") || rightSide.startsWith("divide")) {
            // Handle "assign z = add a, b" or "assign z = add a b"
            String[] operationParts = rightSide.split("\\s+", 2);
            if (operationParts.length < 2) {
                System.out.println("Invalid arithmetic operation in assignment: " + instruction);
                return;
            }

            String operation = operationParts[0].trim();
            String[] operands = operationParts[1].contains(",")
                    ? operationParts[1].split(",")
                    : operationParts[1].split("\\s+");

            if (operands.length != 2) {
                System.out.println("Invalid operands for arithmetic operation in assignment: " + instruction);
                return;
            }

            String operand1 = operands[0].trim();
            String operand2 = operands[1].trim();

            Integer value1 = (Integer) sharedMemory.read(operand1);
            Integer value2 = (Integer) sharedMemory.read(operand2);

            if (value1 == null || value2 == null) {
                System.out.println("Undefined variables in arithmetic operation: " + operand1 + ", " + operand2);
                return;
            }

            int result;
            switch (operation) {
                case "add":
                    result = value1 + value2;
                    break;
                case "subtract":
                    result = value1 - value2;
                    break;
                case "multiply":
                    result = value1 * value2;
                    break;
                case "divide":
                    if (value2 == 0) {
                        System.out.println("Division by zero is not allowed in instruction: " + instruction);
                        return;
                    }
                    result = value1 / value2;
                    break;
                default:
                    System.out.println("Unknown operation in instruction: " + instruction);
                    return;
            }

            sharedMemory.write(variable, result);
            System.out.println("Assigned " + result + " to " + variable + " (result of " + operation + " " + operand1 + " and " + operand2 + ")");
        } else {
            // Handle "assign x = 5"
            try {
                int value = Integer.parseInt(rightSide);
                sharedMemory.write(variable, value);
                System.out.println("Assigned " + value + " to " + variable);
            } catch (NumberFormatException e) {
                System.out.println("Invalid value in assignment instruction: " + instruction);
            }
        }
    }





    private void handlePrint(String instruction) {

        String variable = instruction.split(" ")[1].trim();
        Integer value = (Integer) sharedMemory.read(variable); // Assuming sharedMemory has a read method
        if (value != null) {
            System.out.println("Value of " + variable + ": " + value);
        } else {
            System.out.println("Variable " + variable + " not found.");
        }
    }

    private void handleArithmetic(String instruction) {

        String[] parts = instruction.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Invalid arithmetic instruction: " + instruction);
            return;
        }

        String operation = parts[0].trim();
        String[] operands = parts[1].split(",");


        if (operands.length != 2) {
            System.out.println("Invalid operands in instruction: " + instruction);
            return;
        }

        String variable = operands[0].trim();
        int operand;
        try {
            operand = Integer.parseInt(operands[1].trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number in instruction: " + instruction);
            return;
        }


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
    }







//    public static void main(String[] args) {
//
//        SharedMemory sharedMemory = new SharedMemory();
//
//
//        List<String> instructions = Arrays.asList(
//                "assign x = 5",
//                "print x",
//                "add x, 10",
//                "print x",
//                "subtract x, 3",
//                "print x",
//                "divide x, 0",      // Attempt to divide x by 0
//                "multiply x, 2",
//                "print x"
//        );
//
//
//        Process process1 = Process.createProcess(1, instructions, 0, new int[]{0, 100}, 10);
//
//
//        SlaveCore slaveCore = new SlaveCore(1, sharedMemory);
//
//
//        System.out.println("Starting Process Execution...");
//        slaveCore.executeProcess(process1 , null);
//
//
//        System.out.println("\nFinal Shared Memory State:");
//        System.out.println(sharedMemory.getState());
//    }

}




