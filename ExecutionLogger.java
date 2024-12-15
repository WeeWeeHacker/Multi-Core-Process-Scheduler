package Studying;

import java.util.ArrayList;
import java.util.List;

public class ExecutionLogger {
    private List<String> log;

    public ExecutionLogger() {
        log = new ArrayList<>();
    }


    public void logStatus(String status) {
        log.add(status);
    }

    public void printLogs() {
        if (log.isEmpty()) {
            System.out.println("There are no logs.");
        } else {
            System.out.println("    Execution Logs: ");
            for (int i = 0;i<log.size(); i++) {
                System.out.println(log.get(i));
            }
        }
    }

    public List<String> getLog() {
        return log;
    }
}
