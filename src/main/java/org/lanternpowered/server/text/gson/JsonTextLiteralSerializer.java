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

import static org.lanternpowered.server.text.gson.TextConstants.TEXT;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Type;

final class JsonTextLiteralSerializer extends JsonTextBaseSerializer<LiteralText> {

    JsonTextLiteralSerializer() {
    }

    @Override
    public LiteralText deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return Text.of(json.getAsString());
        }
        final JsonObject json0 = json.getAsJsonObject();
        final LiteralText.Builder builder = Text.builder(json0.get(TEXT).getAsString());
        deserialize(json0, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(LiteralText src, Type typeOfSrc, JsonSerializationContext context) {
        return serializeLiteralText(src, src.getContent(), context, TranslationContext.current().forcesTranslations());
    }

    static JsonElement serializeLiteralText(Text text, String content, JsonSerializationContext context, boolean removeComplexity) {
        final boolean noActionsAndStyle = areActionsAndStyleEmpty(text);
        final ImmutableList<Text> children = text.getChildren();

        if (noActionsAndStyle) {
            if (children.isEmpty()) {
                return new JsonPrimitive(content);
                // Try to make the serialized text object less complex,
                // like text objects nested in a lot of other
                // text objects, this seems to happen a lot
            } else if (removeComplexity && content.isEmpty()) {
                if (children.size() == 1) {
                    return context.serialize(children.get(0));
                } else {
                    return context.serialize(children);
                }
            }
        }

        final JsonObject json = new JsonObject();
        json.addProperty(TEXT, content);
        serialize(json, text, context);
        return json;
    }

    /**
     * Gets whether there are no styles or actions applied to the specified {@link Text}.
     *
     * @param text The text
     * @return Are actions and styles empty
     */
    private static boolean areActionsAndStyleEmpty(Text text) {
        return !text.getHoverAction().isPresent() && !text.getClickAction().isPresent() && !text.getShiftClickAction().isPresent() &&
                text.getStyle().isEmpty() && text.getColor().equals(TextColors.NONE);
    }
}
