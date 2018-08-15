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
package org.lanternpowered.server.game.registry.type.text

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.EarlyRegistration
import org.lanternpowered.server.text.LanternFormattingCodeTextSerializer
import org.lanternpowered.server.text.PlainTextSerializer
import org.lanternpowered.server.text.TextConstants
import org.lanternpowered.server.text.gson.LanternJsonTextSerializer
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.registry.util.RegistrationDependency
import org.spongepowered.api.text.serializer.TextSerializer
import org.spongepowered.api.text.serializer.TextSerializers

@RegistrationDependency(TranslationManagerRegistryModule::class)
object TextSerializerRegistryModule : AdditionalPluginCatalogRegistryModule<TextSerializer>(TextSerializers::class) {

    @EarlyRegistration
    override fun registerDefaults() {
        register(PlainTextSerializer(CatalogKey.minecraft("plain")))
        register(LanternFormattingCodeTextSerializer(CatalogKey.minecraft("legacy_formatting_code"), TextConstants.LEGACY_CHAR))
        register(LanternFormattingCodeTextSerializer(CatalogKey.minecraft("formatting_code"), '&'))
        register(LanternJsonTextSerializer(CatalogKey.minecraft("json"), TranslationManagerRegistryModule.translationManager))
    }

    public override fun <A : TextSerializer?> register(catalogType: A): A = super.register(catalogType)
}
