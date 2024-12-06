import java.util.concurrent.ConcurrentHashMap;

public class SharedMemory {
    private ConcurrentHashMap<String, Object> memoryMap;

    public SharedMemory() {
        memoryMap = new ConcurrentHashMap<>();
    }

    public synchronized Object readVariable(String name) {
        return memoryMap.getOrDefault(name, null);
    }

    public synchronized void writeVariable(String name, Object value) {
        memoryMap.put(name, value);
        System.out.println("Memory Write: " + name + " = " + value);
    }

    public synchronized String getState() {
        return memoryMap.toString();
    }
}
