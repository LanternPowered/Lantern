/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.lang.reflect.Type;
import java.util.Locale;

import org.lanternpowered.server.text.translation.MinecraftTranslation;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class JsonTextTranslatableSerializer extends JsonTextBaseSerializer implements JsonSerializer<Text.Translatable>, JsonDeserializer<Text.Translatable> {

    private final static ThreadLocal<Locale> currentLocale = ThreadLocal.withInitial(() -> Locale.ENGLISH);

    /**
     * Sets the current locale that should be used to translate all
     * the text components if {@link #translateNonMinecraft} is set to true.
     * 
     * <p>This will only be applied to the current thread, so this will
     * can be used in concurrent environments.</p>
     * 
     * @param locale the locale
     */
    public static void setCurrentLocale(Locale locale) {
        currentLocale.set(locale);
    }

    public static Locale getCurrentLocale() {
        return currentLocale.get();
    }

    private final TranslationManager translationManager;
    private final boolean translateNonMinecraft;

    public JsonTextTranslatableSerializer(TranslationManager translationManager, boolean translateNonMinecraft) {
        this.translateNonMinecraft = translateNonMinecraft;
        this.translationManager = translationManager;
    }

    @Override
    public Text.Translatable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        String name = json0.get("translate").getAsString();
        Translation translation = this.translationManager.get(name);
        Object[] arguments;
        if (json0.has("with")) {
            Text[] with = context.deserialize(json0.get("with"), Text[].class);
            arguments = new Object[with.length];
            for (int i = 0; i < with.length; i++) {
                arguments[i] = with[i];
            }
        } else {
            arguments = new Object[0];
        }
        TextBuilder.Translatable builder = Texts.builder(translation, arguments);
        this.deserialize(json0, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(Text.Translatable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        Translation translation = src.getTranslation();
        if (this.translateNonMinecraft && !(translation instanceof MinecraftTranslation)) {
            return new JsonPrimitive(src.getTranslation().get(currentLocale.get(), src.getArguments().toArray()));
        }
        json.addProperty("translate", src.getTranslation().getId());
        ImmutableList<Object> arguments = src.getArguments();
        if (!arguments.isEmpty()) {
            json.add("with", context.serialize(arguments));
        }
        this.serialize(json, src, context);
        return json;
    }

}
