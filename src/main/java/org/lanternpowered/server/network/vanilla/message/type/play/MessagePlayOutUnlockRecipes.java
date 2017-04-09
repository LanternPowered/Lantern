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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.util.collect.Lists2;

import java.util.List;

public final class MessagePlayOutUnlockRecipes implements Message {

    private final boolean notification;
    private final boolean openRecipeBook;
    private final boolean craftingFilter;
    private final List<Entry> entries;

    public MessagePlayOutUnlockRecipes(List<Entry> entries, boolean notification, boolean openRecipeBook, boolean craftingFilter) {
        this.entries = ImmutableList.copyOf(entries);
        this.notification = notification;
        this.openRecipeBook = openRecipeBook;
        this.craftingFilter = craftingFilter;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public boolean isNotification() {
        return this.notification;
    }

    public boolean hasOpenCraftingBook() {
        return this.openRecipeBook;
    }

    public boolean hasCraftingFilter() {
        return this.craftingFilter;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("notification", this.notification)
                .add("openRecipeBook", this.openRecipeBook)
                .add("craftingFilter", this.craftingFilter)
                .add("entries", Lists2.toString(this.entries))
                .toString();
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

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("id", this.id)
                    .add("unlocked", this.unlocked)
                    .add("displayed", this.displayed)
                    .toString();
        }
    }
}
