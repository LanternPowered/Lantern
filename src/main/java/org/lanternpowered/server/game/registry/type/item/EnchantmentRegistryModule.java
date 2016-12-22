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
package org.lanternpowered.server.game.registry.type.item;

import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.lanternpowered.server.item.enchantment.LanternEnchantment;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.Enchantments;

public class EnchantmentRegistryModule extends InternalPluginCatalogRegistryModule<Enchantment> {

    private static final EnchantmentRegistryModule INSTANCE = new EnchantmentRegistryModule();

    public static EnchantmentRegistryModule get() {
        return INSTANCE;
    }

    private EnchantmentRegistryModule() {
        super(Enchantments.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternEnchantment("minecraft", "protection", "enchantment.protect.all", 0));
        register(new LanternEnchantment("minecraft", "fire_protection", "enchantment.protect.fire", 1));
        register(new LanternEnchantment("minecraft", "feather_falling", "enchantment.protect.fall", 2));
        register(new LanternEnchantment("minecraft", "blast_protection", "enchantment.protect.explosion", 3));
        register(new LanternEnchantment("minecraft", "projectile_protection", "enchantment.protect.projectile", 4));
        register(new LanternEnchantment("minecraft", "respiration", "enchantment.oxygen", 5));
        register(new LanternEnchantment("minecraft", "aqua_affinity", "enchantment.waterWorker", 6));
        register(new LanternEnchantment("minecraft", "thorns", "enchantment.thorns", 7));
        register(new LanternEnchantment("minecraft", "depth_strider", "enchantment.waterWalker", 8));
        register(new LanternEnchantment("minecraft", "frost_walker", "enchantment.frostWalker", 9));
        register(new LanternEnchantment("minecraft", "sharpness", "enchantment.damage.all", 16));
        register(new LanternEnchantment("minecraft", "smite", "enchantment.damage.undead", 17));
        register(new LanternEnchantment("minecraft", "bane_of_arthropods", "enchantment.damage.arthropods", 18));
        register(new LanternEnchantment("minecraft", "knockback", "enchantment.knockback", 19));
        register(new LanternEnchantment("minecraft", "fire_aspect", "enchantment.fire", 20));
        register(new LanternEnchantment("minecraft", "looting", "enchantment.lootBonus", 21));
        register(new LanternEnchantment("minecraft", "sweeping", "enchantment.sweeping", 22));
        register(new LanternEnchantment("minecraft", "efficiency", "enchantment.digging", 32));
        register(new LanternEnchantment("minecraft", "silk_touch", "enchantment.untouching", 33));
        register(new LanternEnchantment("minecraft", "unbreaking", "enchantment.durability", 34));
        register(new LanternEnchantment("minecraft", "fortune", "enchantment.lootBonusDigger", 35));
        register(new LanternEnchantment("minecraft", "power", "enchantment.arrowDamage", 48));
        register(new LanternEnchantment("minecraft", "punch", "enchantment.arrowKnockback", 49));
        register(new LanternEnchantment("minecraft", "flame", "enchantment.arrowFire", 50));
        register(new LanternEnchantment("minecraft", "infinity", "enchantment.arrowInfinite", 51));
        register(new LanternEnchantment("minecraft", "luck_of_the_sea", "enchantment.lootBonusFishing", 61));
        register(new LanternEnchantment("minecraft", "lure", "enchantment.fishingSpeed", 62));
        register(new LanternEnchantment("minecraft", "mending", "enchantment.mending", 70));
        register(new LanternEnchantment("minecraft", "vanishing_curse", "enchantment.vanishing_curse", 71));
    }
}
