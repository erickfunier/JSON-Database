package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainCommand {

    private String type;
    private Object key;
    private Object value;

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

    public String getFullCommandJSON() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder
                .create();

        return gson.toJson(this);
    }

}
