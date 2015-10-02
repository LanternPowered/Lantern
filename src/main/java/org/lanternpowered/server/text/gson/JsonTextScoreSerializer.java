package org.lanternpowered.server.text.gson;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class JsonTextScoreSerializer extends JsonTextBaseSerializer implements JsonSerializer<Text.Score>, JsonDeserializer<Text.Score> {

    @Override
    public Text.Score deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        // TODO: Wait for sponge to figure out the format and scoreboard implementation
        return null;
    }

    @Override
    public JsonElement serialize(Text.Score src, Type typeOfSrc, JsonSerializationContext context) {
        // TODO: Verify and modify once we know how sponge will handle it
        JsonObject json = new JsonObject();
        Score score = src.getScore();
        json.addProperty("name", Texts.toPlain(score.getName()));
        Iterator<Objective> it = score.getObjectives().iterator();
        json.addProperty("objective", it.next().getName());
        if (it.hasNext()) {
            JsonArray json0 = new JsonArray();
            while (it.hasNext()) {
                json0.add(new JsonPrimitive(it.next().getName()));
            }
            json.add("extraObjectives", json0);
        }
        Optional<String> override = src.getOverride();
        if (override.isPresent()) {
            json.addProperty("override", override.get());
        }
        json.addProperty("score", Integer.toString(score.getScore()));
        return json;
    }

}
