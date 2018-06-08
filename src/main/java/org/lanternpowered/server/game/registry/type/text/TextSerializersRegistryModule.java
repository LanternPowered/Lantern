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
package org.lanternpowered.server.game.registry.type.text;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.lanternpowered.server.text.FormattingCodeTextSerializer;
import org.lanternpowered.server.text.PlainTextSerializer;
import org.lanternpowered.server.text.TextConstants;
import org.lanternpowered.server.text.gson.LanternJsonTextSerializer;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializerFactory;
import org.spongepowered.api.text.serializer.TextSerializers;

@RegistrationDependency({ TranslationManagerRegistryModule.class, TextFormatRegistryModule.class })
public final class TextSerializersRegistryModule extends AdditionalPluginCatalogRegistryModule<TextSerializer> {

    private final Char2ObjectMap<org.spongepowered.api.text.serializer.FormattingCodeTextSerializer> formattingCodeSerializers =
            new Char2ObjectOpenHashMap<>();

    @SuppressWarnings("deprecation")
    private final TextSerializerFactory textSerializerFactory = legacyChar -> {
        if (legacyChar == TextConstants.LEGACY_CHAR) {
            return TextSerializers.LEGACY_FORMATTING_CODE;
        } else if (legacyChar == TextSerializers.FORMATTING_CODE.getCharacter()) {
            return TextSerializers.FORMATTING_CODE;
        } else {
            synchronized (this.formattingCodeSerializers) {
                org.spongepowered.api.text.serializer.FormattingCodeTextSerializer serializer = this.formattingCodeSerializers.get(legacyChar);
                if (serializer == null) {
                    this.formattingCodeSerializers.put(legacyChar, serializer = new FormattingCodeTextSerializer(
                            "minecraft", "formatting_code_" + legacyChar, legacyChar));
                }
                return serializer;
            }
        }
    };

    public TextSerializersRegistryModule() {
        super(TextSerializers.class);
    }

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        register(new PlainTextSerializer("minecraft", "plain"));
        register(new FormattingCodeTextSerializer("minecraft", "legacy_formatting_code", TextConstants.LEGACY_CHAR));
        register(new FormattingCodeTextSerializer("minecraft", "formatting_code", '&'));
        register(new LanternJsonTextSerializer("minecraft", "json", Lantern.getGame().getRegistry().getRegistryModule(
                TranslationManagerRegistryModule.class).get().getTranslationManager()));
    }

    @Override
    protected void register(TextSerializer catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        if (catalogType instanceof org.spongepowered.api.text.serializer.FormattingCodeTextSerializer) {
            final org.spongepowered.api.text.serializer.FormattingCodeTextSerializer serializer =
                    (org.spongepowered.api.text.serializer.FormattingCodeTextSerializer) catalogType;
            synchronized (this.formattingCodeSerializers) {
                if (this.formattingCodeSerializers.containsKey(serializer.getCharacter())) {
                    Lantern.getLogger().warn("There is already a FormattingCodeTextSerializer registered for the character: {}."
                                    + "The original {} will be overridden by {}",
                            serializer.getCharacter(), this.formattingCodeSerializers.get(serializer.getCharacter()).getId(), serializer.getId());
                }
                this.formattingCodeSerializers.put(serializer.getCharacter(), serializer);
            }
        }
    }

    public TextSerializerFactory getTextSerializerFactory() {
        return this.textSerializerFactory;
    }
}
