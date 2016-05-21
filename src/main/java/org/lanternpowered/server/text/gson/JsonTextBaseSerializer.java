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

import javax.annotation.Nullable;

abstract class JsonTextBaseSerializer {

    public static void deserialize(JsonObject json, Text.Builder builder, JsonDeserializationContext context) throws JsonParseException {
        deserialize(json, builder, context, json.has(CHILDREN) ? json.getAsJsonArray(CHILDREN) : null);
    }

    public static void deserialize(JsonObject json, Text.Builder builder, JsonDeserializationContext context, @Nullable JsonArray children)
            throws JsonParseException {
        if (json.has(COLOR)) {
            Sponge.getRegistry().getType(TextColor.class, json.get(COLOR).getAsString()).ifPresent(builder::color);
        }
        TextStyle style = builder.getStyle();
        if (json.has(BOLD)) {
            style = style.bold(json.get(BOLD).getAsBoolean());
        }
        if (json.has(ITALIC)) {
            style = style.italic(json.get(ITALIC).getAsBoolean());
        }
        if (json.has(UNDERLINE)) {
            style = style.underline(json.get(UNDERLINE).getAsBoolean());
        }
        if (json.has(STRIKETHROUGH)) {
            style = style.strikethrough(json.get(STRIKETHROUGH).getAsBoolean());
        }
        if (json.has(OBFUSCATED)) {
            style = style.obfuscated(json.get(OBFUSCATED).getAsBoolean());
        }
        builder.style(style);
        if (children != null) {
            builder.append((Text[]) context.deserialize(children, Text[].class));
        }
        if (json.has(CLICK_EVENT)) {
            JsonObject json0 = json.getAsJsonObject(CLICK_EVENT);
            if (json0 != null) {
                JsonPrimitive json1 = json0.getAsJsonPrimitive(EVENT_ACTION);
                JsonPrimitive json2 = json0.getAsJsonPrimitive(EVENT_VALUE);
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
        if (json.has(HOVER_EVENT)) {
            JsonObject json0 = json.getAsJsonObject(HOVER_EVENT);
            if (json0 != null) {
                JsonPrimitive json1 = json0.getAsJsonPrimitive(EVENT_ACTION);
                JsonPrimitive json2 = json0.getAsJsonPrimitive(EVENT_VALUE);
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
        if (json.has(INSERTION)) {
            builder.onShiftClick(TextActions.insertText(json.getAsString()));
        }
    }

    public static void serialize(JsonObject json, Text text, JsonSerializationContext context) {
        serialize(json, text, context, text.getChildren());
    }

    public static void serialize(JsonObject json, Text text, JsonSerializationContext context, List<Text> children) {
        TextColor color = text.getColor();
        if (color != TextColors.NONE) {
            json.addProperty(COLOR, color.getId());
        }
        TextStyle style = text.getStyle();
        style.isBold().ifPresent(v -> json.addProperty(BOLD, v));
        style.isItalic().ifPresent(v -> json.addProperty(ITALIC, v));
        style.hasUnderline().ifPresent(v -> json.addProperty(UNDERLINE, v));
        style.hasStrikethrough().ifPresent(v -> json.addProperty(STRIKETHROUGH, v));
        style.isObfuscated().ifPresent(v -> json.addProperty(OBFUSCATED, v));
        if (!children.isEmpty()) {
            json.add(CHILDREN, context.serialize(children.toArray(new Text[children.size()]), Text[].class));
        }
        text.getClickAction().ifPresent(clickAction -> {
            RawAction raw = LanternTextHelper.raw(clickAction);

            JsonObject json0 = new JsonObject();
            json0.addProperty(EVENT_ACTION, raw.getAction());
            json0.addProperty(EVENT_VALUE, raw.getValueAsString());

            json.add(CLICK_EVENT, json0);
        });
        text.getHoverAction().ifPresent(clickAction -> {
            RawAction raw = LanternTextHelper.raw(clickAction);

            JsonObject json0 = new JsonObject();
            json0.addProperty(EVENT_ACTION, raw.getAction());
            json0.addProperty(EVENT_VALUE, raw.getValueAsString());

            json.add(HOVER_EVENT, json0);
        });
        text.getShiftClickAction().ifPresent(shiftClickAction -> {
            if (shiftClickAction instanceof ShiftClickAction.InsertText) {
                json.addProperty(INSERTION, ((ShiftClickAction.InsertText) shiftClickAction).getResult());
            }
        });
    }

}
