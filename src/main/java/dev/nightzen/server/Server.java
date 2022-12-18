package dev.nightzen.server;

import com.google.gson.Gson;
import dev.nightzen.client.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 34522;

    public void start() {
        Terminal terminal = new Terminal();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");

            while (true) {
                try {
                    Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    String request = input.readUTF();
                    Gson gson = new Gson();
                    Command command = gson.fromJson(request, Command.class);

                    if ("exit".equals(command.getType())) {
                        output.writeUTF(gson.toJson(Map.of("response", "OK")));
                        input.close();
                        output.close();
                        socket.close();
                        break;
                    }

                    executor.submit(() -> {
                        try {
                            String response = terminal.execCommand(command);

                            if (response != null) {
                                output.writeUTF(response);
                            }

                            input.close();
                            output.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}