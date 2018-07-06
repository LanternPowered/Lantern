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

import org.spongepowered.api.data.DataView
import org.spongepowered.api.data.Queries
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentType
import java.util.Optional

class LanternEnchantmentBuilder : AbstractDataBuilder<Enchantment>(Enchantment::class.java, 1), Enchantment.Builder {

    private var enchantmentType: EnchantmentType? = null
    private var level: Int? = null

    override fun from(enchantment: Enchantment): Enchantment.Builder = apply {
        this.enchantmentType = enchantment.type
        this.level = enchantment.level
    }

    override fun reset(): Enchantment.Builder = apply {
        this.enchantmentType = null
        this.level = null
    }

    override fun type(enchantmentType: EnchantmentType): Enchantment.Builder = apply {
        this.enchantmentType = enchantmentType
    }

    override fun level(level: Int): Enchantment.Builder = apply {
        check(level in Short.MIN_VALUE..Short.MAX_VALUE) {
            "The level must be between ${Short.MIN_VALUE} and ${Short.MAX_VALUE}, and not $level" }
        this.level = level
    }

    override fun build(): Enchantment {
        val enchantmentType = checkNotNull(this.enchantmentType) { "The enchantment type must be set" }
        val level = checkNotNull(this.level) { "The level must be set" }
        return LanternEnchantment(enchantmentType, level)
    }

    override fun buildContent(container: DataView): Optional<Enchantment> {
        if (!container.contains(Queries.ENCHANTMENT_ID, Queries.LEVEL)) {
            return Optional.empty()
        }
        val enchantmentType = container.getCatalogType(Queries.ENCHANTMENT_ID, EnchantmentType::class.java)
        val level = container.getInt(Queries.LEVEL).get()
        return enchantmentType.map { type -> Enchantment.builder().type(type).level(level).build() }
    }

}
