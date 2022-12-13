package client;

import com.beust.jcommander.Parameter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

public class MainCommand {
    @Parameter(
            names= "-t",
            description = "type"
    )
    private String type;

    @Parameter(
            names= "-k",
            description = "key"
    )
    private Object key;

    @Parameter(
            names= "-v",
            description = "message"
    )
    private Object value;

    @Parameter(
            names= "-in",
            description = "input file"
    )
    private String fileName;

    private String filePath;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getFullCommand() {
        if (type != null) {
            switch (type) {
                case "get" -> {
                    return "get " + key;
                }
                case "delete" -> {
                    return "delete " + key;
                }
                case "set" -> {
                    return "set " + key + " " + value;
                }
                case "exit" -> {
                    return type;
                }
            }
        }
        return null;
    }

    private void loadCommandFromFile(){
        try (Reader reader = new FileReader(filePath + fileName)) {
            Type stringStringMap = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> jsonCommand = new Gson().fromJson(reader, stringStringMap);
            this.setType((String) jsonCommand.get("type"));
            if (jsonCommand.containsKey("key")) {
                this.setKey(jsonCommand.get("key"));
            }

            if (jsonCommand.containsKey("value")) {
                this.setValue(jsonCommand.get("value"));
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MainCommand(String filePath) {
        this.filePath = filePath;
    }

    public String getFullCommandJSON() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder
                .create();

        if (fileName != null) {
            loadCommandFromFile();
        } else {
            gson = gsonBuilder
                .registerTypeAdapter(MainCommand.class, new MainCommandGsonSerializer())
                .create();
        }

        this.filePath = null;
        this.fileName = null;

        return gson.toJson(this);
    }

}
