package dev.nightzen.client;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public void execCommand(String address, int port, String[] args) {
        try (Socket socket = new Socket(address, port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");
            String command = createCommandFromArgs(args);
            output.writeUTF(command);
            System.out.println("Sent: " + command);
            System.out.println("Received: " + input.readUTF());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private String createCommandFromArgs(String[] argv) throws IOException {
        Command command = new Command(argv);
        Gson gson = new Gson();
        return gson.toJson(command);
    }
}