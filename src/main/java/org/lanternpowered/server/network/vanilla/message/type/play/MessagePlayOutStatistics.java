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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.network.message.Message;

import java.util.Set;

public final class MessagePlayOutStatistics implements Message {
    private final ImmutableSet<Entry> entries;

    public MessagePlayOutStatistics(Set<Entry> entries) {
        this.entries = ImmutableSet.copyOf(checkNotNull(entries, "entries"));
    }

    /**
     * Gets the entries.
     * 
     * @return the entries
     */
    public ImmutableSet<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {
        private final String name;
        private final int value;

        /**
         * Creates a new statistics message entry.
         * 
         * @param name the name
         * @param value the value
         */
        public Entry(String name, int value) {
            this.name = checkNotNullOrEmpty(name, "name");
            this.value = value;
        }

        /**
         * Gets the name of the entry.
         * 
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the value of the entry.
         * 
         * @return the value
         */
        public int getValue() {
            return this.value;
        }

    }
}
