// ---------------------------------------------------------
// AuthenticationService.java
// ---------------------------------------------------------

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {

    // dictionary is simply declared as: user_db = {}
    // Java requires: Map<String, User> userDatabase = new HashMap<>();
    private Map<String, User> userDatabase = new HashMap<>();

    private AuditLogger auditLogger;

    // Constructor: pre-loads two users for demonstration
    public AuthenticationService(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;

        // Add a general user and an admin user to the in-memory store
        userDatabase.put("widath",
            new GeneralUser(1, "widath", "pass123", "STANDARD"));
        userDatabase.put("admin",
            new AdminUser(2, "admin", "admin123", 1));
    }

    // Validates credentials and returns the matching User object
    // Returns null if credentials do not match any stored user
    public User validateUser(String username, String password) {
        User user = userDatabase.get(username);

        // Short-circuit: check user exists before calling login()
        if (user != null && user.login(password)) {
            auditLogger.logAction(
                "Login successful: " + username +
                " (Role: " + user.getRole() + ")");
            return user;
        }
        auditLogger.logError(
            "Login failed for username: " + username);
        return null;
    }

    // Returns the role string for a given username
    public String getUserRole(String username) {
        User user = userDatabase.get(username);
        return user != null ? user.getRole() : "UNKNOWN";
    }

    // Loads and returns the User object for a given username
    public User loadUser(String username) {
        return userDatabase.get(username);
    }
}