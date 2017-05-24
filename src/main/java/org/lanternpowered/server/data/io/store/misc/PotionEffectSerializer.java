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
package org.lanternpowered.server.data.io.store.misc;

import org.lanternpowered.server.effect.potion.LanternPotionEffectType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.effect.PotionEffectTypeRegistryModule;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

import javax.annotation.Nullable;

public final class PotionEffectSerializer {

    private static final DataQuery IDENTIFIER = DataQuery.of("Id");
    private static final DataQuery AMPLIFIER = DataQuery.of("Amplifier");
    private static final DataQuery DURATION = DataQuery.of("Duration");
    private static final DataQuery SHOW_PARTICLES = DataQuery.of("ShowParticles");
    private static final DataQuery AMBIENT = DataQuery.of("Ambient");

    public static DataView serialize(PotionEffect potionEffect) {
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(AMPLIFIER, (byte) potionEffect.getAmplifier());
        dataView.set(DURATION, potionEffect.getDuration());
        dataView.set(AMBIENT, (byte) (potionEffect.isAmbient() ? 1 : 0));
        if (potionEffect.getShowParticles()) {
            dataView.set(SHOW_PARTICLES, (byte) 1);
        }
        final LanternPotionEffectType potionEffectType = (LanternPotionEffectType) potionEffect.getType();
        final int internalId = potionEffectType.getInternalId();
        if (internalId > 0xff) {
            dataView.set(IDENTIFIER, internalId);
        } else {
            dataView.set(IDENTIFIER, (byte) internalId);
        }
        return dataView;
    }

    @Nullable
    public static PotionEffect deserialize(DataView dataView) {
        final int internalId;

        if (dataView.get(IDENTIFIER).get() instanceof Byte) {
            internalId = dataView.getByte(IDENTIFIER).get() & 0xff;
        } else {
            internalId = dataView.getInt(IDENTIFIER).get();
        }

        final PotionEffectType effectType = PotionEffectTypeRegistryModule.get().getByInternalId(internalId).orElse(null);
        if (effectType == null) {
            Lantern.getLogger().warn("Unknown potion effect type: " + internalId);
            return null;
        }

        final int amplifier = dataView.getInt(AMPLIFIER).get();
        final int duration = dataView.getInt(DURATION).get();
        final boolean ambient = dataView.getInt(AMBIENT).orElse(0) > 0;
        final boolean particles = dataView.getInt(SHOW_PARTICLES).orElse(0) > 0;
        return PotionEffect.builder()
                .potionType(effectType)
                .ambience(ambient)
                .amplifier(amplifier)
                .duration(duration)
                .particles(particles)
                .build();
    }

    private PotionEffectSerializer() {
    }
}
