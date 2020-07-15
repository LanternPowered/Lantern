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

import org.lanternpowered.api.ResourceKeys
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.text.selector.LanternSelectorType
import org.spongepowered.api.text.selector.SelectorType
import org.spongepowered.api.text.selector.SelectorTypes

import java.util.HashMap

object SelectorTypeRegistryModule : AdditionalPluginCatalogRegistryModule<SelectorType>(SelectorTypes::class) {

    private val byCode = HashMap<String, LanternSelectorType>()

    override fun registerDefaults() {
        register(LanternSelectorType(ResourceKeys.minecraft("all_players"), "a"))
        register(LanternSelectorType(ResourceKeys.minecraft("all_entities"), "e"))
        register(LanternSelectorType(ResourceKeys.minecraft("nearest_player"), "p"))
        register(LanternSelectorType(ResourceKeys.minecraft("random"), "r"))
    }

    override fun doRegistration(selectorType: SelectorType, disallowInbuiltPluginIds: Boolean) {
        selectorType as LanternSelectorType
        check(selectorType.code !in this.byCode) { "The code ${selectorType.code} is already in use."}
        super.doRegistration(selectorType, disallowInbuiltPluginIds)
        this.byCode[selectorType.code] = selectorType
    }

    fun getByCode(code: String): LanternSelectorType? = this.byCode[code]
}
