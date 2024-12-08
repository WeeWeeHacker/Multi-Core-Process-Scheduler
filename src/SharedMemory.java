import java.util.concurrent.ConcurrentHashMap;


public class SharedMemory {
    private ConcurrentHashMap<String, Object> memoryMap;

    public SharedMemory() {
        memoryMap = new ConcurrentHashMap<>();
    }

    /**
     * Reads a variable from shared memory.
     * @param name The name of the variable.
     * @return The value of the variable, or null if it does not exist.
     */
    public synchronized Object read(String name) {
        Object value = memoryMap.get(name);
        if (value == null) {
            System.out.println("Memory Read: Variable \"" + name + "\" does not exist.");
        } else {
            System.out.println("Memory Read: " + name + " = " + value);
        }
        return value;
    }

    /**
     * Writes a value to a variable in shared memory.
     * @param name The name of the variable.
     * @param value The value to write.
     */
    public synchronized void write(String name, Object value) {
        memoryMap.put(name, value);
        System.out.println("Memory Write: " + name + " = " + value);
    }

    /**
     * Returns the current state of shared memory as a string.
     * @return A string representation of all variables and their values.
     */
    public synchronized String getState() {
        return memoryMap.toString();
    }
}

