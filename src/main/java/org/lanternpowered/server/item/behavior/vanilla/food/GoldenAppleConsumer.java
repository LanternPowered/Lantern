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
package org.lanternpowered.server.item.behavior.vanilla.food;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.effect.potion.PotionEffectHelper;
import org.lanternpowered.server.item.behavior.vanilla.EatingInteractionBehavior;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.GoldenApple;
import org.spongepowered.api.data.type.GoldenApples;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collections;
import java.util.List;

public class GoldenAppleConsumer implements EatingInteractionBehavior.Consumer {

    private final List<PotionEffect> enchanted = ImmutableList.<PotionEffect>builder()
            .add(PotionEffect.of(PotionEffectTypes.REGENERATION, 1, 400))
            .add(PotionEffect.of(PotionEffectTypes.RESISTANCE, 0, 6000))
            .add(PotionEffect.of(PotionEffectTypes.FIRE_RESISTANCE, 0, 6000))
            .add(PotionEffect.of(PotionEffectTypes.ABSORPTION, 3, 2400))
            .build();

    private final List<PotionEffect> normal = ImmutableList.<PotionEffect>builder()
            .add(PotionEffect.of(PotionEffectTypes.REGENERATION, 1, 100))
            .add(PotionEffect.of(PotionEffectTypes.ABSORPTION, 0, 2400))
            .build();

    @Override
    public void apply(Player player, BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final GoldenApple goldenApple = context.get(Parameters.USED_ITEM_STACK)
                .flatMap(stack -> stack.get(Keys.GOLDEN_APPLE_TYPE)).orElse(GoldenApples.GOLDEN_APPLE);
        final List<PotionEffect> potionEffects = player.get(Keys.POTION_EFFECTS).orElse(Collections.emptyList());
        player.offer(Keys.POTION_EFFECTS, PotionEffectHelper.merge(potionEffects,
                goldenApple == GoldenApples.ENCHANTED_GOLDEN_APPLE ? this.enchanted : this.normal));
    }
}
