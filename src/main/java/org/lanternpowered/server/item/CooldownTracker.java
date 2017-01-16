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

import static com.google.common.base.Preconditions.checkArgument;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.spongepowered.api.item.ItemType;

import java.util.OptionalInt;

public class CooldownTracker {

    private final Int2LongMap map = new Int2LongOpenHashMap();

    {
        this.map.defaultReturnValue(-1L);
    }

    /**
     * Sets the cooldown for the specified {@link ItemType}.
     *
     * @param itemType The item type
     * @param cooldown The cooldown
     */
    public void set(ItemType itemType, int cooldown) {
        checkArgument(cooldown > 0);
        final int internalId = ItemRegistryModule.get().getInternalId(itemType);
        synchronized (this.map) {
            this.map.put(internalId, LanternGame.currentTimeTicks() + cooldown);
        }
        set0(internalId, cooldown);
    }

    protected void set0(int internalId, int cooldown) {
    }

    /**
     * Sets the cooldown for the specified {@link ItemType}.
     *
     * @param itemType The item type
     * @return Whether the cooldown was removed successfully
     */
    public boolean remove(ItemType itemType) {
        final int internalId = ItemRegistryModule.get().getInternalId(itemType);
        final long time;
        synchronized (this.map) {
            time = this.map.remove(internalId);
        }
        if (time == -1L || time - LanternGame.currentTimeTicks() <= 0) {
            return false;
        }
        remove0(internalId);
        return true;
    }

    protected void remove0(int internalId) {
    }

    /**
     * Gets the cooldown for the specified {@link ItemType} if present.
     *
     * @param itemType The item type
     * @return The cooldown
     */
    public OptionalInt get(ItemType itemType) {
        final int internalId = ItemRegistryModule.get().getInternalId(itemType);
        synchronized (this.map) {
            final long time = this.map.get(internalId);
            if (time != -1L) {
                final long current = LanternGame.currentTimeTicks();
                if (time <= current) {
                    this.map.remove(internalId);
                } else {
                    return OptionalInt.of((int) (time - current));
                }
            }
            return OptionalInt.empty();
        }
    }

    public boolean contains(ItemType itemType) {
        final int internalId = ItemRegistryModule.get().getInternalId(itemType);
        synchronized (this.map) {
            final long time = this.map.get(internalId);
            if (time != -1L) {
                final long current = LanternGame.currentTimeTicks();
                if (time <= current) {
                    this.map.remove(internalId);
                } else {
                    return true;
                }
            }
            return false;
        }
    }
}
