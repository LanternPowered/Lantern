package org.lanternpowered.server.text.gson;

import java.lang.reflect.Type;
import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes/deserializes the {@link Text.Placeholder} object type introduced by sponge.
 */
public class JsonTextPlaceholderSerializer extends JsonTextBaseSerializer implements JsonDeserializer<Text.Placeholder>, JsonSerializer<Text.Placeholder> {

    @Override
    public JsonElement serialize(Text.Placeholder src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        List<Text> children = src.getChildren();
        Optional<Text> fallback = src.getFallback();
        if (fallback.isPresent()) {
            children.add(0, fallback.get());
            json.addProperty("fallbackAsExtra", true);
        }
        this.serialize(json, src, context, children);
        json.addProperty("placeholderKey", src.getKey());
        // Make sure that if a vanilla server/client reads the format it won't throw errors
        json.addProperty("text", "");
        return json;
    }

    @Override
    public Text.Placeholder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        String key = json0.get("placeholderKey").getAsString();
        JsonArray array = json0.has("extra") ? json0.getAsJsonArray("extra") : null;
        Text fallback = null;
        if (array != null && array.size() > 0 && json0.has("fallbackAsExtra") &&
                json0.get("fallbackAsExtra").getAsBoolean()) {
            // We copy all the objects first to avoid modifying the elements
            // used by the serializer, somebody may still need them
            JsonArray array0 = new JsonArray();
            array0.addAll(array);
            fallback = context.deserialize(array0.remove(0), Text.class);
            array = array0;
        }
        TextBuilder.Placeholder builder = Texts.placeholderBuilder(key).fallback(fallback);
        this.deserialize(json0, builder, context, array);
        return builder.build();
    }

}
