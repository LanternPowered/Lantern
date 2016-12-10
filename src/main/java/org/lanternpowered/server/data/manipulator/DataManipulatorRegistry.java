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
package org.lanternpowered.server.data.manipulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.manipulator.immutable.IImmutableDataManipulator;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableColoredData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableCommandData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableDisplayNameData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableDyeableData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableFireworkEffectData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableFireworkRocketData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutablePotionEffectData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableRepresentedItemData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableRepresentedPlayerData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableRotationalData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableSkullData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableTargetedLocationData;
import org.lanternpowered.server.data.manipulator.immutable.LanternImmutableWetData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableAuthorData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableBlockItemData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableBreakableData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableCoalData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableCookedFishData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableDurabilityData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableEnchantmentData;
import org.lanternpowered.server.data.manipulator.immutable.item.LanternImmutableFishData;
import org.lanternpowered.server.data.manipulator.mutable.IDataManipulator;
import org.lanternpowered.server.data.manipulator.mutable.LanternColoredData;
import org.lanternpowered.server.data.manipulator.mutable.LanternCommandData;
import org.lanternpowered.server.data.manipulator.mutable.LanternDisplayNameData;
import org.lanternpowered.server.data.manipulator.mutable.LanternDyeableData;
import org.lanternpowered.server.data.manipulator.mutable.LanternFireworkEffectData;
import org.lanternpowered.server.data.manipulator.mutable.LanternFireworkRocketData;
import org.lanternpowered.server.data.manipulator.mutable.LanternPotionEffectData;
import org.lanternpowered.server.data.manipulator.mutable.LanternRepresentedItemData;
import org.lanternpowered.server.data.manipulator.mutable.LanternRepresentedPlayerData;
import org.lanternpowered.server.data.manipulator.mutable.LanternRotationalData;
import org.lanternpowered.server.data.manipulator.mutable.LanternSkullData;
import org.lanternpowered.server.data.manipulator.mutable.LanternTargetedLocationData;
import org.lanternpowered.server.data.manipulator.mutable.LanternWetData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternAuthorData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternBlockItemData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternBreakableData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternCoalData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternCookedFishData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternDurabilityData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternEnchantmentData;
import org.lanternpowered.server.data.manipulator.mutable.item.LanternFishData;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableColoredData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableCommandData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDisplayNameData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableDyeableData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFireworkEffectData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFireworkRocketData;
import org.spongepowered.api.data.manipulator.immutable.ImmutablePotionEffectData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRepresentedItemData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRepresentedPlayerData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableRotationalData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableSkullData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableTargetedLocationData;
import org.spongepowered.api.data.manipulator.immutable.ImmutableWetData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableAuthorData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableBlockItemData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableBreakableData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableCoalData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableCookedFishData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableDurabilityData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableEnchantmentData;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableFishData;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.manipulator.mutable.FireworkEffectData;
import org.spongepowered.api.data.manipulator.mutable.FireworkRocketData;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.RotationalData;
import org.spongepowered.api.data.manipulator.mutable.SkullData;
import org.spongepowered.api.data.manipulator.mutable.TargetedLocationData;
import org.spongepowered.api.data.manipulator.mutable.WetData;
import org.spongepowered.api.data.manipulator.mutable.item.AuthorData;
import org.spongepowered.api.data.manipulator.mutable.item.BlockItemData;
import org.spongepowered.api.data.manipulator.mutable.item.BreakableData;
import org.spongepowered.api.data.manipulator.mutable.item.CoalData;
import org.spongepowered.api.data.manipulator.mutable.item.CookedFishData;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.FishData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataManipulatorRegistry {

    private static final DataManipulatorRegistry INSTANCE = new DataManipulatorRegistry();

    public static DataManipulatorRegistry get() {
        return INSTANCE;
    }

    private final Map<Class, DataManipulatorRegistration> registrationByClass = new HashMap<>();

    {
        register(ColoredData.class, LanternColoredData::new, LanternColoredData::new, LanternColoredData::new,
                ImmutableColoredData.class, LanternImmutableColoredData::new, LanternImmutableColoredData::new);
        register(CommandData.class, LanternCommandData::new, LanternCommandData::new, LanternCommandData::new,
                ImmutableCommandData.class, LanternImmutableCommandData::new, LanternImmutableCommandData::new);
        register(DisplayNameData.class, LanternDisplayNameData::new, LanternDisplayNameData::new, LanternDisplayNameData::new,
                ImmutableDisplayNameData.class, LanternImmutableDisplayNameData::new, LanternImmutableDisplayNameData::new);
        register(DyeableData.class, LanternDyeableData::new, LanternDyeableData::new, LanternDyeableData::new,
                ImmutableDyeableData.class, LanternImmutableDyeableData::new, LanternImmutableDyeableData::new);
        register(FireworkEffectData.class, LanternFireworkEffectData::new, LanternFireworkEffectData::new, LanternFireworkEffectData::new,
                ImmutableFireworkEffectData.class, LanternImmutableFireworkEffectData::new, LanternImmutableFireworkEffectData::new);
        register(FireworkRocketData.class, LanternFireworkRocketData::new, LanternFireworkRocketData::new, LanternFireworkRocketData::new,
                ImmutableFireworkRocketData.class, LanternImmutableFireworkRocketData::new, LanternImmutableFireworkRocketData::new);
        register(PotionEffectData.class, LanternPotionEffectData::new, LanternPotionEffectData::new, LanternPotionEffectData::new,
                ImmutablePotionEffectData.class, LanternImmutablePotionEffectData::new, LanternImmutablePotionEffectData::new);
        register(RepresentedItemData.class, LanternRepresentedItemData::new, LanternRepresentedItemData::new, LanternRepresentedItemData::new,
                ImmutableRepresentedItemData.class, LanternImmutableRepresentedItemData::new, LanternImmutableRepresentedItemData::new);
        register(RepresentedPlayerData.class, LanternRepresentedPlayerData::new, LanternRepresentedPlayerData::new, LanternRepresentedPlayerData::new,
                ImmutableRepresentedPlayerData.class, LanternImmutableRepresentedPlayerData::new, LanternImmutableRepresentedPlayerData::new);
        register(RotationalData.class, LanternRotationalData::new, LanternRotationalData::new, LanternRotationalData::new,
                ImmutableRotationalData.class, LanternImmutableRotationalData::new, LanternImmutableRotationalData::new);
        register(SkullData.class, LanternSkullData::new, LanternSkullData::new, LanternSkullData::new,
                ImmutableSkullData.class, LanternImmutableSkullData::new, LanternImmutableSkullData::new);
        register(TargetedLocationData.class, LanternTargetedLocationData::new, LanternTargetedLocationData::new, LanternTargetedLocationData::new,
                ImmutableTargetedLocationData.class, LanternImmutableTargetedLocationData::new, LanternImmutableTargetedLocationData::new);
        register(WetData.class, LanternWetData::new, LanternWetData::new, LanternWetData::new,
                ImmutableWetData.class, LanternImmutableWetData::new, LanternImmutableWetData::new);

        // item package
        register(AuthorData.class, LanternAuthorData::new, LanternAuthorData::new, LanternAuthorData::new,
                ImmutableAuthorData.class, LanternImmutableAuthorData::new, LanternImmutableAuthorData::new);
        register(BlockItemData.class, LanternBlockItemData::new, LanternBlockItemData::new, LanternBlockItemData::new,
                ImmutableBlockItemData.class, LanternImmutableBlockItemData::new, LanternImmutableBlockItemData::new);
        register(BreakableData.class, LanternBreakableData::new, LanternBreakableData::new, LanternBreakableData::new,
                ImmutableBreakableData.class, LanternImmutableBreakableData::new, LanternImmutableBreakableData::new);
        register(CoalData.class, LanternCoalData::new, LanternCoalData::new, LanternCoalData::new,
                ImmutableCoalData.class, LanternImmutableCoalData::new, LanternImmutableCoalData::new);
        register(CookedFishData.class, LanternCookedFishData::new, LanternCookedFishData::new, LanternCookedFishData::new,
                ImmutableCookedFishData.class, LanternImmutableCookedFishData::new, LanternImmutableCookedFishData::new);
        register(DurabilityData.class, LanternDurabilityData::new, LanternDurabilityData::new, LanternDurabilityData::new,
                ImmutableDurabilityData.class, LanternImmutableDurabilityData::new, LanternImmutableDurabilityData::new);
        register(EnchantmentData.class, LanternEnchantmentData::new, LanternEnchantmentData::new, LanternEnchantmentData::new,
                ImmutableEnchantmentData.class, LanternImmutableEnchantmentData::new, LanternImmutableEnchantmentData::new);
        register(FishData.class, LanternFishData::new, LanternFishData::new, LanternFishData::new,
                ImmutableFishData.class, LanternImmutableFishData::new, LanternImmutableFishData::new);
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> DataManipulatorRegistration<M, I> register(
            Class<M> manipulatorType, Supplier<M> manipulatorSupplier, Function<M, M> manipulatorCopyFunction, Function<I, M> immutableToMutableFunction,
            Class<I> immutableManipulatorType, Supplier<I> immutableManipulatorSupplier, Function<M, I> mutableToImmutableFunction) {
        checkNotNull(manipulatorType, "manipulatorType");
        checkNotNull(manipulatorSupplier, "manipulatorSupplier");
        checkNotNull(immutableManipulatorType, "immutableManipulatorType");
        checkNotNull(immutableManipulatorSupplier, "immutableManipulatorSupplier");
        final M manipulator = manipulatorSupplier.get();
        checkArgument(manipulator instanceof IDataManipulator,
                "The mutable manipulator implementation must implement IDataManipulator.");
        //noinspection unchecked
        final Class<M> manipulatorType1 = ((IDataManipulator<M, I>) manipulator).getMutableType();
        checkArgument(manipulatorType1 == manipulatorType,
                "The mutable data manipulator returns a different manipulator type, expected %s, but got %s",
                manipulatorType, manipulatorType1);
        final I immutableManipulator = immutableManipulatorSupplier.get();
        checkArgument(immutableManipulator instanceof IImmutableDataManipulator,
                "The immutable manipulator implementation must implement IImmutableData.");
        //noinspection unchecked
        final Class<I> immutableManipulatorType1 = ((IImmutableDataManipulator<I, M>) immutableManipulator).getImmutableType();
        checkArgument(immutableManipulatorType1 == immutableManipulatorType,
                "The immutable data manipulator returns a different manipulator type, expected %s, but got %s",
                immutableManipulatorType, immutableManipulatorType1);
        final Set<Key<?>> requiredKeys = new HashSet<>(manipulator.getKeys());
        final DataManipulatorRegistration<M, I> registration = new DataManipulatorRegistration<>(
                manipulatorType, manipulatorSupplier, manipulatorCopyFunction, immutableToMutableFunction,
                immutableManipulatorType, immutableManipulatorSupplier, mutableToImmutableFunction, requiredKeys);
        this.registrationByClass.put(manipulatorType, registration);
        this.registrationByClass.put(immutableManipulatorType, registration);
        return registration;
    }

    public Collection<DataManipulatorRegistration> getAll() {
        return Collections.unmodifiableCollection(this.registrationByClass.values());
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Optional<DataManipulatorRegistration<M, I>> getByMutable(
            Class<M> manipulatorType) {
        //noinspection unchecked
        return Optional.ofNullable(this.registrationByClass.get(checkNotNull(manipulatorType, "manipulatorType")));
    }

    public <M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> Optional<DataManipulatorRegistration<M, I>> getByImmutable(
            Class<I> immutableManipulatorType) {
        //noinspection unchecked
        return Optional.ofNullable(this.registrationByClass.get(checkNotNull(immutableManipulatorType, "immutableManipulatorType")));
    }
}
