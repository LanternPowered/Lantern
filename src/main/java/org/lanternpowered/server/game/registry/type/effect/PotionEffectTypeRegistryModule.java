/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;

public final class PotionEffectTypeRegistryModule extends AdditionalInternalPluginCatalogRegistryModule<PotionEffectType> {

    public static PotionEffectTypeRegistryModule get() {
        return Holder.INSTANCE;
    }

    private PotionEffectTypeRegistryModule() {
        super(PotionEffectTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternPotionEffectType("minecraft", "speed", 1, "moveSpeed"));
        this.register(new LanternPotionEffectType("minecraft", "slowness", 2, "moveSlowdown"));
        this.register(new LanternPotionEffectType("minecraft", "haste", 3, "digSpeed"));
        this.register(new LanternPotionEffectType("minecraft", "mining_fatigue", 4, "digSlowDown"));
        this.register(new LanternPotionEffectType("minecraft", "strength", 5, "damageBoost"));
        this.register(new LanternPotionEffectType("minecraft", "instant_health", 6, "heal"));
        this.register(new LanternPotionEffectType("minecraft", "instant_damage", 7, "harm"));
        this.register(new LanternPotionEffectType("minecraft", "jump_boost", 8, "jump"));
        this.register(new LanternPotionEffectType("minecraft", "nausea", 9, "confusion"));
        this.register(new LanternPotionEffectType("minecraft", "regeneration", 10, "regeneration"));
        this.register(new LanternPotionEffectType("minecraft", "resistance", 11, "resistance"));
        this.register(new LanternPotionEffectType("minecraft", "fire_resistance", 12, "fireResistance"));
        this.register(new LanternPotionEffectType("minecraft", "water_breathing", 13, "waterBreathing"));
        this.register(new LanternPotionEffectType("minecraft", "invisibility", 14, "invisibility"));
        this.register(new LanternPotionEffectType("minecraft", "blindness", 15, "blindness"));
        this.register(new LanternPotionEffectType("minecraft", "night_vision", 16, "nightVision"));
        this.register(new LanternPotionEffectType("minecraft", "hunger", 17, "hunger"));
        this.register(new LanternPotionEffectType("minecraft", "weakness", 18, "weakness"));
        this.register(new LanternPotionEffectType("minecraft", "poison", 19, "poison"));
        this.register(new LanternPotionEffectType("minecraft", "wither", 20, "wither"));
        this.register(new LanternPotionEffectType("minecraft", "health_boost", 21, "healthBoost"));
        this.register(new LanternPotionEffectType("minecraft", "absorption", 22, "absorption"));
        this.register(new LanternPotionEffectType("minecraft", "saturation", 23, "saturation"));
        this.register(new LanternPotionEffectType("minecraft", "glowing", 24, "glowing"));
        this.register(new LanternPotionEffectType("minecraft", "levitation", 25, "levitation"));
        this.register(new LanternPotionEffectType("minecraft", "luck", 26, "luck"));
        this.register(new LanternPotionEffectType("minecraft", "unluck", 27, "unluck"));
    }

    private static final class Holder {
        private static final PotionEffectTypeRegistryModule INSTANCE = new PotionEffectTypeRegistryModule();
    }
}
