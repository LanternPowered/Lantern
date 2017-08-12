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
package org.lanternpowered.server.inventory.client;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.Enchantment;

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

    public static final ContainerProperty<Optional<Enchantment>> SHOWN_ENCHANTMENT_1 =
            new ContainerProperty<>(new TypeToken<Optional<Enchantment>>() {});

    public static final ContainerProperty<Optional<Enchantment>> SHOWN_ENCHANTMENT_2 =
            new ContainerProperty<>(new TypeToken<Optional<Enchantment>>() {});

    public static final ContainerProperty<Optional<Enchantment>> SHOWN_ENCHANTMENT_3 =
            new ContainerProperty<>(new TypeToken<Optional<Enchantment>>() {});

    public static final ContainerProperty<Integer> SHOWN_ENCHANTMENT_LEVEL_1 = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> SHOWN_ENCHANTMENT_LEVEL_2 = new ContainerProperty<>(TypeToken.of(Integer.class));

    public static final ContainerProperty<Integer> SHOWN_ENCHANTMENT_LEVEL_3 = new ContainerProperty<>(TypeToken.of(Integer.class));

    private ContainerProperties() {
    }
}
