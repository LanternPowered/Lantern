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

import org.lanternpowered.api.ext.PotionEffectHelper;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.ISlot;
import org.lanternpowered.server.item.ItemKeys;
import org.lanternpowered.server.item.behavior.types.FinishUsingItemBehavior;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.property.Properties;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("ConstantConditions")
public class ConsumableInteractionBehavior implements InteractWithItemBehavior, FinishUsingItemBehavior {

    public interface Consumer {

        void apply(Player player, BehaviorPipeline<Behavior> pipeline, BehaviorContext context);
    }

    @Nullable private Consumer consumer;
    @Nullable private Supplier<ItemStack> restItemSupplier;

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Player> optPlayer = context.getContext(ContextKeys.PLAYER);
        if (optPlayer.isPresent()) {
            final Player player = optPlayer.get();
            final ItemStack itemStack = context.requireContext(ContextKeys.USED_ITEM_STACK);
            if (itemStack.getProperty(ItemKeys.IS_ALWAYS_CONSUMABLE).orElse(false)) {
                int status = 0;
                final double replenishedFood = itemStack.getDoubleProperty(Properties.REPLENISHED_FOOD).orElse(0);
                if (replenishedFood != 0) {
                    final int maxFood = player.get(LanternKeys.MAX_FOOD_LEVEL).orElse(1);
                    final int food = player.get(Keys.FOOD_LEVEL).orElse(maxFood);
                    status = food < maxFood ? 2 : 1;
                }
                if (status != 2) {
                    final double restoredHealth = itemStack.getDoubleProperty(ItemKeys.HEALTH_RESTORATION).orElse(0);
                    if (restoredHealth != 0) {
                        final double maxHealth = player.get(Keys.MAX_HEALTH).orElse(1.0);
                        final double health = player.get(Keys.HEALTH).orElse(maxHealth);
                        status = health < maxHealth ? 2 : 1;
                    }
                }
                if (status == 1) {
                    return BehaviorResult.PASS;
                }
            }
            optPlayer.get().offer(LanternKeys.ACTIVE_HAND, Optional.of(context.requireContext(ContextKeys.INTERACTION_HAND)));
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.PASS;
    }

    @Override
    public BehaviorResult tryUse(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Player> optPlayer = context.getContext(ContextKeys.PLAYER);
        if (optPlayer.isPresent()) {
            final Player player = optPlayer.get();
            final ItemStack itemStack = context.requireContext(ContextKeys.USED_ITEM_STACK);

            final double replenishedFood = itemStack.getDoubleProperty(Properties.REPLENISHED_FOOD).orElse(0);
            if (replenishedFood != 0) {
                final Optional<Integer> maxFood = player.get(LanternKeys.MAX_FOOD_LEVEL);
                final Optional<Integer> optFoodLevel = player.get(Keys.FOOD_LEVEL);
                optFoodLevel.ifPresent(food -> player.offer(Keys.FOOD_LEVEL,
                        Math.min(food + (int) replenishedFood, maxFood.orElse(Integer.MAX_VALUE))));
            }
            final double restoredHealth = itemStack.getDoubleProperty(ItemKeys.HEALTH_RESTORATION).orElse(0);
            if (restoredHealth != 0) {
                final Optional<Double> maxHealth = player.get(Keys.MAX_HEALTH);
                final Optional<Double> optHealth = player.get(Keys.HEALTH);
                optHealth.ifPresent(health -> player.offer(Keys.HEALTH,
                        Math.min(health + restoredHealth, maxHealth.orElse(Double.MAX_VALUE))));
            }
            final double replenishedSaturation = itemStack.getDoubleProperty(Properties.REPLENISHED_SATURATION).orElse(0);
            if (replenishedSaturation != 0) {
                final Optional<Double> optSaturation = player.get(Keys.SATURATION);
                optSaturation.ifPresent(aDouble -> player.offer(Keys.SATURATION,
                        Math.min(aDouble + replenishedSaturation, player.get(Keys.FOOD_LEVEL).orElse(20))));
            }
            itemStack.getProperty(Properties.APPLICABLE_POTION_EFFECTS)
                    .filter(applicableEffects -> !applicableEffects.isEmpty())
                    .ifPresent(applicableEffects -> {
                        final List<PotionEffect> potionEffects = player.get(Keys.POTION_EFFECTS).orElse(Collections.emptyList());
                        player.offer(Keys.POTION_EFFECTS, PotionEffectHelper.merge(potionEffects, applicableEffects));
                    });
            if (this.consumer != null) {
                this.consumer.apply(player, pipeline, context);
            }

            if (!player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
                final ISlot slot = (ISlot) context.requireContext(ContextKeys.USED_SLOT);
                slot.poll(1);
                if (this.restItemSupplier != null) {
                    if (slot.peek().isNotEmpty()) {
                        ((LanternPlayer) player).getInventory().getPrimary().offer(this.restItemSupplier.get());
                    } else {
                        slot.set(this.restItemSupplier.get());
                    }
                }
            }

            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.PASS;
    }

    public ConsumableInteractionBehavior restItem(Supplier<ItemStack> restItemSupplier) {
        this.restItemSupplier = restItemSupplier;
        return this;
    }

    public ConsumableInteractionBehavior consumer(@Nullable Consumer consumer) {
        this.consumer = consumer;
        return this;
    }
}
