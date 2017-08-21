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
package org.lanternpowered.server.network.forge.message.type.handshake;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.lanternpowered.server.network.message.Message;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MessageForgeHandshakeOutRegistryData implements Message {

    private final List<Entry> entries;

    public MessageForgeHandshakeOutRegistryData(Iterable<Entry> entries) {
        this.entries = ImmutableList.copyOf(entries);
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {

        private final String name;
        private final Object2IntMap<String> ids;
        private final Map<String, String> overrides;
        private final Set<String> dummies;

        public Entry(String name, Object2IntMap<String> ids, Map<String, String> overrides, Set<String> dummies) {
            this.overrides = overrides;
            this.dummies = dummies;
            this.name = name;
            this.ids = ids;
        }

        public String getName() {
            return this.name;
        }

        public Object2IntMap<String> getIds() {
            return this.ids;
        }

        public Map<String, String> getOverrides() {
            return this.overrides;
        }

        public Set<String> getDummies() {
            return this.dummies;
        }
    }
}
