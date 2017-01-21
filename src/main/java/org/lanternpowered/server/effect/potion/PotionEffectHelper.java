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
package org.lanternpowered.server.effect.potion;

import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PotionEffectHelper {

    public static List<PotionEffect> merge(List<PotionEffect> effectsA, List<PotionEffect> effectsB) {
        final Map<PotionEffectType, PotionEffect> effectsByType = new HashMap<>();
        for (PotionEffect effect : effectsA) {
            effectsByType.put(effect.getType(), effect);
        }
        final List<PotionEffect> result = new ArrayList<>();
        for (PotionEffect effect : effectsB) {
            final PotionEffect potionEffect = effectsByType.remove(effect.getType());
            if (potionEffect != null) {
                result.add(merge(effect, potionEffect));
            } else {
                result.add(effect);
            }
        }
        result.addAll(effectsByType.values());
        return result;
    }

    public static PotionEffect merge(PotionEffect effectA, PotionEffect effectB) {
        final PotionEffect.Builder builder = PotionEffect.builder().from(effectA);
        if (effectB.getAmplifier() > effectA.getAmplifier()) {
            builder.amplifier(effectB.getAmplifier()).duration(effectB.getDuration());
        } else if (effectB.getAmplifier() == effectA.getAmplifier() && effectA.getDuration() < effectB.getDuration()) {
            builder.duration(effectB.getDuration());
        } else if (!effectB.isAmbient() && effectA.isAmbient()) {
            builder.ambience(effectB.isAmbient());
        }
        builder.particles(effectB.getShowParticles());
        return builder.build();
    }

    private PotionEffectHelper() {
    }
}
