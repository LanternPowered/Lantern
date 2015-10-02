package org.lanternpowered.server.text.gson;

import java.lang.reflect.Type;

import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class JsonTextTranslatableSerializer extends JsonTextBaseSerializer implements JsonSerializer<Text.Translatable>, JsonDeserializer<Text.Translatable> {

    private final TranslationManager translationManager;

    public JsonTextTranslatableSerializer(TranslationManager translationManager) {
        this.translationManager = translationManager;
    }

    @Override
    public Text.Translatable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        String name = json0.get("translate").getAsString();
        Translation translation = this.translationManager.get(name);
        Object[] arguments;
        if (json0.has("with")) {
            Text[] with = context.deserialize(json0.get("with"), Text[].class);
            arguments = new Object[with.length];
            for (int i = 0; i < with.length; i++) {
                arguments[i] = with[i];
            }
        } else {
            arguments = new Object[0];
        }
        TextBuilder.Translatable builder = Texts.builder(translation, arguments);
        this.deserialize(json0, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(Text.Translatable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("translate", src.getTranslation().getId());
        ImmutableList<Object> arguments = src.getArguments();
        if (!arguments.isEmpty()) {
            json.add("with", context.serialize(arguments));
        }
        this.serialize(json, src, context);
        return json;
    }

}
