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
package org.lanternpowered.server.text.gson

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import org.lanternpowered.server.scoreboard.LanternScore
import org.lanternpowered.server.text.LanternTexts.fromLegacy
import org.lanternpowered.server.text.LanternTexts.toLegacy
import org.lanternpowered.server.text.translation.TranslationContext
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.scoreboard.objective.Objective
import org.spongepowered.api.text.ScoreText
import org.spongepowered.api.text.Text
import java.lang.reflect.Type
import java.util.HashSet

internal class JsonTextScoreSerializer : JsonTextBaseSerializer<ScoreText>() {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ScoreText {
        json as JsonObject
        val name = fromLegacy(json[TextConstants.SCORE_NAME].asString)
        // Try to parse the value
        var value = 0
        try {
            value = json[TextConstants.SCORE_VALUE].asString.toInt()
        } catch (ignored: NumberFormatException) {
        }
        val baseObjective = json[TextConstants.SCORE_MAIN_OBJECTIVE].asString
        val objectives = mutableSetOf<Objective>()
        if (baseObjective.isNotEmpty())
            tryAddObjective(baseObjective, objectives)
        json[TextConstants.SCORE_EXTRA_OBJECTIVES]?.let { extraObjectives ->
            for (jsonElement in extraObjectives.asJsonArray)
                tryAddObjective(jsonElement.asString, objectives)
        }
        val override = json[TextConstants.SCORE_OVERRIDE]?.asString
        val score = LanternScore(name)
        // TODO: How to handle the objectives?
        // We cannot add them to the score without attaching the
        // score to the objective
        score.score = value
        val builder = Text.builder(score).override(override)
        deserialize(json, builder, context)
        return builder.build()
    }

    private fun tryAddObjective(objectiveName: String, objectives: Set<Objective>) {
        // TODO: Search all the world scoreboards for objectives
    }

    override fun serialize(src: ScoreText, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        // Lantern:
        // This is a field added by sponge to be able to override
        // the text provided by this component.
        val override = src.override
        // If we are using the networking format and there is an override present, just use
        // the override as a literal text object
        val networkingFormat = TranslationContext.current().forcesTranslations()
        if (networkingFormat && override.isPresent)
            return JsonPrimitive(override.get())
        // There are here some extra fields to represent the (lantern/sponge) score text object,
        // while they are not supported by sponge itself, it seems worth it to provide this
        // This will still remain compatible with vanilla.
        val obj = JsonObject()
        val score = src.score
        obj.addProperty(TextConstants.SCORE_NAME, toLegacy(score.name))
        val it = score.objectives.iterator()
        if (it.hasNext()) {
            obj.addProperty(TextConstants.SCORE_MAIN_OBJECTIVE, it.next().name)
            // Lantern:
            // Provide a list with all the extra objectives that
            // are attached to the score.
            // There is no need to send this to the client.
            if (!networkingFormat) {
                if (it.hasNext()) {
                    val array = JsonArray()
                    while (it.hasNext()) {
                        array.add(JsonPrimitive(it.next().name))
                    }
                    obj.add(TextConstants.SCORE_EXTRA_OBJECTIVES, array)
                }
            }
        } else {
            // This field must always be specified to be valid score json,
            // making it empty will prevent issues
            obj.addProperty(TextConstants.SCORE_MAIN_OBJECTIVE, "")
        }
        override.ifPresent { value -> obj.addProperty(TextConstants.SCORE_OVERRIDE, value) }
        obj.addProperty(TextConstants.SCORE_VALUE, score.score.toString())
        serialize(obj, src, context)
        return obj
    }
}
