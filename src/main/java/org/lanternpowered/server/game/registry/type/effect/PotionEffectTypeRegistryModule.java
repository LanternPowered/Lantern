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
package org.lanternpowered.server.game.registry.type.effect;

import org.lanternpowered.server.effect.potion.LanternPotionEffectType;
import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeRegistryData;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;

public final class PotionEffectTypeRegistryModule extends AdditionalInternalPluginCatalogRegistryModule<PotionEffectType>
        implements ForgeCatalogRegistryModule<PotionEffectType> {

    public static PotionEffectTypeRegistryModule get() {
        return Holder.INSTANCE;
    }

    private PotionEffectTypeRegistryModule() {
        super(PotionEffectTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternPotionEffectType("minecraft", "speed", 1, "moveSpeed"));
        register(new LanternPotionEffectType("minecraft", "slowness", 2, "moveSlowdown"));
        register(new LanternPotionEffectType("minecraft", "haste", 3, "digSpeed"));
        register(new LanternPotionEffectType("minecraft", "mining_fatigue", 4, "digSlowDown"));
        register(new LanternPotionEffectType("minecraft", "strength", 5, "damageBoost"));
        register(new LanternPotionEffectType("minecraft", "instant_health", 6, "heal").instant());
        register(new LanternPotionEffectType("minecraft", "instant_damage", 7, "harm").instant());
        register(new LanternPotionEffectType("minecraft", "jump_boost", 8, "jump"));
        register(new LanternPotionEffectType("minecraft", "nausea", 9, "confusion"));
        register(new LanternPotionEffectType("minecraft", "regeneration", 10, "regeneration"));
        register(new LanternPotionEffectType("minecraft", "resistance", 11, "resistance"));
        register(new LanternPotionEffectType("minecraft", "fire_resistance", 12, "fireResistance"));
        register(new LanternPotionEffectType("minecraft", "water_breathing", 13, "waterBreathing"));
        register(new LanternPotionEffectType("minecraft", "invisibility", 14, "invisibility"));
        register(new LanternPotionEffectType("minecraft", "blindness", 15, "blindness"));
        register(new LanternPotionEffectType("minecraft", "night_vision", 16, "nightVision"));
        register(new LanternPotionEffectType("minecraft", "hunger", 17, "hunger"));
        register(new LanternPotionEffectType("minecraft", "weakness", 18, "weakness"));
        register(new LanternPotionEffectType("minecraft", "poison", 19, "poison"));
        register(new LanternPotionEffectType("minecraft", "wither", 20, "wither"));
        register(new LanternPotionEffectType("minecraft", "health_boost", 21, "healthBoost"));
        register(new LanternPotionEffectType("minecraft", "absorption", 22, "absorption"));
        register(new LanternPotionEffectType("minecraft", "saturation", 23, "saturation"));
        register(new LanternPotionEffectType("minecraft", "glowing", 24, "glowing"));
        register(new LanternPotionEffectType("minecraft", "levitation", 25, "levitation"));
        register(new LanternPotionEffectType("minecraft", "luck", 26, "luck"));
        register(new LanternPotionEffectType("minecraft", "unluck", 27, "unluck"));
    }

    @Override
    public ForgeRegistryData getRegistryData() {
        return new ForgeRegistryData("minecraft:potions", getRegistryDataMappings());
    }

    private static final class Holder {
        private static final PotionEffectTypeRegistryModule INSTANCE = new PotionEffectTypeRegistryModule();
    }
}
