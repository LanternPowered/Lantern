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
@file:Suppress("FunctionName")

package org.lanternpowered.server.game.registry.type.item

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule
import org.lanternpowered.server.item.enchantment.LanternEnchantmentTypeBuilder
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.item.enchantment.EnchantmentType
import org.spongepowered.api.item.enchantment.EnchantmentTypes

object EnchantmentTypeRegistryModule : InternalPluginCatalogRegistryModule<EnchantmentType>(EnchantmentTypes::class) {

    private inline fun register(id: String, name: String, fn: EnchantmentTypeBuilder.() -> Unit = {}) {
        register(LanternEnchantmentTypeBuilder().id(id).name(tr(name)).apply(fn).build())
    }

    override fun registerDefaults() {


        // Register for minecraft plugin
        CauseStack.current().withPlugin(Lantern.getMinecraftPlugin()) {
            register("protection", "enchantment.protect.all")
            register("fire_protection", "enchantment.protect.fire")
            register("feather_falling", "enchantment.protect.fall")
            register("blast_protection", "enchantment.protect.explosion")
            register("projectile_protection", "enchantment.protect.projectile")
            register("respiration", "enchantment.oxygen")
            register("aqua_affinity", "enchantment.waterWorker")
            register("thorns", "enchantment.thorns")
            register("depth_strider", "enchantment.waterWalker")
            register("frost_walker", "enchantment.frostWalker")
            register("sharpness", "enchantment.damage.all")
            register("smite", "enchantment.damage.undead")
            register("bane_of_arthropods", "enchantment.damage.arthropods")
            register("knockback", "enchantment.knockback") {
                maxLevel(2)
                enchantabilityRange {
                    val min = 5 + (it - 1) * 20
                    val max = min + 50
                    min..max
                }
            }
            register("fire_aspect", "enchantment.fire") {
                maxLevel(2)
                enchantabilityRange {
                    val min = 10 + (it - 1) * 20
                    val max = min + 50
                    min..max
                }
            }
            val lootBonusModifier: EnchantmentTypeBuilder.() -> Unit = {
                enchantabilityRange {
                    val min = 15 + (it - 1) * 9
                    val max = min + 50
                    min..max
                }
                maxLevel(3)
                compatibilityTester { it != EnchantmentTypes.SILK_TOUCH }
            }
            register("looting", "enchantment.lootBonus") {
                apply(lootBonusModifier)
            }
            register("sweeping", "enchantment.sweeping") {
                maxLevel(3)
                enchantabilityRange {
                    val min = 5 + (it - 1) * 9
                    val max = min + 15
                    min..max
                }
            }
            register("efficiency", "enchantment.digging")
            register("silk_touch", "enchantment.untouching")
            register("unbreaking", "enchantment.durability") {
                maxLevel(3)
                enchantabilityRange {
                    val min = 5 + (it - 1) * 8
                    val max = min + 50
                    min..max
                }
            }
            register("fortune", "enchantment.lootBonusDigger") {
                apply(lootBonusModifier)
            }
            register("power", "enchantment.arrowDamage")
            register("punch", "enchantment.arrowKnockback")
            register("flame", "enchantment.arrowFire")
            register("infinity", "enchantment.arrowInfinite")
            register("luck_of_the_sea", "enchantment.lootBonusFishing") {
                apply(lootBonusModifier)
            }
            register("lure", "enchantment.fishingSpeed")
            register("mending", "enchantment.mending")
            register("vanishing_curse", "enchantment.vanishing_curse")
        }
    }
}
