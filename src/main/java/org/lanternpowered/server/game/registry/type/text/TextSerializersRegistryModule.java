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

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.lanternpowered.server.text.FormattingCodeTextSerializer;
import org.lanternpowered.server.text.LanternTextSerializerFactory;
import org.lanternpowered.server.text.PlainTextSerializer;
import org.lanternpowered.server.text.TextConstants;
import org.lanternpowered.server.text.gson.LanternJsonTextSerializer;
import org.lanternpowered.server.text.xml.XmlTextSerializer;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.serializer.TextSerializerFactory;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Collections;
import java.util.List;

@RegistrationDependency({ TranslationManagerRegistryModule.class, TextColorRegistryModule.class, TextStyleRegistryModule.class })
public final class TextSerializersRegistryModule implements RegistryModule, CatalogMappingDataHolder {

    private TextSerializerFactory textSerializerFactory;

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        this.textSerializerFactory = new LanternTextSerializerFactory();
    }

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        return Collections.singletonList(new CatalogMappingData(TextSerializers.class, ImmutableMap.<String, Object>builder()
                .put("plain", new PlainTextSerializer())
                .put("legacy_formatting_code", new FormattingCodeTextSerializer(TextConstants.LEGACY_CHAR))
                .put("formatting_code", new FormattingCodeTextSerializer('&'))
                .put("json", new LanternJsonTextSerializer(Lantern.getGame().getRegistry().getRegistryModule(
                        TranslationManagerRegistryModule.class).get().getTranslationManager()))
                .put("text_xml", new XmlTextSerializer())
                .build()));
    }

    public TextSerializerFactory getTextSerializerFactory() {
        return this.textSerializerFactory;
    }

}
