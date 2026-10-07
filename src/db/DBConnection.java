package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection provides a centralized, reusable JDBC connection utility for
 * connecting to Oracle Database Free 26ai.
 *
 * <p>Configuration Details:
 * <ul>
 *   <li>Host: localhost</li>
 *   <li>Port: 1521</li>
 *   <li>Service Name: FREE</li>
 *   <li>JDBC Driver: oracle.jdbc.OracleDriver</li>
 *   <li>URL format: jdbc:oracle:thin:@//localhost:1521/FREE</li>
 * </ul>
 *
 * <p>Authentication is strictly configurable. Credentials are resolved in the following order:
 * <ol>
 *   <li>System properties: {@code -Ddb.user=<username> -Ddb.password=<password>}</li>
 *   <li>Environment variables: {@code DB_USER} and {@code DB_PASSWORD}</li>
 * </ol>
 * No hardcoded credentials or assumed database users are stored in source control.
 */
public final class DBConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 1521;
    private static final String SERVICE_NAME = getResolvedServiceName();
    private static final String DRIVER_CLASS = "oracle.jdbc.OracleDriver";

    // Oracle Service-Name syntax using thin driver:
    private static final String URL = "jdbc:oracle:thin:@//" + HOST + ":" + PORT + "/" + SERVICE_NAME;

    // Static initializer to verify driver availability once
    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: Oracle JDBC driver class not found (" + DRIVER_CLASS + ").");
            System.err.println("[DBConnection] Ensure ojdbc8.jar / ojdbc11.jar is added to the project classpath.");
        }
    }

    // Suppress default constructor for utility class
    private DBConnection() {
        throw new UnsupportedOperationException("Utility class DBConnection cannot be instantiated.");
    }


    /**
     * Resolves the database username from System properties or Environment variables.
     *
     * @return configured database username
     * @throws SQLException if the username is not configured
     */
    private static String getResolvedUser() throws SQLException {
        String user = System.getProperty("db.user");
        if (user == null || user.trim().isEmpty()) {
            user = System.getenv("DB_USER");
        }
        if (user == null || user.trim().isEmpty()) {
            throw new SQLException(
                "Oracle database username is not configured.\n" +
                "Please configure via environment variable 'DB_USER' or JVM argument '-Ddb.user=<username>'."
            );
        }
        return user.trim();
    }

    /**
     * Resolves the database password from System properties or Environment variables.
     *
     * @return configured database password
     * @throws SQLException if the password is not configured
     */
    private static String getResolvedPassword() throws SQLException {
        String password = System.getProperty("db.password");
        if (password == null || password.trim().isEmpty()) {
            password = System.getenv("DB_PASSWORD");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new SQLException(
                "Oracle database password is not configured.\n" +
                "Please configure via environment variable 'DB_PASSWORD' or JVM argument '-Ddb.password=<password>'."
            );
        }
        return password.trim();
    }

    /**
     * Resolves the Oracle service name from JVM properties or environment variables.
     *
     * @return configured service name, or FREE if none is configured
     */
    private static String getResolvedServiceName() {
        String serviceName = System.getProperty("db.service");
        if (serviceName == null || serviceName.trim().isEmpty()) {
            serviceName = System.getenv("DB_SERVICE");
        }
        if (serviceName == null || serviceName.trim().isEmpty()) {
            return "FREE";
        }
        return serviceName.trim();
    }

    /**
     * Establishes and returns a new {@link Connection} to the Oracle database.
     *
     * @return an active {@link Connection} instance
     * @throws SQLException if connection failed or configuration is missing
     */
    public static Connection getConnection() throws SQLException {
        String user = getResolvedUser();
        String password = getResolvedPassword();

        try {
            return DriverManager.getConnection(URL, user, password);
        } catch (SQLException ex) {
            int errorCode = ex.getErrorCode();
            String sqlState = ex.getSQLState();
            System.err.println("[DBConnection] Connection failure to " + URL);
            System.err.println("[DBConnection] ORA Error Code : " + errorCode);
            System.err.println("[DBConnection] SQLState       : " + sqlState);
            System.err.println("[DBConnection] Detail         : " + ex.getMessage());

            if (errorCode == 1017) {
                System.err.println("[DBConnection] Diagnosis: Invalid username or password (ORA-01017).");
            } else if (errorCode == 12541) {
                System.err.println("[DBConnection] Diagnosis: TNS no listener on port 1521. Check if Oracle service is running.");
            } else if (errorCode == 12514) {
                System.err.println("[DBConnection] Diagnosis: Listener does not recognize service 'FREE'. Confirm database service name.");
            }

            throw ex;
        }
    }

    /**
     * Standalone diagnostic runner to test JDBC connectivity without launching any GUI.
     * Executes 'SELECT SYSDATE FROM dual' to verify connection vitality.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Testing Oracle JDBC Connection (Member 1 Test)");
        System.out.println("Target URL: " + URL);
        System.out.println("==================================================");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SYSDATE FROM dual")) {

            if (rs.next()) {
                System.out.println("SUCCESS: Connected to Oracle Database Free 26ai!");
                System.out.println("Database Server Current Time: " + rs.getString(1));
            } else {
                System.out.println("CONNECTED: Query executed but returned no rows.");
            }

        } catch (SQLException e) {
            System.err.println("TEST FAILED: " + e.getMessage());
        }
    }
}
