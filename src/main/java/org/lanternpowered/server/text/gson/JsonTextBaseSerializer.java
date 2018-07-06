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

import static org.lanternpowered.server.text.gson.TextConstants.BOLD;
import static org.lanternpowered.server.text.gson.TextConstants.CHILDREN;
import static org.lanternpowered.server.text.gson.TextConstants.CLICK_EVENT;
import static org.lanternpowered.server.text.gson.TextConstants.COLOR;
import static org.lanternpowered.server.text.gson.TextConstants.EVENT_ACTION;
import static org.lanternpowered.server.text.gson.TextConstants.EVENT_VALUE;
import static org.lanternpowered.server.text.gson.TextConstants.HOVER_EVENT;
import static org.lanternpowered.server.text.gson.TextConstants.INSERTION;
import static org.lanternpowered.server.text.gson.TextConstants.ITALIC;
import static org.lanternpowered.server.text.gson.TextConstants.OBFUSCATED;
import static org.lanternpowered.server.text.gson.TextConstants.STRIKETHROUGH;
import static org.lanternpowered.server.text.gson.TextConstants.UNDERLINE;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.lanternpowered.server.text.gson.JsonTextEventHelper.RawAction;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;

import java.util.List;

abstract class JsonTextBaseSerializer<T extends Text> implements JsonSerializer<T>, JsonDeserializer<T> {

    static void deserialize(JsonObject json, Text.Builder builder, JsonDeserializationContext context) throws JsonParseException {
        JsonElement element;
        if ((element = json.get(COLOR)) != null) {
            Sponge.getRegistry().getType(TextColor.class, CatalogKey.resolve(element.getAsString())).ifPresent(builder::color);
        }
        TextStyle style = builder.getStyle();
        if ((element = json.get(BOLD)) != null) {
            style = style.bold(element.getAsBoolean());
        }
        if ((element = json.get(ITALIC)) != null) {
            style = style.italic(element.getAsBoolean());
        }
        if ((element = json.get(UNDERLINE)) != null) {
            style = style.underline(element.getAsBoolean());
        }
        if ((element = json.get(STRIKETHROUGH)) != null) {
            style = style.strikethrough(element.getAsBoolean());
        }
        if ((element = json.get(OBFUSCATED)) != null) {
            style = style.obfuscated(element.getAsBoolean());
        }
        builder.style(style);
        final JsonArray children = json.getAsJsonArray(CHILDREN);
        if (children != null) {
            builder.append((Text[]) context.deserialize(children, Text[].class));
        }
        if ((element = json.get(CLICK_EVENT)) != null) {
            final JsonObject jsonClickEvent = element.getAsJsonObject();
            if (jsonClickEvent != null) {
                final JsonPrimitive jsonEventAction = jsonClickEvent.getAsJsonPrimitive(EVENT_ACTION);
                final JsonPrimitive jsonEventValue = jsonClickEvent.getAsJsonPrimitive(EVENT_VALUE);
                if (jsonEventAction != null && jsonEventValue != null) {
                    final String action = jsonEventAction.getAsString();
                    final String value = jsonEventValue.getAsString();

                    final ClickAction<?> clickAction = JsonTextEventHelper.parseClickAction(action, value);
                    if (clickAction != null) {
                        builder.onClick(clickAction);
                    }
                }
            }
        }
        if ((element = json.get(HOVER_EVENT)) != null) {
            final JsonObject jsonHoverEvent = element.getAsJsonObject();
            if (jsonHoverEvent != null) {
                final JsonPrimitive jsonEventAction = jsonHoverEvent.getAsJsonPrimitive(EVENT_ACTION);
                final JsonPrimitive jsonEventValue = jsonHoverEvent.getAsJsonPrimitive(EVENT_VALUE);
                if (jsonEventAction != null && jsonEventValue != null) {
                    final String action = jsonEventAction.getAsString();
                    final String value = jsonEventValue.getAsString();
                    builder.onHover(JsonTextEventHelper.parseHoverAction(action, value));
                }
            }
        }
        if ((element = json.get(INSERTION)) != null) {
            builder.onShiftClick(TextActions.insertText(element.getAsString()));
        }
    }

    static void serialize(JsonObject json, Text text, JsonSerializationContext context) {
        final TextColor color = text.getColor();
        if (color != TextColors.NONE) {
            json.addProperty(COLOR, color.getKey().toString());
        }
        final TextStyle style = text.getStyle();
        style.isBold().ifPresent(v -> json.addProperty(BOLD, v));
        style.isItalic().ifPresent(v -> json.addProperty(ITALIC, v));
        style.hasUnderline().ifPresent(v -> json.addProperty(UNDERLINE, v));
        style.hasStrikethrough().ifPresent(v -> json.addProperty(STRIKETHROUGH, v));
        style.isObfuscated().ifPresent(v -> json.addProperty(OBFUSCATED, v));
        final List<Text> children = text.getChildren();
        if (!children.isEmpty()) {
            json.add(CHILDREN, context.serialize(children.toArray(new Text[children.size()]), Text[].class));
        }
        text.getClickAction().ifPresent(clickAction -> {
            final RawAction raw = JsonTextEventHelper.raw(clickAction);

            final JsonObject jsonEvent = new JsonObject();
            jsonEvent.addProperty(EVENT_ACTION, raw.action);
            jsonEvent.addProperty(EVENT_VALUE, raw.value);

            json.add(CLICK_EVENT, jsonEvent);
        });
        text.getHoverAction().ifPresent(clickAction -> {
            final RawAction raw = JsonTextEventHelper.raw(clickAction);

            final JsonObject jsonEvent = new JsonObject();
            jsonEvent.addProperty(EVENT_ACTION, raw.action);
            jsonEvent.addProperty(EVENT_VALUE, raw.value);

            json.add(HOVER_EVENT, jsonEvent);
        });
        text.getShiftClickAction().ifPresent(shiftClickAction -> {
            if (shiftClickAction instanceof ShiftClickAction.InsertText) {
                json.addProperty(INSERTION, ((ShiftClickAction.InsertText) shiftClickAction).getResult());
            }
        });
    }

}
