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
package org.lanternpowered.server.item;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.data.property.Properties;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.sound.music.MusicDisc;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;

import java.util.Collection;

public final class PropertyProviders {

    public static PropertyProviderCollection applicableEffects(PotionEffect... potionEffects) {
        return PropertyProviderCollection.constant(Properties.APPLICABLE_POTION_EFFECTS, ImmutableList.copyOf(potionEffects));
    }

    public static PropertyProviderCollection applicableEffects(Collection<PotionEffect> potionEffects) {
        return PropertyProviderCollection.constant(Properties.APPLICABLE_POTION_EFFECTS, ImmutableList.copyOf(potionEffects));
    }

    public static PropertyProviderCollection applicableEffects(PropertyProvider<Collection<PotionEffect>> provider) {
        return PropertyProviderCollection.of(Properties.APPLICABLE_POTION_EFFECTS, provider);
    }

    public static PropertyProviderCollection replenishedFood(double replenishedFood) {
        return PropertyProviderCollection.constant(Properties.REPLENISHED_FOOD, replenishedFood);
    }

    public static PropertyProviderCollection replenishedFood(PropertyProvider<Double> replenishedFood) {
        return PropertyProviderCollection.of(Properties.REPLENISHED_FOOD, replenishedFood);
    }

    public static PropertyProviderCollection saturation(double saturation) {
        return PropertyProviderCollection.constant(Properties.REPLENISHED_SATURATION, saturation);
    }

    public static PropertyProviderCollection saturation(PropertyProvider<Double> saturation) {
        return PropertyProviderCollection.of(Properties.REPLENISHED_SATURATION, saturation);
    }

    public static PropertyProviderCollection healthRestoration(double health) {
        return PropertyProviderCollection.constant(ItemProperties.HEALTH_RESTORATION, health);
    }

    public static PropertyProviderCollection healthRestoration(PropertyProvider<Double> health) {
        return PropertyProviderCollection.of(ItemProperties.HEALTH_RESTORATION, health);
    }

    public static PropertyProviderCollection useLimit(int limit) {
        return PropertyProviderCollection.constant(Properties.USE_LIMIT, limit);
    }

    public static PropertyProviderCollection useLimit(PropertyProvider<Integer> limit) {
        return PropertyProviderCollection.of(Properties.USE_LIMIT, limit);
    }

    public static PropertyProviderCollection useDuration(int duration) {
        return useDuration(duration, duration);
    }

    public static PropertyProviderCollection useDuration(int minimum, int maximum) {
        return PropertyProviderCollection.builder()
                .addConstant(ItemProperties.MAXIMUM_USE_DURATION, maximum)
                .addConstant(ItemProperties.MINIMUM_USE_DURATION, minimum)
                .build();
    }

    public static PropertyProviderCollection minimumUseDuration(int minimum) {
        return PropertyProviderCollection.constant(ItemProperties.MINIMUM_USE_DURATION, minimum);
    }

    public static PropertyProviderCollection maximumUseDuration(int maximum) {
        return PropertyProviderCollection.constant(ItemProperties.MAXIMUM_USE_DURATION, maximum);
    }

    public static PropertyProviderCollection cooldown(int cooldown) {
        return PropertyProviderCollection.constant(ItemProperties.USE_COOLDOWN, cooldown);
    }

    public static PropertyProviderCollection cooldown(PropertyProvider<Integer> cooldown) {
        return PropertyProviderCollection.of(ItemProperties.USE_COOLDOWN, cooldown);
    }

    public static PropertyProviderCollection alwaysConsumable(boolean alwaysConsumable) {
        return PropertyProviderCollection.constant(ItemProperties.IS_ALWAYS_CONSUMABLE, alwaysConsumable);
    }

    public static PropertyProviderCollection alwaysConsumable(PropertyProvider<Boolean> alwaysConsumable) {
        return PropertyProviderCollection.of(ItemProperties.IS_ALWAYS_CONSUMABLE, alwaysConsumable);
    }

    public static PropertyProviderCollection dualWield(boolean dualWield) {
        return PropertyProviderCollection.constant(ItemProperties.IS_DUAL_WIELDABLE, dualWield);
    }

    public static PropertyProviderCollection dualWield(PropertyProvider<Boolean> dualWield) {
        return PropertyProviderCollection.of(ItemProperties.IS_DUAL_WIELDABLE, dualWield);
    }

    public static PropertyProviderCollection toolType(ToolType toolType) {
        return PropertyProviderCollection.constant(Properties.TOOL_TYPE, toolType);
    }

    public static PropertyProviderCollection toolType(PropertyProvider<ToolType> toolType) {
        return PropertyProviderCollection.of(Properties.TOOL_TYPE, toolType);
    }

    public static PropertyProviderCollection armorType(ArmorType armorType) {
        return PropertyProviderCollection.constant(Properties.ARMOR_TYPE, armorType);
    }

    public static PropertyProviderCollection armorType(PropertyProvider<ArmorType> armorType) {
        return PropertyProviderCollection.of(Properties.ARMOR_TYPE, armorType);
    }

    public static PropertyProviderCollection equipmentType(EquipmentType equipmentType) {
        return PropertyProviderCollection.constant(Properties.EQUIPMENT_TYPE, equipmentType);
    }

    public static PropertyProviderCollection equipmentType(PropertyProvider<EquipmentType> equipmentType) {
        return PropertyProviderCollection.of(Properties.EQUIPMENT_TYPE, equipmentType);
    }

    public static PropertyProviderCollection musicDisc(MusicDisc musicDisc) {
        return PropertyProviderCollection.constant(Properties.MUSIC_DISC, musicDisc);
    }

    private PropertyProviders() {
    }
}
