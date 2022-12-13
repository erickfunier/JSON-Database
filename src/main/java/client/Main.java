package client;

import com.beust.jcommander.JCommander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        String filePath = System.getProperty("user.dir") + "/src/main/java/client/data/";

        MainCommand mainCommands = new MainCommand(filePath);
        JCommander.newBuilder()
                .addObject(mainCommands)
                .build()
                .parse(args);

        String address = "127.0.0.1";
        int port = 23456;
        System.out.println("Client Starded!");
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());) {

            String command = mainCommands.getFullCommandJSON();

            System.out.println("Sent: " + command);

            output.writeUTF(command); // sending message to the server
            String receivedMsg = input.readUTF(); // response message

            System.out.println("Received: " + receivedMsg);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
