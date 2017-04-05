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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.message.Message;

import java.util.List;

public final class MessagePlayOutUnlockRecipes implements Message {

    private final boolean flag1;
    private final boolean flag2;
    private final boolean flag3;
    private final List<Entry> entries;

    public MessagePlayOutUnlockRecipes(List<Entry> entries, boolean flag1, boolean flag2, boolean flag3) {
        this.entries = ImmutableList.copyOf(entries);
        this.flag1 = flag1;
        this.flag2 = flag2;
        this.flag3 = flag3;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public boolean hasFlag1() {
        return this.flag1;
    }

    public boolean hasFlag2() {
        return this.flag2;
    }

    public boolean hasFlag3() {
        return this.flag3;
    }

    public static final class Entry {

        private final String id;
        private final boolean unlocked;
        private final boolean displayed;

        public Entry(String id, boolean unlocked, boolean displayed) {
            this.id = id;
            this.unlocked = unlocked;
            this.displayed = displayed;
        }

        public String getId() {
            return this.id;
        }

        public boolean isUnlocked() {
            return this.unlocked;
        }

        public boolean isDisplayed() {
            return this.displayed;
        }
    }
}
