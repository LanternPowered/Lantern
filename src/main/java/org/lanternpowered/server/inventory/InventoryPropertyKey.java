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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.Objects;

import javax.annotation.Nullable;

final class InventoryPropertyKey {

    private final Class<? extends InventoryProperty> type;
    @Nullable private final Object key;

    InventoryPropertyKey(Class<? extends InventoryProperty> type, @Nullable Object key) {
        this.type = type;
        this.key = key;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InventoryPropertyKey)) {
            return false;
        }
        final InventoryPropertyKey other0 = (InventoryPropertyKey) other;
        return other0.type == this.type && Objects.equals(other0.key, this.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.key);
    }
}
