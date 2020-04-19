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

@RegistrationDependency(TranslationManagerRegistryModule::class, TextStyleRegistryModule::class, TextColorRegistryModule::class)
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
