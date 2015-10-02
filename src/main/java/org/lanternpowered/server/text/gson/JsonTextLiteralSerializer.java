package org.lanternpowered.server.text.gson;

import java.lang.reflect.Type;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class JsonTextLiteralSerializer extends JsonTextBaseSerializer implements JsonSerializer<Text.Literal>, JsonDeserializer<Text.Literal> {

    @Override
    public Text.Literal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return Texts.of(json.getAsString());
        }
        JsonObject json0 = json.getAsJsonObject();
        TextBuilder.Literal builder = Texts.builder(json0.get("text").getAsString());
        this.deserialize(json0, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(Text.Literal src, Type typeOfSrc, JsonSerializationContext context) {
        if (!src.getHoverAction().isPresent() && !src.getClickAction().isPresent() &&
                src.getStyle().isEmpty() && src.getColor().equals(TextColors.NONE)) {
            return new JsonPrimitive(src.getContent());
        }
        JsonObject json = new JsonObject();
        json.addProperty("text", src.getContent());
        this.serialize(json, src, context);
        return json;
    }

}
