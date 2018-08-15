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

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.text.selector.LanternSelectorType
import org.spongepowered.api.text.selector.SelectorType
import org.spongepowered.api.text.selector.SelectorTypes

import java.util.HashMap

object SelectorTypeRegistryModule : AdditionalPluginCatalogRegistryModule<SelectorType>(SelectorTypes::class) {

    private val byCode = HashMap<String, LanternSelectorType>()

    override fun registerDefaults() {
        register(LanternSelectorType(CatalogKeys.minecraft("all_players"), "a"))
        register(LanternSelectorType(CatalogKeys.minecraft("all_entities"), "e"))
        register(LanternSelectorType(CatalogKeys.minecraft("nearest_player"), "p"))
        register(LanternSelectorType(CatalogKeys.minecraft("random"), "r"))
    }

    override fun doRegistration(selectorType: SelectorType, disallowInbuiltPluginIds: Boolean) {
        selectorType as LanternSelectorType
        check(selectorType.code !in this.byCode) { "The code ${selectorType.code} is already in use."}
        super.doRegistration(selectorType, disallowInbuiltPluginIds)
        this.byCode[selectorType.code] = selectorType
    }

    fun getByCode(code: String): LanternSelectorType? = this.byCode[code]
}
