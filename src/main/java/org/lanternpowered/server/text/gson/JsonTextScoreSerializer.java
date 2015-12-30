/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.text.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Optional;

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
