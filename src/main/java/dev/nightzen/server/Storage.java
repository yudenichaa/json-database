package dev.nightzen.server;

import java.util.Map;

class Storage {
    public Map<String, Object> storage;

    public Map<String, Object> getStorage() {
        return storage;
    }

    public Storage(Map<String, Object> storage) {
        this.storage = storage;
    }

    public void setStorage(Map<String, Object> storage) {
        this.storage = storage;
    }
}