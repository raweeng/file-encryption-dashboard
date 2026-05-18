// --------------------------------------------
// User.java
// ---------------------------------------------

public class User {

    // Protected: accessible by subclasses
    protected int    userId;
    protected String username;
    protected String password;
    protected String role;

    // Constructor
    public User(int userId, String username,
                String password, String role) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.role     = role;
    }

    // Validates that the supplied password matches stored password
    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // Clears the session
    public void logout() {
        System.out.println(username + " has logged out.");
    }

    // Getters
    public int    getUserId()  { return userId; }
    public String getUsername(){ return username; }
    public String getRole()    { return role; }
}

// ------------------------------------------------------------
// AdminUser.java (defined in same file for simplicity)
// ------------------------------------------------------------

class AdminUser extends User {

    private int adminLevel;

    public AdminUser(int userId, String username,
                     String password, int adminLevel) {
        // Call superclass constructor using super()
        super(userId, username, password, "ADMIN");
        this.adminLevel = adminLevel;
    }

    // Admin-only operations
    public void manageUsers() {
        System.out.println("Admin " + username + " is managing users.");
    }

    public void viewAuditLogs() {
        System.out.println("Admin " + username + " is viewing audit logs.");
    }

    public int getAdminLevel() { return adminLevel; }
}


// ---------------------------------------------------------------
// GeneralUser.java (defined in same file for simplicity)
// ---------------------------------------------------------------

class GeneralUser extends User {

    private String accessLevel;

    public GeneralUser(int userId, String username,
                       String password, String accessLevel) {
        super(userId, username, password, "USER");
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() { return accessLevel; }
}