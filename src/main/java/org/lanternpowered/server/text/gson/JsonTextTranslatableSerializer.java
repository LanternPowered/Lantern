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

import static org.lanternpowered.server.text.gson.TextConstants.TRANSLATABLE;
import static org.lanternpowered.server.text.gson.TextConstants.TRANSLATABLE_ARGS;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.translation.MinecraftTranslation;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.translation.Translation;

import java.lang.reflect.Type;

public final class JsonTextTranslatableSerializer extends JsonTextBaseSerializer<TranslatableText> {

    private final TranslationManager translationManager;

    JsonTextTranslatableSerializer(TranslationManager translationManager) {
        this.translationManager = translationManager;
    }

    @Override
    public TranslatableText deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();
        final String name = obj.get(TRANSLATABLE).getAsString();
        final Translation translation = this.translationManager.get(name);
        final Object[] arguments;
        if ((element = obj.get(TRANSLATABLE_ARGS)) != null) {
            final Text[] with = context.deserialize(element, Text[].class);
            arguments = new Object[with.length];
            System.arraycopy(with, 0, arguments, 0, with.length);
        } else {
            arguments = new Object[0];
        }
        final TranslatableText.Builder builder = Text.builder(translation, arguments);
        deserialize(obj, builder, context);
        return builder.build();
    }

    @Override
    public JsonElement serialize(TranslatableText src, Type typeOfSrc, JsonSerializationContext context) {
        final Translation translation = src.getTranslation();
        final TranslationContext networkCtx = TranslationContext.current();
        if (networkCtx.forcesTranslations() && !(translation instanceof MinecraftTranslation)) {
            // Take care of the text translations
            if (translation instanceof TextTranslation) {
                return context.serialize(TextTranslation.toText(translation));
            }
            final ImmutableList<Object> arguments = src.getArguments();
            final Object[] rawArguments = arguments.toArray(new Object[arguments.size()]);
            for (int i = 0; i < rawArguments.length; i++) {
                Object object = rawArguments[i];
                if (object instanceof TextRepresentable) {
                    if (!(object instanceof Text)) {
                        object = ((TextRepresentable) object).toText();
                    }
                    rawArguments[i] = LanternTexts.toLegacy((Text) object);
                } else {
                    rawArguments[i] = object.toString();
                }
            }
            final String content = src.getTranslation().get(networkCtx.getLocale(), rawArguments);
            return JsonTextLiteralSerializer.serializeLiteralText(src, content, context, true);
        }
        final JsonObject obj = new JsonObject();
        obj.addProperty(TRANSLATABLE, src.getTranslation().getId());
        final ImmutableList<Object> arguments = src.getArguments();
        if (!arguments.isEmpty()) {
            final JsonArray argumentsArray = new JsonArray();
            for (Object object : arguments) {
                // Only primitive strings and text json is allowed,
                // so we need to convert the objects if possible
                if (object instanceof TextRepresentable) {
                    if (!(object instanceof Text)) {
                        object = ((TextRepresentable) object).toText();
                    }
                    argumentsArray.add(context.serialize(object, Text.class));
                } else {
                    argumentsArray.add(new JsonPrimitive(object.toString()));
                }
            }
            obj.add(TRANSLATABLE_ARGS, argumentsArray);
        }
        serialize(obj, src, context);
        return obj;
    }

}
