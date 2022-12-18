package dev.nightzen.client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Command {
    private String type;
    private Object key;
    private Object value;
    private static final String COMMANDS_DIR =
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
                    + "client"
                    + File.separator
                    + "data";

    public Command(String[] argv) throws IOException {
        CommandArgs args = new CommandArgs();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);

        if (args.in != null) {
            Gson gson = new Gson();
            Path commandPath = Paths.get(COMMANDS_DIR, args.in);
            String json = new String(Files.readAllBytes(commandPath));
            Command command = gson.fromJson(json, Command.class);
            this.key = command.key;
            this.type = command.type;
            this.value = command.value;
        } else {
            this.type = args.type;
            this.key = args.key;
            this.value = args.value;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
