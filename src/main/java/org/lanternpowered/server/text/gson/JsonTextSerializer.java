package org.lanternpowered.server.text.gson;

import java.lang.reflect.Type;

import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.text.Text;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class JsonTextSerializer extends JsonTextBaseSerializer implements JsonDeserializer<Text> {

    /**
     * Registers the json text serializers for the specified gson builder.
     * 
     * @param gsonBuilder the gson builder
     * @param translationManager the translation manager
     * @return the gson builder for chaining
     */
    public static GsonBuilder applyTo(GsonBuilder gsonBuilder, TranslationManager translationManager) {
        gsonBuilder.registerTypeAdapter(Text.class, new JsonTextSerializer());
        gsonBuilder.registerTypeAdapter(Text.Literal.class, new JsonTextLiteralSerializer());
        gsonBuilder.registerTypeAdapter(Text.Placeholder.class, new JsonTextPlaceholderSerializer());
        gsonBuilder.registerTypeAdapter(Text.Score.class, new JsonTextScoreSerializer());
        gsonBuilder.registerTypeAdapter(Text.Selector.class, new JsonTextSelectorSerializer());
        gsonBuilder.registerTypeAdapter(Text.Translatable.class, new JsonTextTranslatableSerializer(translationManager));
        return gsonBuilder;
    }

    @Override
    public Text deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return context.deserialize(json, Text.Literal.class);
        }
        JsonObject json0 = json.getAsJsonObject();
        if (json0.has("placeholderKey")) {
            return context.deserialize(json, Text.Placeholder.class);
        } else if (json0.has("text")) {
            return context.deserialize(json, Text.Literal.class);
        } else if (json0.has("translate")) {
            return context.deserialize(json, Text.Translatable.class);
        } else if (json0.has("score")) {
            return context.deserialize(json, Text.Score.class);
        } else if (json0.has("selector")) {
            return context.deserialize(json, Text.Selector.class);
        } else {
            throw new JsonParseException("Unknown text format: " + json.toString());
        }
    }

}
