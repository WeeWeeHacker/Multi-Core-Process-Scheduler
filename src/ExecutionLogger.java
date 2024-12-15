 import java.util.ArrayList;

import java.util.List;

public class ExecutionLogger {
    private List<String> logs;

    public ExecutionLogger() {
        logs = new ArrayList<>();
    }


    public void AddLog(String status) {
        logs.add(status);
    }

    public void printLogs() {
        if (logs.isEmpty()) {
            System.out.println("There are no logs.");
        } else {
            System.out.println("Execution Logs: ");
            for (String log : logs) {
                System.out.println(log);
            }
        }
    }

    public List<String> getLogs() {
        return logs;
    }
}
