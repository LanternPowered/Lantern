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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.item.property.CooldownProperty;
import org.lanternpowered.server.item.property.DualWieldProperty;
import org.lanternpowered.server.item.property.HealthRestorationProperty;
import org.lanternpowered.server.item.property.MaximumUseDurationProperty;
import org.lanternpowered.server.item.property.MinimumUseDurationProperty;
import org.spongepowered.api.data.property.item.ApplicableEffectProperty;
import org.spongepowered.api.data.property.item.ArmorTypeProperty;
import org.spongepowered.api.data.property.item.EquipmentProperty;
import org.spongepowered.api.data.property.item.FoodRestorationProperty;
import org.spongepowered.api.data.property.item.SaturationProperty;
import org.spongepowered.api.data.property.item.ToolTypeProperty;
import org.spongepowered.api.data.property.item.UseLimitProperty;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;

import java.util.Collection;

public final class PropertyProviders {

    private static final DualWieldProperty DUAL_WIELD_PROPERTY_TRUE = new DualWieldProperty(true);
    private static final DualWieldProperty DUAL_WIELD_PROPERTY_FALSE = new DualWieldProperty(false);

    public static PropertyProviderCollection applicableEffects(PotionEffect... potionEffects) {
        checkNotNull(potionEffects, "potionEffects");
        final ApplicableEffectProperty property = new ApplicableEffectProperty(ImmutableSet.copyOf(potionEffects));
        return PropertyProviderCollection.builder()
                .add(ApplicableEffectProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection applicableEffects(Collection<PotionEffect> potionEffects) {
        checkNotNull(potionEffects, "potionEffects");
        final ApplicableEffectProperty property = new ApplicableEffectProperty(ImmutableSet.copyOf(potionEffects));
        return PropertyProviderCollection.builder()
                .add(ApplicableEffectProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection applicableEffects(ObjectProvider<Collection<PotionEffect>> potionEffects) {
        return PropertyProviderCollection.builder()
                .add(ApplicableEffectProperty.class, (itemType, itemStack) ->
                        new ApplicableEffectProperty(ImmutableSet.copyOf(potionEffects.get(itemType, itemStack))))
                .build();
    }

    public static PropertyProviderCollection foodRestoration(int food) {
        final FoodRestorationProperty property = new FoodRestorationProperty(food);
        return PropertyProviderCollection.builder()
                .add(FoodRestorationProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection foodRestoration(ObjectProvider<Integer> food) {
        return PropertyProviderCollection.builder()
                .add(FoodRestorationProperty.class, (itemType, itemStack) ->
                        new FoodRestorationProperty(food.get(itemType, itemStack)))
                .build();
    }

    public static PropertyProviderCollection saturation(double saturation) {
        final SaturationProperty property = new SaturationProperty(saturation);
        return PropertyProviderCollection.builder()
                .add(SaturationProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection saturation(ObjectProvider<Double> saturation) {
        return PropertyProviderCollection.builder()
                .add(SaturationProperty.class, (itemType, itemStack) ->
                        new SaturationProperty(saturation.get(itemType, itemStack)))
                .build();
    }

    public static PropertyProviderCollection healthRestoration(double health) {
        final HealthRestorationProperty property = new HealthRestorationProperty(health);
        return PropertyProviderCollection.builder()
                .add(HealthRestorationProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection healthRestoration(ObjectProvider<Double> health) {
        return PropertyProviderCollection.builder()
                .add(HealthRestorationProperty.class, (itemType, itemStack) ->
                        new HealthRestorationProperty(health.get(itemType, itemStack)))
                .build();
    }

    public static PropertyProviderCollection useLimit(int limit) {
        final UseLimitProperty property = new UseLimitProperty(limit);
        return PropertyProviderCollection.builder()
                .add(UseLimitProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection useLimit(ObjectProvider<Integer> limit) {
        return PropertyProviderCollection.builder()
                .add(UseLimitProperty.class, (itemType, itemStack) ->
                        new UseLimitProperty(limit.get(itemType, itemStack)))
                .build();
    }

    public static PropertyProviderCollection useDuration(int duration) {
        return useDuration(duration, duration);
    }

    public static PropertyProviderCollection useDuration(int minimum, int maximum) {
        final MaximumUseDurationProperty maxProperty = new MaximumUseDurationProperty(maximum);
        final MinimumUseDurationProperty minProperty = new MinimumUseDurationProperty(minimum);
        return PropertyProviderCollection.builder()
                .add(MaximumUseDurationProperty.class, (itemType, itemStack) -> maxProperty)
                .add(MinimumUseDurationProperty.class, (itemType, itemStack) -> minProperty)
                .build();
    }

    public static PropertyProviderCollection minimumUseDuration(int minimum) {
        final MinimumUseDurationProperty minProperty = new MinimumUseDurationProperty(minimum);
        return PropertyProviderCollection.builder()
                .add(MinimumUseDurationProperty.class, (itemType, itemStack) -> minProperty)
                .build();
    }

    public static PropertyProviderCollection maximumUseDuration(int maximum) {
        final MaximumUseDurationProperty maxProperty = new MaximumUseDurationProperty(maximum);
        return PropertyProviderCollection.builder()
                .add(MaximumUseDurationProperty.class, (itemType, itemStack) -> maxProperty)
                .build();
    }

    public static PropertyProviderCollection cooldown(int cooldown) {
        final CooldownProperty property = new CooldownProperty(cooldown);
        return PropertyProviderCollection.builder()
                .add(CooldownProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection cooldown(ObjectProvider<Integer> cooldown) {
        return PropertyProviderCollection.builder()
                .add(CooldownProperty.class, (itemType, itemStack) ->
                        new CooldownProperty(cooldown.get(itemType, itemStack)))
                .build();
    }

    public static PropertyProviderCollection dualWield(boolean dualWield) {
        final DualWieldProperty property = dualWield ? DUAL_WIELD_PROPERTY_TRUE : DUAL_WIELD_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(DualWieldProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection dualWield(ObjectProvider<Boolean> dualWield) {
        return PropertyProviderCollection.builder()
                .add(DualWieldProperty.class, (itemType, itemStack) ->
                        dualWield.get(itemType, itemStack) ? DUAL_WIELD_PROPERTY_TRUE : DUAL_WIELD_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection toolType(ToolType toolType) {
        final ToolTypeProperty property = new ToolTypeProperty(toolType);
        return PropertyProviderCollection.builder()
                .add(ToolTypeProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection toolType(ObjectProvider<ToolType> toolType) {
        return PropertyProviderCollection.builder()
                .add(ToolTypeProperty.class, (itemType, itemStack) ->
                        new ToolTypeProperty(toolType.get(itemType, itemStack)))
                .build();
    }

    public static PropertyProviderCollection armorType(ArmorType armorType) {
        final ArmorTypeProperty property = new ArmorTypeProperty(armorType);
        return PropertyProviderCollection.builder()
                .add(ArmorTypeProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection armorType(ObjectProvider<ArmorType> armorType) {
        return PropertyProviderCollection.builder()
                .add(ArmorTypeProperty.class, (itemType, itemStack) ->
                        new ArmorTypeProperty(armorType.get(itemType, itemStack)))
                .build();
    }

    public static PropertyProviderCollection equipmentType(EquipmentType equipmentType) {
        final EquipmentProperty property = new EquipmentProperty(equipmentType);
        return PropertyProviderCollection.builder()
                .add(EquipmentProperty.class, (itemType, itemStack) -> property)
                .build();
    }

    public static PropertyProviderCollection equipmentType(ObjectProvider<EquipmentType> equipmentType) {
        return PropertyProviderCollection.builder()
                .add(EquipmentProperty.class, (itemType, itemStack) ->
                        new EquipmentProperty(equipmentType.get(itemType, itemStack)))
                .build();
    }

    private PropertyProviders() {
    }
}
