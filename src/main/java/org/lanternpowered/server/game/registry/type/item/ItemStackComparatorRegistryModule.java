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
package org.lanternpowered.server.game.registry.type.item;

import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.DurabilityData;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ItemStackComparatorRegistryModule implements RegistryModule {

    @RegisterCatalog(ItemStackComparators.class) private final Map<String, Comparator<ItemStack>> mappings = new HashMap<>();

    @Override
    public void registerDefaults() {
        final Comparator<ItemStack> type = Comparator.comparing(i -> i.getType().getKey());
        map("type", type);
        final Comparator<ItemStack> size = Comparator.comparing(ItemStack::getQuantity);
        map("size", size);
        final Comparator<ItemStack> typeSize = type.thenComparing(size);
        map("type_size", typeSize);
        map("default", typeSize);
        final Properties properties = new Properties();
        map("properties", properties);
        final ItemDataComparator itemData = new ItemDataComparator();
        map("item_data", itemData);
        map("item_data_ignore_damage", new ItemDataComparator(DurabilityData.class));
        map("ignore_size", type.thenComparing(properties).thenComparing(itemData));
        map("all", type.thenComparing(size).thenComparing(properties).thenComparing(itemData));
    }

    private void map(String id, Comparator<ItemStack> value) {
        this.mappings.put(id, value);
    }

    private static final class Properties implements Comparator<ItemStack> {

        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            final Map<Property<?>, Object> properties = new HashMap<>(o2.getProperties());
            for (Map.Entry<Property<?>, ?> entry : o1.getProperties().entrySet()) {
                final Object o1Value = entry.getValue();
                final Object o2Value = properties.get(entry.getKey());
                if (Objects.equals(o1Value, o2Value)) {
                    properties.remove(entry.getKey());
                } else {
                    return -1;
                }
            }
            return properties.size();
        }
    }

    private static final class ItemDataComparator implements Comparator<ItemStack> {

        private final Class<? extends DataManipulator<?, ?>>[] ignored;

        @SafeVarargs
        ItemDataComparator(Class<? extends DataManipulator<?, ?>>... ignored) {
            this.ignored = ignored;
        }

        @Override
        public int compare(ItemStack o1, ItemStack o2) {
            final Set<DataManipulator<?, ?>> manipulators = new LinkedHashSet<>(o2.getContainers());
            for (final DataManipulator<?, ?> manipulator : o1.getContainers()) {
                if (manipulators.contains(manipulator)) {
                    manipulators.remove(manipulator);
                } else if (!isIgnored(manipulators, manipulator)) {
                    return -1;
                }
            }
            return manipulators.size();
        }

        private boolean isIgnored(Set<DataManipulator<?, ?>> list, DataManipulator<?, ?> toCheck) {
            for (Class<? extends DataManipulator<?, ?>> ignore : this.ignored) {
                if (ignore.isAssignableFrom(toCheck.getClass())) {
                    list.removeIf(manip -> ignore.isAssignableFrom(manip.getClass()));
                    return true;
                }
            }
            return false;
        }
    }
}
