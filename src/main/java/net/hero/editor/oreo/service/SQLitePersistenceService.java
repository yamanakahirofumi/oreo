package net.hero.editor.oreo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLitePersistenceService implements PersistenceService {

    private final String dbUrl;

    public SQLitePersistenceService() {
        this("jdbc:sqlite:" + getDefaultDbPath());
    }

    public SQLitePersistenceService(String dbUrl) {
        this.dbUrl = dbUrl;
        initializeDatabase();
    }

    private static String getDefaultDbPath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        Path path;

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData == null) appData = userHome;
            path = Paths.get(appData, "OREO", "oreo.db");
        } else if (os.contains("mac")) {
            path = Paths.get(userHome, "Library", "Application Support", "OREO", "oreo.db");
        } else {
            path = Paths.get(userHome, ".config", "oreo", "oreo.db");
        }

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            return "oreo.db";
        }
        return path.toString();
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    @Override
    public void saveSetting(String key, String value) {
        String sql = "INSERT OR REPLACE INTO settings(key, value) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save setting: " + key, e);
        }
    }

    @Override
    public String getSetting(String key) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get setting: " + key, e);
        }
        return null;
    }
}
