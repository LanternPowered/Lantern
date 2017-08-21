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

import org.lanternpowered.server.effect.potion.LanternPotionType;
import org.lanternpowered.server.effect.potion.PotionType;
import org.lanternpowered.server.effect.potion.PotionTypes;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeCatalogRegistryModule;
import org.lanternpowered.server.game.registry.forge.ForgeRegistryData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency({ PotionEffectTypeRegistryModule.class })
public class PotionTypeRegistryModule extends InternalPluginCatalogRegistryModule<PotionType> implements ForgeCatalogRegistryModule<PotionType> {

    private static final PotionTypeRegistryModule INSTANCE = new PotionTypeRegistryModule();

    public static PotionTypeRegistryModule get() {
        return INSTANCE;
    }

    private PotionTypeRegistryModule() {
        super(PotionTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternPotionType("minecraft", "empty", "%s.effect.empty", 0));
        register(new LanternPotionType("minecraft", "water", "%s.effect.water", 1));
        register(new LanternPotionType("minecraft", "mundane", "%s.effect.mundane", 2));
        register(new LanternPotionType("minecraft", "thick", "%s.effect.thick", 3));
        register(new LanternPotionType("minecraft", "awkward", "%s.effect.awkward", 4));
        register(new LanternPotionType("minecraft", "night_vision", "%s.effect.night_vision", 5)
                .add(PotionEffect.of(PotionEffectTypes.NIGHT_VISION, 0, 3600)));
        register(new LanternPotionType("minecraft", "long_night_vision", "%s.effect.night_vision", 6)
                .add(PotionEffect.of(PotionEffectTypes.NIGHT_VISION, 0, 9600)));
        register(new LanternPotionType("minecraft", "invisibility", "%s.effect.invisibility", 7)
                .add(PotionEffect.of(PotionEffectTypes.INVISIBILITY, 0, 3600)));
        register(new LanternPotionType("minecraft", "long_invisibility", "%s.effect.invisibility", 8)
                .add(PotionEffect.of(PotionEffectTypes.INVISIBILITY, 0, 9600)));
        register(new LanternPotionType("minecraft", "leaping", "%s.effect.leaping", 9)
                .add(PotionEffect.of(PotionEffectTypes.JUMP_BOOST, 0, 3600)));
        register(new LanternPotionType("minecraft", "long_leaping", "%s.effect.leaping", 10)
                .add(PotionEffect.of(PotionEffectTypes.JUMP_BOOST, 0, 9600)));
        register(new LanternPotionType("minecraft", "strong_leaping", "%s.effect.leaping", 11)
                .add(PotionEffect.of(PotionEffectTypes.JUMP_BOOST, 1, 1800)));
        register(new LanternPotionType("minecraft", "fire_resistance", "%s.effect.fire_resistance", 12)
                .add(PotionEffect.of(PotionEffectTypes.JUMP_BOOST, 0, 3600)));
        register(new LanternPotionType("minecraft", "long_fire_resistance", "%s.effect.fire_resistance", 13)
                .add(PotionEffect.of(PotionEffectTypes.JUMP_BOOST, 0, 9600)));
        register(new LanternPotionType("minecraft", "swiftness", "%s.effect.swiftness", 14)
                .add(PotionEffect.of(PotionEffectTypes.SPEED, 0, 3600)));
        register(new LanternPotionType("minecraft", "long_swiftness", "%s.effect.swiftness", 15)
                .add(PotionEffect.of(PotionEffectTypes.SPEED, 0, 9600)));
        register(new LanternPotionType("minecraft", "strong_swiftness", "%s.effect.swiftness", 16)
                .add(PotionEffect.of(PotionEffectTypes.SPEED, 1, 1800)));
        register(new LanternPotionType("minecraft", "slowness", "%s.effect.slowness", 17)
                .add(PotionEffect.of(PotionEffectTypes.SLOWNESS, 0, 1800)));
        register(new LanternPotionType("minecraft", "long_slowness", "%s.effect.slowness", 18)
                .add(PotionEffect.of(PotionEffectTypes.SLOWNESS, 0, 4800)));
        register(new LanternPotionType("minecraft", "water_breathing", "%s.effect.water_breathing", 19)
                .add(PotionEffect.of(PotionEffectTypes.WATER_BREATHING, 0, 3600)));
        register(new LanternPotionType("minecraft", "long_water_breathing", "%s.effect.water_breathing", 20)
                .add(PotionEffect.of(PotionEffectTypes.WATER_BREATHING, 0, 9600)));
        register(new LanternPotionType("minecraft", "healing", "%s.effect.healing", 21)
                .add(PotionEffect.of(PotionEffectTypes.INSTANT_HEALTH, 0, 1)));
        register(new LanternPotionType("minecraft", "strong_healing", "%s.effect.healing", 22)
                .add(PotionEffect.of(PotionEffectTypes.INSTANT_HEALTH, 1, 1)));
        register(new LanternPotionType("minecraft", "harming", "%s.effect.harming", 23)
                .add(PotionEffect.of(PotionEffectTypes.INSTANT_DAMAGE, 0, 1)));
        register(new LanternPotionType("minecraft", "strong_harming", "%s.effect.harming", 24)
                .add(PotionEffect.of(PotionEffectTypes.INSTANT_DAMAGE, 1, 1)));
        register(new LanternPotionType("minecraft", "poison", "%s.effect.poison", 25)
                .add(PotionEffect.of(PotionEffectTypes.POISON, 0, 900)));
        register(new LanternPotionType("minecraft", "long_poison", "%s.effect.poison", 26)
                .add(PotionEffect.of(PotionEffectTypes.POISON, 0, 1800)));
        register(new LanternPotionType("minecraft", "strong_poison", "%s.effect.poison", 27)
                .add(PotionEffect.of(PotionEffectTypes.POISON, 1, 432)));
        register(new LanternPotionType("minecraft", "regeneration", "%s.effect.regeneration", 28)
                .add(PotionEffect.of(PotionEffectTypes.REGENERATION, 0, 900)));
        register(new LanternPotionType("minecraft", "long_regeneration", "%s.effect.regeneration", 29)
                .add(PotionEffect.of(PotionEffectTypes.REGENERATION, 0, 1800)));
        register(new LanternPotionType("minecraft", "strong_regeneration", "%s.effect.regeneration", 30)
                .add(PotionEffect.of(PotionEffectTypes.REGENERATION, 1, 450)));
        register(new LanternPotionType("minecraft", "strength", "%s.effect.strength", 31)
                .add(PotionEffect.of(PotionEffectTypes.STRENGTH, 0, 3600)));
        register(new LanternPotionType("minecraft", "long_strength", "%s.effect.strength", 32)
                .add(PotionEffect.of(PotionEffectTypes.STRENGTH, 0, 9600)));
        register(new LanternPotionType("minecraft", "strong_strength", "%s.effect.strength", 33)
                .add(PotionEffect.of(PotionEffectTypes.STRENGTH, 1, 1800)));
        register(new LanternPotionType("minecraft", "weakness", "%s.effect.weakness", 34)
                .add(PotionEffect.of(PotionEffectTypes.WEAKNESS, 0, 1800)));
        register(new LanternPotionType("minecraft", "long_weakness", "%s.effect.weakness", 35)
                .add(PotionEffect.of(PotionEffectTypes.WEAKNESS, 0, 4800)));
        register(new LanternPotionType("minecraft", "luck", "%s.effect.luck", 36)
                .add(PotionEffect.of(PotionEffectTypes.LUCK, 0, 6000)));
    }

    @Override
    public ForgeRegistryData getRegistryData() {
        return new ForgeRegistryData("minecraft:potiontypes", getRegistryDataMappings());
    }
}
