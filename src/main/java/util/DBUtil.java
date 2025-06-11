package util;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel";
    private static String USER;
    private static String PASSWORD;
	
    //private static final String USER = "root";
    //private static final String PASSWORD = "farhad";
    //private static final String URL = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7782688?useSSL=false&serverTimezone=UTC";
    //private static final String USER = "sql7782688";
    //private static final String PASSWORD = "I6wtnzZH4E";

    static {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Load encrypted credentials
            Properties props = new Properties();
            //String configPath = Paths.get("config", "db.properties").toString();
            String configPath = Paths.get(System.getProperty("user.dir"), "config", "db.properties").toString();
            props.load(new FileInputStream(configPath));

            String key = props.getProperty("key");
            USER = decrypt(props.getProperty("user"), key);
            PASSWORD = decrypt(props.getProperty("password"), key);
        } catch (Exception e) {
            System.err.println("Error initializing DBUtil.");
            e.printStackTrace();
        }
    }
    

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeQuietly(AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
            } catch (Exception ignored) {}
        }
    }

    private static String decrypt(String encrypted, String key) throws Exception {
        byte[] decodedKey = key.getBytes("UTF-8");
        SecretKeySpec secretKey = new SecretKeySpec(decodedKey, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decoded = Base64.getDecoder().decode(encrypted);
        byte[] original = cipher.doFinal(decoded);
        return new String(original, "UTF-8");
    }
    
}
