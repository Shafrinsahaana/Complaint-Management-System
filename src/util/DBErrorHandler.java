package util;

import java.sql.SQLException;

public class DBErrorHandler {
    public static String getMessage(SQLException e) {
        int errorCode = e.getErrorCode();
        
        // Oracle Unique Constraint Violation (Duplicate Entry)
        if (errorCode == 1) {
            return "This email is already registered.";
        }
        
        // Oracle DB Down or Connection Failed
        if (errorCode == 12541 || errorCode == 12514 || errorCode == 17002 || errorCode == 0) {
            return "Cannot connect to the database. Is the server running?";
        }
        
        // Invalid credentials (though our system relies on hashed passwords, this handles DB access misconfigs)
        if (errorCode == 1017) {
            return "Database configuration error: Invalid DB credentials.";
        }
        
        return "Database Error: " + e.getMessage();
    }
}
