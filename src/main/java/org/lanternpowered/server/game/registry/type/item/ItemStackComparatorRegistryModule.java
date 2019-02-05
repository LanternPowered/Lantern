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
