package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    private static void saveDb(String filePath, ReadWriteLock lock){
        lock.readLock().lock();
        try(Reader reader = new FileReader(filePath)) {
            lock.readLock().unlock();
        } catch (FileNotFoundException e) {
            lock.readLock().unlock();
            lock.writeLock().lock();
            try(Writer writer = new FileWriter(filePath)) {
                writer.write("{}");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            lock.writeLock().unlock();
        } catch (IOException e) {
            lock.readLock().unlock();
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 23456;
        String filePath = System.getProperty("user.dir") + "/src/main/java/server/data/db.json";

        ReadWriteLock lock = new ReentrantReadWriteLock();

        System.out.println("Server started!");

        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            while (true) {
                try {
                    Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output  = new DataOutputStream(socket.getOutputStream());
                    MainCommand command = new Gson().fromJson(input.readUTF(), MainCommand.class);

                    ResposeMsg resposeMsg = new ResposeMsg();

                    if (command.getType().equals("exit")) {
                        resposeMsg.setResponse("OK");
                        saveDb(filePath, lock);
                        output.writeUTF(resposeMsg.getJSON());
                        break;
                    } else {

                        ThreadHandleCommand threadHandleCommand = new ThreadHandleCommand(socket, command, filePath, lock);
                        threadHandleCommand.start();
                    }
                } catch (IOException | JsonSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
