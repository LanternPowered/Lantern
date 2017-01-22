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
package org.lanternpowered.server.game.registry.type.data;

import static org.lanternpowered.server.item.PropertyProviders.applicableEffects;
import static org.lanternpowered.server.item.PropertyProviders.foodRestoration;
import static org.lanternpowered.server.item.PropertyProviders.saturation;

import org.lanternpowered.server.data.type.LanternFish;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.effect.PotionEffectTypeRegistryModule;
import org.spongepowered.api.data.type.Fish;
import org.spongepowered.api.data.type.Fishes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency(PotionEffectTypeRegistryModule.class)
public class FishRegistryModule extends InternalPluginCatalogRegistryModule<Fish> {

    private static final FishRegistryModule INSTANCE = new FishRegistryModule();

    public static FishRegistryModule get() {
        return INSTANCE;
    }

    private FishRegistryModule() {
        super(Fishes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternFish("minecraft", "cod", "item.fish.cod.raw.name", 0,
                builder -> builder
                        .add(foodRestoration(2))
                        .add(saturation(0.1))));
        register(new LanternFish("minecraft", "salmon", "item.fish.salmon.raw.name", 1,
                builder -> builder
                        .add(foodRestoration(2))
                        .add(saturation(0.1))));
        register(new LanternFish("minecraft", "clownfish", "item.fish.clownfish.raw.name", 2,
                builder -> builder
                        .add(foodRestoration(1))
                        .add(saturation(0.1))));
        register(new LanternFish("minecraft", "pufferfish", "item.fish.pufferfish.raw.name", 3,
                builder -> builder
                        .add(foodRestoration(1))
                        .add(saturation(0.1))
                        .add(applicableEffects(
                                PotionEffect.of(PotionEffectTypes.POISON, 3, 1200),
                                PotionEffect.of(PotionEffectTypes.HUNGER, 2, 300),
                                PotionEffect.of(PotionEffectTypes.NAUSEA, 1, 300)))));
    }
}
