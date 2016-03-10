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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.lanternpowered.server.text.LanternTextHelper;
import org.lanternpowered.server.text.LanternTextHelper.RawAction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

abstract class JsonTextBaseSerializer {

    public void deserialize(JsonObject json, Text.Builder builder, JsonDeserializationContext context) throws JsonParseException {
        this.deserialize(json, builder, context, json.has("extra") ? json.getAsJsonArray("extra") : null);
    }

    public void deserialize(JsonObject json, Text.Builder builder, JsonDeserializationContext context, @Nullable JsonArray children)
            throws JsonParseException {
        if (json.has("color")) {
            TextColor color = Sponge.getRegistry().getType(TextColor.class, json.get("color").getAsString()).orElse(null);
            if (color != null) {
                builder.color(color);
            }
        }
        TextStyle style = builder.getStyle();
        if (json.has("bold")) {
            style = style.bold(json.get("bold").getAsBoolean());
        }
        if (json.has("italic")) {
            style = style.italic(json.get("italic").getAsBoolean());
        }
        if (json.has("underlined")) {
            style = style.underline(json.get("underlined").getAsBoolean());
        }
        if (json.has("strikethrough")) {
            style = style.strikethrough(json.get("strikethrough").getAsBoolean());
        }
        if (json.has("obfuscated")) {
            style = style.obfuscated(json.get("obfuscated").getAsBoolean());
        }
        builder.style(style);
        if (children != null) {
            builder.append((Text[]) context.deserialize(children, Text[].class));
        }
        if (json.has("clickEvent")) {
            JsonObject json0 = json.getAsJsonObject("clickEvent");
            if (json0 != null) {
                JsonPrimitive json1 = json0.getAsJsonPrimitive("action");
                JsonPrimitive json2 = json0.getAsJsonPrimitive("value");
                if (json1 != null && json2 != null) {
                    String action = json1.getAsString();
                    String value = json2.getAsString();

                    ClickAction<?> clickAction = LanternTextHelper.parseClickAction(action, value);
                    if (clickAction != null) {
                        builder.onClick(clickAction);
                    }
                }
            }
        }
        if (json.has("hoverEvent")) {
            JsonObject json0 = json.getAsJsonObject("hoverEvent");
            if (json0 != null) {
                JsonPrimitive json1 = json0.getAsJsonPrimitive("action");
                JsonPrimitive json2 = json0.getAsJsonPrimitive("value");
                if (json1 != null && json2 != null) {
                    String action = json1.getAsString();
                    String value = json2.getAsString();

                    HoverAction<?> hoverAction = LanternTextHelper.parseHoverAction(action, value);
                    if (hoverAction != null) {
                        builder.onHover(hoverAction);
                    }
                }
            }
        }
        if (json.has("insertion")) {
            builder.onShiftClick(TextActions.insertText(json.getAsString()));
        }
    }

    public void serialize(JsonObject json, Text text, JsonSerializationContext context) {
        this.serialize(json, text, context, text.getChildren());
    }

    public void serialize(JsonObject json, Text text, JsonSerializationContext context, List<Text> children) {
        TextColor color = text.getColor();
        if (color != TextColors.NONE) {
            json.addProperty("color", color.getId());
        }
        TextStyle style = text.getStyle();
        Optional<Boolean> bold = style.isBold();
        if (bold.isPresent()) {
            json.addProperty("bold", bold.get());
        }
        Optional<Boolean> italic = style.isItalic();
        if (italic.isPresent()) {
            json.addProperty("italic", italic.get());
        }
        Optional<Boolean> underlined = style.hasUnderline();
        if (underlined.isPresent()) {
            json.addProperty("underlined", underlined.get());
        }
        Optional<Boolean> strikethrough = style.hasStrikethrough();
        if (strikethrough.isPresent()) {
            json.addProperty("strikethrough", strikethrough.get());
        }
        Optional<Boolean> obfuscated = style.isObfuscated();
        if (obfuscated.isPresent()) {
            json.addProperty("obfuscated", obfuscated.get());
        }
        if (!children.isEmpty()) {
            json.add("extra", context.serialize(children.toArray(new Text[children.size()]), Text[].class));
        }
        ClickAction<?> clickAction = text.getClickAction().orElse(null);
        if (clickAction != null) {
            RawAction raw = LanternTextHelper.raw(clickAction);

            JsonObject json0 = new JsonObject();
            json0.addProperty("action", raw.getAction());
            json0.addProperty("value", raw.getValueAsString());

            json.add("clickEvent", json0);
        }
        HoverAction<?> hoverAction = text.getHoverAction().orElse(null);
        if (hoverAction != null) {
            RawAction raw = LanternTextHelper.raw(hoverAction);

            JsonObject json0 = new JsonObject();
            json0.addProperty("action", raw.getAction());
            json0.addProperty("value", raw.getValueAsString());

            json.add("hoverEvent", json0);
        }
        ShiftClickAction<?> shiftClickAction = text.getShiftClickAction().orElse(null);
        if (shiftClickAction != null && shiftClickAction instanceof ShiftClickAction.InsertText) {
            json.addProperty("insertion", ((ShiftClickAction.InsertText) shiftClickAction).getResult());
        }
    }

}
