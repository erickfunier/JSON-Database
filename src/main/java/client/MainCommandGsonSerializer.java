package client;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainCommandGsonSerializer implements JsonSerializer<MainCommand> {
    @Override
    public JsonElement serialize(MainCommand command, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

        if (command.getKey() instanceof ArrayList<?> && ((ArrayList<?>) command.getKey()).size() == 1) {
            JsonObject commandObject = new JsonObject();

            commandObject.addProperty("type", command.getType());
            if (command.getKey() != null)
                commandObject.addProperty("key", ((ArrayList<String>) command.getKey()).get(0));

            if (command.getValue() != null)
                commandObject.addProperty("value", ((ArrayList<String>) command.getValue()).get(0));

            return commandObject;
        }

        return new Gson().toJsonTree(command);
    }
}
