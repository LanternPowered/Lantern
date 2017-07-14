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

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Type;

import static org.lanternpowered.server.text.gson.TextConstants.*;

final class JsonTextLiteralSerializer extends JsonTextBaseSerializer implements JsonSerializer<LiteralText>, JsonDeserializer<LiteralText> {

    private final boolean removeComplexity;

    JsonTextLiteralSerializer(boolean removeComplexity) {
        this.removeComplexity = removeComplexity;
    }

    @Override
    public LiteralText deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return Text.of(json.getAsString());
        }
        JsonObject json0 = json.getAsJsonObject();
        LiteralText.Builder builder = Text.builder(json0.get(TEXT).getAsString());
        deserialize(json0, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(LiteralText src, Type typeOfSrc, JsonSerializationContext context) {
        return serializeLiteralText(src, src.getContent(), context, this.removeComplexity);
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
            } else if (removeComplexity && content.isEmpty() && children.size() == 1) {
                return context.serialize(children.get(0));
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
