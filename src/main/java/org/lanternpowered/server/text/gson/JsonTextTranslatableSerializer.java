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

import static org.lanternpowered.server.text.gson.TextConstants.TRANSLATABLE;
import static org.lanternpowered.server.text.gson.TextConstants.TRANSLATABLE_ARGS;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.netty.util.concurrent.FastThreadLocal;
import org.lanternpowered.server.text.LanternTextSerializer;
import org.lanternpowered.server.text.translation.MinecraftTranslation;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.lanternpowered.server.util.FastThreadLocals;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.translation.Translation;

import java.lang.reflect.Type;
import java.util.Locale;

public final class JsonTextTranslatableSerializer extends JsonTextBaseSerializer implements JsonSerializer<TranslatableText>,
        JsonDeserializer<TranslatableText> {

    private final static FastThreadLocal<Locale> currentLocale = FastThreadLocals.withInitial(() -> Locale.ENGLISH);

    /**
     * Sets the current locale that should be used to translate all
     * the text components if {@link #networkingFormat} is set to true.
     * 
     * <p>This will only be applied to the current thread, so this will
     * can be used in concurrent environments.</p>
     * 
     * @param locale the locale
     */
    public static void setCurrentLocale(Locale locale) {
        currentLocale.set(locale);
    }

    /**
     * Removes the current locale that is attached to the thread, toggling
     * the locale back to the default.
     */
    public static void removeCurrentLocale() {
        currentLocale.remove();
    }

    public static Locale getCurrentLocale() {
        return currentLocale.get();
    }

    private final TranslationManager translationManager;
    private final boolean networkingFormat;
    private final boolean removeComplexity;

    public JsonTextTranslatableSerializer(TranslationManager translationManager, boolean networkingFormat, boolean removeComplexity) {
        this.translationManager = translationManager;
        this.networkingFormat = networkingFormat;
        this.removeComplexity = removeComplexity;
    }

    @Override
    public TranslatableText deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json0 = json.getAsJsonObject();
        String name = json0.get(TRANSLATABLE).getAsString();
        Translation translation = this.translationManager.get(name);
        Object[] arguments;
        if (json0.has(TRANSLATABLE_ARGS)) {
            Text[] with = context.deserialize(json0.get(TRANSLATABLE_ARGS), Text[].class);
            arguments = new Object[with.length];
            System.arraycopy(with, 0, arguments, 0, with.length);
        } else {
            arguments = new Object[0];
        }
        TranslatableText.Builder builder = Text.builder(translation, arguments);
        deserialize(json0, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(TranslatableText src, Type typeOfSrc, JsonSerializationContext context) {
        final Translation translation = src.getTranslation();
        if (this.networkingFormat && !(translation instanceof MinecraftTranslation)) {
            final ImmutableList<Object> arguments = src.getArguments();
            final Object[] rawArguments = arguments.toArray(new Object[arguments.size()]);
            final Locale locale = currentLocale.get();
            for (int i = 0; i < rawArguments.length; i++) {
                Object object = rawArguments[i];
                if (object instanceof Text || object instanceof Text.Builder || object instanceof TextRepresentable) {
                    if (object instanceof Text) {
                        // Ignore
                    } else if (object instanceof Text.Builder) {
                        object = ((Text.Builder) object).build();
                    } else {
                        object = ((TextRepresentable) object).toText();
                    }
                    rawArguments[i] = ((LanternTextSerializer) TextSerializers.LEGACY_FORMATTING_CODE).serialize((Text) object, locale);
                } else {
                    rawArguments[i] = object.toString();
                }
            }
            String content = src.getTranslation().get(locale, rawArguments);
            return JsonTextLiteralSerializer.serializeLiteralText(src, content, context, this.removeComplexity);
        }
        JsonObject json = new JsonObject();
        json.addProperty(TRANSLATABLE, src.getTranslation().getId());
        ImmutableList<Object> arguments = src.getArguments();
        if (!arguments.isEmpty()) {
            JsonArray argumentsArray = new JsonArray();
            for (Object object : arguments) {
                // Only primitive strings and text json is allowed,
                // so we need to convert the objects if possible
                if (object instanceof Text || object instanceof Text.Builder || object instanceof TextRepresentable) {
                    if (object instanceof Text) {
                        // Ignore
                    } else if (object instanceof Text.Builder) {
                        object = ((Text.Builder) object).build();
                    } else {
                        object = ((TextRepresentable) object).toText();
                    }
                    argumentsArray.add(context.serialize(object, Text.class));
                } else {
                    argumentsArray.add(new JsonPrimitive(object.toString()));
                }
            }
            json.add(TRANSLATABLE_ARGS, argumentsArray);
        }
        serialize(json, src, context);
        return json;
    }

}
