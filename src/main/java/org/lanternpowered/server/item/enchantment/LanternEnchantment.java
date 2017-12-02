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
package org.lanternpowered.server.item.enchantment;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;

import java.util.Objects;

public final class LanternEnchantment implements Enchantment {

    private final EnchantmentType enchantmentType;
    private final int level;

    LanternEnchantment(EnchantmentType enchantmentType, int level) {
        this.enchantmentType = enchantmentType;
        this.level = level;
    }

    @Override
    public EnchantmentType getType() {
        return this.enchantmentType;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, getContentVersion())
                .set(Queries.ENCHANTMENT_ID, this.enchantmentType.getId())
                .set(Queries.LEVEL, this.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.enchantmentType, this.level);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final LanternEnchantment other = (LanternEnchantment) object;
        return Objects.equals(this.enchantmentType, other.enchantmentType) && Objects.equals(this.level, other.level);
    }
}
