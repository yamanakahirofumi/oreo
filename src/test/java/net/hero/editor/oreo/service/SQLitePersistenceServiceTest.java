package net.hero.editor.oreo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SQLitePersistenceServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testSaveAndGetSetting() {
        Path dbPath = tempDir.resolve("test.db");
        PersistenceService service = new SQLitePersistenceService("jdbc:sqlite:" + dbPath.toString());

        service.saveSetting("theme", "dark");
        service.saveSetting("fontSize", "16");

        assertEquals("dark", service.getSetting("theme"));
        assertEquals("16", service.getSetting("fontSize"));
        assertNull(service.getSetting("nonexistent"));
    }

    @Test
    void testGetSettingWithDefault() {
        Path dbPath = tempDir.resolve("test_default.db");
        PersistenceService service = new SQLitePersistenceService("jdbc:sqlite:" + dbPath.toString());

        assertEquals("default-value", service.getSetting("unknown", "default-value"));
        
        service.saveSetting("known", "actual-value");
        assertEquals("actual-value", service.getSetting("known", "default-value"));
    }

    @Test
    void testUpdateSetting() {
        Path dbPath = tempDir.resolve("test_update.db");
        PersistenceService service = new SQLitePersistenceService("jdbc:sqlite:" + dbPath.toString());

        service.saveSetting("key", "v1");
        assertEquals("v1", service.getSetting("key"));

        service.saveSetting("key", "v2");
        assertEquals("v2", service.getSetting("key"));
    }
}
