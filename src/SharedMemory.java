import java.util.concurrent.ConcurrentHashMap;

public class SharedMemory {
    private final ConcurrentHashMap<String, Object> memoryMap;

    public SharedMemory() {
        memoryMap = new ConcurrentHashMap<>();
    }

    public synchronized Object read(String name) {
        Object value = memoryMap.get(name);
        System.out.println(value == null
                ? "Memory Read: Variable \"" + name + "\" does not exist."
                : "Memory Read: " + name + " = " + value);
        return value;
    }

    public synchronized void write(String name, Object value) {
        memoryMap.put(name, value);
        System.out.println("Memory Write: " + name + " = " + value);
    }

    public synchronized void remove(String name) {
        memoryMap.remove(name);
        System.out.println("Memory Removed: " + name);
    }

    public synchronized String getState() {
        return memoryMap.toString();
    }
}