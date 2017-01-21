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
package org.lanternpowered.server.item.behavior.vanilla;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.potion.PotionEffectHelper;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.item.behavior.types.FinishUsingItemBehavior;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class EatingInteractionBehavior implements InteractWithItemBehavior, FinishUsingItemBehavior {

    public interface Consumer {

        void apply(Player player, BehaviorPipeline<Behavior> pipeline, BehaviorContext context);
    }

    private boolean alwaysConsumable;

    private double saturation;
    private double health;
    private int food;

    @Nullable private Consumer consumer;
    @Nullable private Supplier<ItemStack> restItemSupplier;

    private final List<PotionEffect> potionEffects = new ArrayList<>();

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Player> optPlayer = context.get(Parameters.PLAYER);
        if (optPlayer.isPresent()) {
            final Player player = optPlayer.get();
            if (!this.alwaysConsumable) {
                final int maxFood = player.get(LanternKeys.MAX_FOOD_LEVEL).orElse(1);
                final int food = player.get(Keys.FOOD_LEVEL).orElse(maxFood);
                if (food >= maxFood) {
                    return BehaviorResult.PASS;
                }
            }
            optPlayer.get().offer(LanternKeys.ACTIVE_HAND, Optional.of(context.tryGet(Parameters.INTERACTION_HAND)));
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.PASS;
    }

    @Override
    public BehaviorResult tryUse(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Player> optPlayer = context.get(Parameters.PLAYER);
        if (optPlayer.isPresent()) {
            final Player player = optPlayer.get();

            if (this.food != 0.0) {
                final Optional<Integer> maxFood = player.get(LanternKeys.MAX_FOOD_LEVEL);
                final Optional<Integer> optFoodLevel = player.get(Keys.FOOD_LEVEL);
                if (optFoodLevel.isPresent()) {
                    player.offer(Keys.FOOD_LEVEL, Math.min(optFoodLevel.get() + this.food, maxFood.orElse(Integer.MAX_VALUE)));
                }
            }
            if (this.health != 0.0) {
                final Optional<Double> maxHealth = player.get(Keys.MAX_HEALTH);
                final Optional<Double> optHealth = player.get(Keys.HEALTH);
                if (optHealth.isPresent()) {
                    player.offer(Keys.HEALTH, Math.min(optHealth.get() + this.health, maxHealth.orElse(Double.MAX_VALUE)));
                }
            }
            if (this.saturation != 0.0) {
                final Optional<Double> maxSaturation = player.get(LanternKeys.MAX_SATURATION);
                final Optional<Double> optSaturation = player.get(Keys.SATURATION);
                if (optSaturation.isPresent()) {
                    player.offer(Keys.SATURATION, Math.min(optSaturation.get() + this.saturation, maxSaturation.orElse(Double.MAX_VALUE)));
                }
            }
            if (!this.potionEffects.isEmpty()) {
                final List<PotionEffect> potionEffects = player.get(Keys.POTION_EFFECTS).orElse(Collections.emptyList());
                player.offer(Keys.POTION_EFFECTS, PotionEffectHelper.merge(potionEffects, this.potionEffects));
            }
            if (this.consumer != null) {
                this.consumer.apply(player, pipeline, context);
            }

            final Slot slot = context.tryGet(Parameters.USED_SLOT);
            slot.poll(1);
            if (this.restItemSupplier != null) {
                if (slot.peek().isPresent()) {
                    ((LanternPlayer) player).getInventory().getMain().offer(this.restItemSupplier.get());
                } else {
                    slot.set(this.restItemSupplier.get());
                }
            }

            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.PASS;
    }

    public EatingInteractionBehavior restItem(Supplier<ItemStack> restItemSupplier) {
        this.restItemSupplier = restItemSupplier;
        return this;
    }

    public EatingInteractionBehavior alwaysConsumable(boolean alwaysConsumable) {
        this.alwaysConsumable = alwaysConsumable;
        return this;
    }

    public EatingInteractionBehavior potionEffect(PotionEffect potionEffect) {
        this.potionEffects.add(checkNotNull(potionEffect, "potionEffect"));
        return this;
    }

    public EatingInteractionBehavior consumer(@Nullable Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public EatingInteractionBehavior saturation(double saturation) {
        this.saturation = saturation;
        return this;
    }

    public EatingInteractionBehavior health(double health) {
        this.health = health;
        return this;
    }

    public EatingInteractionBehavior food(int food) {
        this.food = food;
        return this;
    }
}
