/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.inventory.client;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.enchantment.EnchantmentType;

import java.util.Optional;

public final class ContainerProperties {

    /**
     * A double container property that will affect the fuel
     * progress bar of a {@link FurnaceClientContainer} or
     * {@link BrewingStandClientContainer}. The value scales
     * between 0 and 1, where 1 means that the fuel
     * is full and 0 empty.
     */
    public static final ContainerProperty<Double> FUEL_PROGRESS = new ContainerProperty<>(TypeToken.of(Double.class));

    /**
     * A double container property that will affect the smelting
     * progress bar of a {@link FurnaceClientContainer}. The
     * value scales between 0 and 1, where 1 means that the item
     * finished smelting and 0 that it hasn't started yet.
     */
    public static final ContainerProperty<Double> SMELT_PROGRESS = new ContainerProperty<>(TypeToken.of(Double.class));

    /**
     * A double container property that will affect the brewing
     * progress bar of a {@link BrewingStandClientContainer}. The
     * value scales between 0 and 1, where 1 means that the item
     * finished brewing and 0 that it hasn't started yet.
     */
    public static final ContainerProperty<Double> BREW_PROGRESS = new ContainerProperty<>(TypeToken.of(Double.class));

    public static final ContainerProperty<Optional<PotionEffectType>> PRIMARY_POTION_EFFECT =
            new ContainerProperty<>(new TypeToken<Optional<PotionEffectType>>() {});

    public static final ContainerProperty<Optional<PotionEffectType>> SECONDARY_POTION_EFFECT =
            new ContainerProperty<>(new TypeToken<Optional<PotionEffectType>>() {});

    public static final ContainerProperty<Integer> REPAIR_COST = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> REQUIRED_EXPERIENCE_LEVEL_1 = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> REQUIRED_EXPERIENCE_LEVEL_2 = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> REQUIRED_EXPERIENCE_LEVEL_3 = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> ENCHANTMENT_SEED = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Optional<EnchantmentType>> SHOWN_ENCHANTMENT_1 =
            new ContainerProperty<>(new TypeToken<Optional<EnchantmentType>>() {});

    public static final ContainerProperty<Optional<EnchantmentType>> SHOWN_ENCHANTMENT_2 =
            new ContainerProperty<>(new TypeToken<Optional<EnchantmentType>>() {});

    public static final ContainerProperty<Optional<EnchantmentType>> SHOWN_ENCHANTMENT_3 =
            new ContainerProperty<>(new TypeToken<Optional<EnchantmentType>>() {});

    public static final ContainerProperty<Integer> SHOWN_ENCHANTMENT_LEVEL_1 = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> SHOWN_ENCHANTMENT_LEVEL_2 = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> SHOWN_ENCHANTMENT_LEVEL_3 = new ContainerProperty<>(TypeToken.of(Integer.class));

    private ContainerProperties() {
    }
}
