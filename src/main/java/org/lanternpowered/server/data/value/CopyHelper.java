/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.data.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.weighted.WeightedTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("unchecked")
public final class CopyHelper {

    /**
     * Creates a copy for the given object of type {@link T}.
     *
     * @param object The object
     * @param <T> The object type
     * @return The copied object
     */
    public static <T> T copy(T object) {
        checkNotNull(object, "object");
        if (object instanceof Set) {
            return (T) copySetAsMutable((Set) object);
        } else if (object instanceof List) {
            return (T) copyListAsMutable((List) object);
        } else if (object instanceof Map) {
            return (T) copyAsMutable((Map) object);
        } else if (object instanceof WeightedTable) {
            return (T) copyWeightedTable((WeightedTable) object);
        } else if (object instanceof ItemStack) {
            return (T) ((ItemStack) object).copy();
        } else if (object instanceof Optional) {
            return (T) ((Optional<Object>) object).map(CopyHelper::copy);
        }
        return object;
    }

    public static <T> Set<T> copySetAsMutable(Set<T> set) {
        if (set instanceof LinkedHashSet) {
            return new LinkedHashSet<>(set);
        } else if (set instanceof TreeSet) {
            return new TreeSet<>(set);
        }
        return new HashSet<>(set);
    }

    public static <T> List<T> copyListAsMutable(List<T> list) {
        if (list instanceof LinkedList || list instanceof ImmutableList) {
            return new LinkedList<>(list);
        }
        return new ArrayList<>(list);
    }

    public static <K, V> Map<K, V> copyAsMutable(Map<K, V> map) {
        if (map instanceof BiMap) {
            return HashBiMap.create(map);
        } else if (map instanceof LinkedHashMap) {
            return new LinkedHashMap<>(map);
        }
        return new HashMap<>(map);
    }

    public static <T> WeightedTable<T> copyWeightedTable(WeightedTable<T> table) {
        final WeightedTable<T> copy = new WeightedTable<>(table.getRolls());
        copy.addAll(table.getEntries());
        return copy;
    }

    public static <K, V> ImmutableMap<K, V> mapAsUnmodifiable(Map<K, V> map) {
        if (map instanceof ImmutableMap) {
            return (ImmutableMap<K, V>) map;
        } else if (map instanceof BiMap) {
            return ImmutableBiMap.copyOf(map);
        }
        return ImmutableMap.copyOf(map);
    }

    private CopyHelper() {
    }
}
