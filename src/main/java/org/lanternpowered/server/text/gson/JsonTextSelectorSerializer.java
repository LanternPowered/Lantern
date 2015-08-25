package org.lanternpowered.server.text.gson;

import java.lang.reflect.Type;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.Selectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonTextSelectorSerializer extends JsonTextBaseSerializer implements JsonSerializer<Text.Selector>, JsonDeserializer<Text.Selector> {

    @Override
    public Text.Selector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        Selector selector = Selectors.parse(json0.get("selector").getAsString());
        TextBuilder.Selector builder = Texts.builder(selector);
        this.deserialize(json0, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(Text.Selector src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("selector", src.getSelector().toPlain());
        this.serialize(json, src, context);
        return json;
    }

}
