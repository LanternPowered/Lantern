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
package org.lanternpowered.server.block.trait;

import com.google.common.base.Objects;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;

/**
 * This object is a key that is used to modify values
 * of a block state.
 */
public final class BlockTraitKey<E extends Comparable<E>, V extends MutableBlockTraitValue<E>> implements Key<V> {

    private final BlockTrait<E> blockTrait;
    private final Class<E> elementClass;
    private final Class<V> valueClass;
    private final DataQuery path;

    public BlockTraitKey(BlockTrait<E> blockTrait, Class<E> elementClass, Class<V> valueClass) {
        this.path = DataQuery.of(blockTrait.getName());
        this.elementClass = elementClass;
        this.valueClass = valueClass;
        this.blockTrait = blockTrait;
    }

    /**
     * Gets the block trait attached to this key.
     * 
     * @return the block trait
     */
    public BlockTrait<E> getBlockTrait() {
        return this.blockTrait;
    }

    @Override
    public Class<V> getValueClass() {
        return this.valueClass;
    }

    @Override
    public DataQuery getQuery() {
        return this.path;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.blockTrait, this.elementClass, this.valueClass, this.path);
    }

    @Override
    public String toString() {
        return "BlockTraitKey{Value:" + this.valueClass.getName() + "<" + this.elementClass + ">, Query: " + this.path.toString() + "}";
    }
}
