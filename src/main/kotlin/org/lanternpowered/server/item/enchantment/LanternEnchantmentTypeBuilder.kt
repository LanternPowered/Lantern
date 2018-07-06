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
package org.lanternpowered.server.item.enchantment

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.api.x.item.enchantment.XEnchantmentType
import org.lanternpowered.server.text.translation.TranslationHelper.tr

class LanternEnchantmentTypeBuilder : EnchantmentTypeBuilder {
    companion object {
        var idCounter: Int = 0
    }

    private var name: Translation? = null
    private var id: String? = null
    private var weight: Int = 5
    private var levelRange: IntRange = 1..1
    private var curse: Boolean = false
    private var treasure: Boolean = false
    private var enchantabilityRange: ((Int) -> IntRange)? = null
    private var compatibilityTester: ((EnchantmentType) -> Boolean)? = null

    override fun weight(weight: Int): EnchantmentTypeBuilder = apply { this.weight = weight }
    override fun levelRange(levelRange: IntRange): EnchantmentTypeBuilder = apply { this.levelRange = levelRange }
    override fun curse(curse: Boolean): EnchantmentTypeBuilder = apply { this.curse = curse }
    override fun treasure(treasure: Boolean): EnchantmentTypeBuilder = apply { this.treasure = treasure }
    override fun id(id: String): EnchantmentTypeBuilder = apply { this.id = id }
    override fun enchantabilityRange(provider: (Int) -> IntRange): EnchantmentTypeBuilder = apply { this.enchantabilityRange = provider }
    override fun compatibilityTester(tester: (EnchantmentType) -> Boolean) = apply { this.compatibilityTester = tester }
    override fun name(name: Translation): EnchantmentTypeBuilder = apply { this.name = name }
    override fun name(name: String): EnchantmentTypeBuilder = apply { this.name = tr(name) }
    override fun name(name: Translatable): EnchantmentTypeBuilder = apply { this.name = name.translation }

    override fun build(): XEnchantmentType {
        val id = checkNotNull(this.id) { "The id must be set" }
        val name = checkNotNull(this.name) { "The name must be set" }
        check(!id.isEmpty()) { "The id cannot be empty" }
        return LanternEnchantmentType(CatalogKeys.activePlugin(id), name, idCounter++,
                this.levelRange, this.weight, this.treasure, this.curse, this.enchantabilityRange, this.compatibilityTester)
    }

    override fun from(value: XEnchantmentType): EnchantmentTypeBuilder = throw UnsupportedOperationException()

    override fun reset(): EnchantmentTypeBuilder = apply {
        this.name = null
        this.id = null
        this.weight = 5
        this.levelRange = 1..1
        this.curse = false
        this.treasure = false
        this.enchantabilityRange = null
        this.compatibilityTester = null
    }
}
