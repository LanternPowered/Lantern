/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.game;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@NonnullByDefault
public final class LanternGameDictionary implements GameDictionary {

    private final Map<String, Set<ItemType>> map = Maps.newConcurrentMap();

    @Override
    public void register(String key, ItemType type) {
        checkNotNullOrEmpty(key, "key");
        checkNotNull(type, "type");
        this.map.computeIfAbsent(key, key0 -> Sets.newConcurrentHashSet()).add(type);
    }

    @Override
    public Set<ItemType> get(String key) {
        Set<ItemType> set = this.map.get(checkNotNull(key, "key"));
        if (set != null) {
            return ImmutableSet.copyOf(set);
        }
        return ImmutableSet.of();
    }

    @Override
    public Map<String, Set<ItemType>> getAllItems() {
        ImmutableMap.Builder<String, Set<ItemType>> builder = ImmutableMap.builder();
        for (Entry<String, Set<ItemType>> en : this.map.entrySet()) {
            builder.put(en.getKey(), ImmutableSet.copyOf(en.getValue()));
        }
        return builder.build();
    }

}
