package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class ThreadHandleCommand extends Thread {

    private final Socket socket;
    private final MainCommand command;
    private final String filePath;
    private final Lock readLock;
    private final Lock writeLock;

    public ThreadHandleCommand(Socket socket, MainCommand command, String filePath, ReadWriteLock lock) {
        this.socket = socket;
        this.command = command;
        this.filePath = filePath;
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    private Map<String, Object> loadDb(){
        Map<String, Object> jsonDatabase;
        Type stringStringMap = new TypeToken<Map<String, Object>>(){}.getType();
        readLock.lock();
        try (Reader reader = new FileReader(filePath)) {

            jsonDatabase = new HashMap<>(new Gson().fromJson(reader, stringStringMap));
        } catch (FileNotFoundException e) {
            jsonDatabase = new HashMap<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readLock.unlock();
        return jsonDatabase;
    }

    private void saveDb(Map<String, Object> jsonDb){
        writeLock.lock();
        try (Writer writer = new FileWriter(filePath)) {
            writer.write(new Gson().toJson(jsonDb));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeLock.unlock();
    }

    private boolean checkIfKeyExistsRecursive(JsonObject jsonObject, List<String> key) {
        if (key.size() > 1 && jsonObject.has(key.get(0)))
            return checkIfKeyExistsRecursive(jsonObject.get(key.get(0)).getAsJsonObject(), key.stream().skip(1).toList());
        else
            return jsonObject.has(key.get(0));
    }

    private boolean checkIfKeyExists(List<String> key, JsonObject context) {
        if (key.size() > 1 && context.has(key.get(0)))
            return checkIfKeyExistsRecursive(new Gson().toJsonTree(context.get(key.get(0))).getAsJsonObject(), key.stream().skip(1).toList());
        else
            return loadDb().containsKey(key.get(0));
    }

    private boolean checkIfKeyExists(List<String> key) {
        if (key.size() > 1 && loadDb().containsKey(key.get(0)))
            return checkIfKeyExistsRecursive(new Gson().toJsonTree(loadDb().get(key.get(0))).getAsJsonObject(), key.stream().skip(1).toList());
        else
            return loadDb().containsKey(key.get(0));
    }

    private com.google.gson.JsonElement getJsonRecursive(JsonObject jsonObject, List<String> key) {
        if (key.size() > 1 && checkIfKeyExists(key)) {
            return getJsonRecursive(jsonObject.get(key.get(0)).getAsJsonObject(), key.stream().skip(1).toList());
        } else {
            return jsonObject.get(key.get(0));
        }

    }

    private com.google.gson.JsonElement getValue(List<String> key) {
        if (key.size() > 1 && checkIfKeyExists(key)) {
            return getJsonRecursive(new Gson().toJsonTree(loadDb().get(key.get(0))).getAsJsonObject(), key.stream().skip(1).toList());
        } else {
            return new Gson().toJsonTree(loadDb().get(key.get(0)));
        }
    }

    private void setJsonRecursive(JsonObject jsonObject, List<String> key, Object value) {
        if (key.size() > 1 && checkIfKeyExists(key, jsonObject)) {
            setJsonRecursive(jsonObject.get(key.get(0)).getAsJsonObject(), key.stream().skip(1).toList(), value);
        } else {
            jsonObject.addProperty(key.get(0), (String) value);
        }

    }

    private void setValue(List<String> key, Object value) {
        Map<String, Object> jsonDatabase = loadDb();

        if (key.size() > 1) {
            JsonObject jsonObject = new Gson().toJsonTree(jsonDatabase.get(key.get(0))).getAsJsonObject();
            setJsonRecursive(jsonObject, key.stream().skip(1).toList(), value);
            jsonDatabase.put(key.get(0), jsonObject);
        }

        else
            jsonDatabase.put(key.get(0), value);

        saveDb(jsonDatabase);
    }

    private void deleteRecursive(JsonObject jsonObject, List<String> key) {
        if (key.size() > 1 && checkIfKeyExists(key, jsonObject)) {
            deleteRecursive(jsonObject.get(key.get(0)).getAsJsonObject(), key.stream().skip(1).toList());
        } else {
            jsonObject.remove(key.get(0));
        }
    }

    private void deleteValue(List<String> key) {
        Map<String, Object> jsonDatabase = loadDb();
        if (key.size() > 1) {
            JsonObject jsonObject = new Gson().toJsonTree(jsonDatabase.get(key.get(0))).getAsJsonObject();
            deleteRecursive(jsonObject, key.stream().skip(1).toList());
            jsonDatabase.put(key.get(0), jsonObject);
        } else
            jsonDatabase.remove(key.get(0));

        saveDb(jsonDatabase);
    }

    @Override
    public void run() {
        try (DataOutputStream output  = new DataOutputStream(socket.getOutputStream())) {

            ResposeMsg resposeMsg = new ResposeMsg();

            switch (command.getType()) {
                case "get" -> {
                    if (command.getKey() instanceof String) {
                        if (checkIfKeyExists(Collections.singletonList((String) command.getKey()))) {
                            resposeMsg.setResponse("OK");
                            resposeMsg.setValue(getValue(Collections.singletonList((String) command.getKey())));
                        } else {
                            resposeMsg.setResponse("ERROR");
                            resposeMsg.setReason("No such key");
                        }

                        output.writeUTF(resposeMsg.getJSON());
                    } else {
                        if (checkIfKeyExists((List<String>) command.getKey())) {
                            resposeMsg.setResponse("OK");
                            resposeMsg.setValue(getValue((List<String>) command.getKey()));
                        } else {
                            resposeMsg.setResponse("ERROR");
                            resposeMsg.setReason("No such key");
                        }

                        output.writeUTF(resposeMsg.getJSON());
                    }
                }
                case "set" -> {
                    if (command.getKey() instanceof String)
                        setValue(Collections.singletonList(command.getKey().toString()), command.getValue());
                    else
                        setValue((List<String>) command.getKey(), command.getValue());

                    resposeMsg.setResponse("OK");
                    output.writeUTF(resposeMsg.getJSON());
                }
                case "delete" -> {
                    if (checkIfKeyExists((List<String>) command.getKey())) {
                        deleteValue((List<String>) command.getKey());
                        resposeMsg.setResponse("OK");
                    } else {
                        resposeMsg.setResponse("ERROR");
                        resposeMsg.setReason("No such key");
                    }
                    output.writeUTF(resposeMsg.getJSON());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
