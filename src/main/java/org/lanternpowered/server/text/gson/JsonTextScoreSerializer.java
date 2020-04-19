/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.text.gson;

import static org.lanternpowered.server.text.gson.TextConstants.SCORE_EXTRA_OBJECTIVES;
import static org.lanternpowered.server.text.gson.TextConstants.SCORE_MAIN_OBJECTIVE;
import static org.lanternpowered.server.text.gson.TextConstants.SCORE_NAME;
import static org.lanternpowered.server.text.gson.TextConstants.SCORE_OVERRIDE;
import static org.lanternpowered.server.text.gson.TextConstants.SCORE_VALUE;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.lanternpowered.server.scoreboard.LanternScore;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.ScoreText;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

final class JsonTextScoreSerializer extends JsonTextBaseSerializer<ScoreText> {

    JsonTextScoreSerializer() {
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
        final boolean networkingFormat = TranslationContext.current().forcesTranslations();
        if (networkingFormat && override.isPresent()) {
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
            if (!networkingFormat) {
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
