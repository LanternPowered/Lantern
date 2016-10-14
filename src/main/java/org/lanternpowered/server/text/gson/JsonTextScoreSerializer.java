/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

final class JsonTextScoreSerializer extends JsonTextBaseSerializer implements JsonSerializer<ScoreText>, JsonDeserializer<ScoreText> {

    private final boolean networkingFormat;

    JsonTextScoreSerializer(boolean networkingFormat) {
        this.networkingFormat = networkingFormat;
    }

    @Override
    public ScoreText deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();
        final Text name = LanternTexts.fromLegacy(obj.get(SCORE_NAME).getAsString());
        // Try to parse the value
        int value = 0;
        try {
            value = Integer.parseInt(obj.get(SCORE_VALUE).getAsString());
        } catch (NumberFormatException ignored) {
        }
        final String baseObjective = obj.get(SCORE_MAIN_OBJECTIVE).getAsString();
        final Set<Objective> objectives = new HashSet<>();
        if (!baseObjective.isEmpty()) {
            this.tryAddObjective(baseObjective, objectives);
        }
        if ((element = obj.get(SCORE_EXTRA_OBJECTIVES)) != null) {
            final JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                this.tryAddObjective(jsonElement.getAsString(), objectives);
            }
        }
        String override = null;
        if ((element = obj.get(SCORE_OVERRIDE)) != null) {
            override = element.getAsString();
        }

        final Score score = new LanternScore(name);
        // TODO: How to handle the objectives?
        // We cannot add them to the score without attaching the
        // score to the objective
        score.setScore(value);

        final ScoreText.Builder builder = Text.builder(score).override(override);
        deserialize(obj, builder, context);
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
        final Optional<String> override = src.getOverride();
        // If we are using the networking format and there is an override present, just use
        // the override as a literal text object
        if (this.networkingFormat && override.isPresent()) {
            return new JsonPrimitive(override.get());
        }
        // There are here some extra fields to represent the (lantern/sponge) score text object,
        // while they are not supported by sponge itself, it seems worth it to provide this
        // This will still remain compatible with vanilla.
        final JsonObject obj = new JsonObject();
        final Score score = src.getScore();
        obj.addProperty(SCORE_NAME, LanternTexts.toLegacy(score.getName()));
        final Iterator<Objective> it = score.getObjectives().iterator();
        if (it.hasNext()) {
            obj.addProperty(SCORE_MAIN_OBJECTIVE, it.next().getName());
            // Lantern:
            // Provide a list with all the extra objectives that
            // are attached to the score.
            // There is no need to send this to the client.
            if (!this.networkingFormat) {
                if (it.hasNext()) {
                    final JsonArray array = new JsonArray();
                    while (it.hasNext()) {
                        array.add(new JsonPrimitive(it.next().getName()));
                    }
                    obj.add(SCORE_EXTRA_OBJECTIVES, array);
                }
            }
        } else {
            // This field must always be specified to be valid score json,
            // making it empty will prevent issues
            obj.addProperty(SCORE_MAIN_OBJECTIVE, "");
        }
        override.ifPresent(v -> obj.addProperty(SCORE_OVERRIDE, override.get()));
        obj.addProperty(SCORE_VALUE, Integer.toString(score.getScore()));
        serialize(obj, src, context);
        return obj;
    }

}
