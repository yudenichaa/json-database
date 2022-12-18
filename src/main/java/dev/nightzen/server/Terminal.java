package dev.nightzen.server;

import com.google.gson.Gson;
import dev.nightzen.client.Command;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Terminal {

    @SuppressWarnings("unchecked")
    public String execCommand(Command command) throws IOException {
        Gson gson = new Gson();
        List<String> keys = command.getKey() instanceof String
                ? List.of((String) command.getKey())
                : (List<String>) command.getKey();

        switch (command.getType()) {
            case "get": {
                Object value = DB.get(keys);

                if (value != null) {
                    return gson.toJson(Map.of(
                            "response", "OK",
                            "value", value));
                } else {
                    return gson.toJson(Map.of(
                            "response", "ERROR",
                            "reason", "No such key"));
                }
            }
            case "set": {
                try {
                    DB.set(keys, command.getValue());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return gson.toJson(Map.of("response", "OK"));
            }
            case "delete": {
                boolean keyDeleted = DB.delete(keys);

                if (keyDeleted) {
                    return gson.toJson(Map.of("response", "OK"));
                } else {
                    return gson.toJson(Map.of(
                            "response", "ERROR",
                            "reason", "No such key"));
                }
            }
            default:
                return null;
        }
    }
}
