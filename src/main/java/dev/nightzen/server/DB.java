package dev.nightzen.server;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DB {
    private final static String DB_FILE_PATH =
            System.getProperty("user.dir")
                    + File.separator
                    + "src"
                    + File.separator
                    + "main"
                    + File.separator
                    + "java"
                    + File.separator
                    + "dev"
                    + File.separator
                    + "nightzen"
                    + File.separator
                    + "server"
                    + File.separator
                    + "data"
                    + File.separator
                    + "db.json";

    private static final Map<String, Object> db;
    private static final Gson gson = new Gson();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    static {
        try {
            db = readDBFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object get(List<String> keys) {
        readLock.lock();
        Object value = getR(db, keys);
        readLock.unlock();
        return value;
    }

    @SuppressWarnings("unchecked")
    private static Object getR(Map<String, Object> db, List<String> keys) {
        String key = keys.get(0);

        if (keys.size() == 1) {
            return db.get(key);
        } else if (db.containsKey(key) && db.get(key) instanceof Map) {
            Map<String, Object> subJson = (Map<String, Object>) db.get(key);
            List<String> subKeys = keys.subList(1, keys.size());
            return getR(subJson, subKeys);
        } else {
            return null;
        }
    }

    public static void set(List<String> keys, Object value) throws IOException {
        writeLock.lock();
        setR(db, keys, value);
        writeDBToFile();
        writeLock.unlock();
    }

    @SuppressWarnings("unchecked")
    private static void setR(Map<String, Object> db, List<String> keys, Object value) {
        String key = keys.get(0);

        if (keys.size() == 1) {
            db.put(key, value);
        } else {
            Map<String, Object> subJson =
                    (db.containsKey(key) && db.get(key) instanceof Map)
                            ? (Map<String, Object>) db.get(key)
                            : new HashMap<>();
            List<String> subKeys = keys.subList(1, keys.size());
            setR(subJson, subKeys, value);
            db.put(key, subJson);
        }

    }

    public static boolean delete(List<String> keys) throws IOException {
        writeLock.lock();
        boolean isDeleted = deleteR(db, keys);

        if (isDeleted) {
            writeDBToFile();
        }

        writeLock.unlock();
        return isDeleted;
    }

    @SuppressWarnings("unchecked")
    private static boolean deleteR(Map<String, Object> db, List<String> keys) {
        String key = keys.get(0);

        if (keys.size() == 1) {
            db.remove(key);
            return true;
        } else if (db.containsKey(key) && db.get(key) instanceof Map) {
            Map<String, Object> subJson = (Map<String, Object>) db.get(key);
            List<String> subKeys = keys.subList(1, keys.size());
            return deleteR(subJson, subKeys);
        } else {
            return false;
        }
    }

    private static Map<String, Object> readDBFromFile() throws IOException {
        Path dbPath = Paths.get(DB_FILE_PATH);

        if (!Files.exists(dbPath)) {
            return new HashMap<>();
        }

        String json = new String(Files.readAllBytes(dbPath));
        Storage storage = gson.fromJson(json, Storage.class);

        if (storage.getStorage() == null) {
            return new HashMap<>();
        }

        return storage.getStorage();
    }

    private static void writeDBToFile() throws IOException {
        Storage storage = new Storage(db);
        String json = gson.toJson(storage);
        Writer fileWriter = new FileWriter(DB_FILE_PATH);
        fileWriter.write(json);
        fileWriter.close();
    }
}
