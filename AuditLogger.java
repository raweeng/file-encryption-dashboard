// ------------------------------------------------------------
// AuditLogger.java 
// ------------------------------------------------------------

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditLogger {

    // Private log storage - hidden from outside classes
    private List<String> logs = new ArrayList<>();

    // Formats a timestamp string for each log entry
    private String timestamp() {
        DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(fmt);
    }

    // Logs a general action message
    public void logAction(String message) {
        String entry = "[ACTION] " + timestamp() + " -- " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    // Logs an error message
    public void logError(String message) {
        String entry = "[ERROR]  " + timestamp() + " -- " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    // Returns all stored log messages as a formatted string
    public String viewLogs() {
        if (logs.isEmpty()) {
            return "No audit log entries found.";
        }
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        return sb.toString();
    }

    // Returns the number of log entries
    public int getLogCount() {
        return logs.size();
    }
}