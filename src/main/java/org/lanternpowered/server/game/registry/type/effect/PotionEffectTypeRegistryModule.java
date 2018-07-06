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
import org.spongepowered.api.CatalogKey;
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
        register(new LanternPotionEffectType(CatalogKey.minecraft("speed"), 1, "moveSpeed"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("slowness"), 2, "moveSlowdown"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("haste"), 3, "digSpeed"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("mining_fatigue"), 4, "digSlowDown"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("strength"), 5, "damageBoost"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("instant_health"), 6, "heal").instant());
        register(new LanternPotionEffectType(CatalogKey.minecraft("instant_damage"), 7, "harm").instant());
        register(new LanternPotionEffectType(CatalogKey.minecraft("jump_boost"), 8, "jump"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("nausea"), 9, "confusion"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("regeneration"), 10, "regeneration"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("resistance"), 11, "resistance"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("fire_resistance"), 12, "fireResistance"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("water_breathing"), 13, "waterBreathing"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("invisibility"), 14, "invisibility"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("blindness"), 15, "blindness"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("night_vision"), 16, "nightVision"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("hunger"), 17, "hunger"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("weakness"), 18, "weakness"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("poison"), 19, "poison"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("wither"), 20, "wither"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("health_boost"), 21, "healthBoost"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("absorption"), 22, "absorption"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("saturation"), 23, "saturation"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("glowing"), 24, "glowing"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("levitation"), 25, "levitation"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("luck"), 26, "luck"));
        register(new LanternPotionEffectType(CatalogKey.minecraft("unluck"), 27, "unluck"));
    }

    private static final class Holder {
        private static final PotionEffectTypeRegistryModule INSTANCE = new PotionEffectTypeRegistryModule();
    }
}
