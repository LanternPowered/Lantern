/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import static org.lanternpowered.server.text.gson.TextConstants.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.server.scoreboard.LanternScore;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public final class JsonTextScoreSerializer extends JsonTextBaseSerializer implements JsonSerializer<ScoreText>, JsonDeserializer<ScoreText> {

    private final boolean networkingFormat;

    public JsonTextScoreSerializer(boolean networkingFormat) {
        this.networkingFormat = networkingFormat;
    }

    @Override
    public ScoreText deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        Text name = LanternTexts.fromLegacy(json0.get(SCORE_NAME).getAsString());
        // Try to parse the value
        int value;
        try {
            value = Integer.parseInt(json0.get(SCORE_VALUE).getAsString());
        } catch (NumberFormatException e) {
            value = 0;
        }
        String baseObjective = json0.get(SCORE_MAIN_OBJECTIVE).getAsString();
        Set<Objective> objectives = new HashSet<>();
        if (!baseObjective.isEmpty()) {
            this.tryAddObjective(baseObjective, objectives);
        }
        if (json0.has(SCORE_EXTRA_OBJECTIVES)) {
            JsonArray array = json0.getAsJsonArray(SCORE_EXTRA_OBJECTIVES);
            for (JsonElement jsonElement : array) {
                this.tryAddObjective(jsonElement.getAsString(), objectives);
            }
        }
        String override = null;
        if (json0.has(SCORE_OVERRIDE)) {
            override = json0.get(SCORE_OVERRIDE).getAsString();
        }

        Score score = new LanternScore(name);
        // TODO: How to handle the objectives?
        // We cannot add them to the score without attaching the
        // score to the objective
        score.setScore(value);

        ScoreText.Builder builder = Text.builder(score).override(override);
        deserialize(json0, builder, context);
        return builder.build();
    }

    private void tryAddObjective(String objectiveName, Set<Objective> objectives) {
        // TODO: Search all the world scoreboards for objectives
    }

    @Override
    public JsonElement serialize(ScoreText src, Type typeOfSrc, JsonSerializationContext context) {
        // Lantern:
        // This is a field added by sponge to be able to override
        // the text provided by this component.
        Optional<String> override = src.getOverride();
        // If we are using the networking format and there is an override present, just use
        // the override as a literal text object
        if (this.networkingFormat && override.isPresent()) {
            return new JsonPrimitive(override.get());
        }
        // There are here some extra fields to represent the (lantern/sponge) score text object,
        // while they are not supported by sponge itself, it seems worth it to provide this
        // This will still remain compatible with vanilla.
        JsonObject json = new JsonObject();
        Score score = src.getScore();
        json.addProperty(SCORE_NAME, LanternTexts.toLegacy(score.getName()));
        Iterator<Objective> it = score.getObjectives().iterator();
        if (it.hasNext()) {
            json.addProperty(SCORE_MAIN_OBJECTIVE, it.next().getName());
            // Lantern:
            // Provide a list with all the extra objectives that
            // are attached to the score.
            // There is no need to send this to the client.
            if (!this.networkingFormat) {
                if (it.hasNext()) {
                    JsonArray json0 = new JsonArray();
                    while (it.hasNext()) {
                        json0.add(new JsonPrimitive(it.next().getName()));
                    }
                    json.add(SCORE_EXTRA_OBJECTIVES, json0);
                }
            }
        } else {
            // This field must always be specified to be valid score json,
            // making it empty will prevent issues
            json.addProperty(SCORE_MAIN_OBJECTIVE, "");
        }
        override.ifPresent(v -> json.addProperty(SCORE_OVERRIDE, override.get()));
        json.addProperty(SCORE_VALUE, Integer.toString(score.getScore()));
        serialize(json, src, context);
        return json;
    }

}
