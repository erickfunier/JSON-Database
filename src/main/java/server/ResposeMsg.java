package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ResposeMsg {
    private String response;
    private Object value;
    private String reason;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getJSON() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder
                .create();

        //System.out.println(getValue());
        //System.out.println(gson.toJson(this));

        return gson.toJson(this);
    }
}
