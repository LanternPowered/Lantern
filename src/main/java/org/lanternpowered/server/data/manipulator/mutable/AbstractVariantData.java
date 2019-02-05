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
package org.lanternpowered.server.data.manipulator.mutable;

import org.lanternpowered.server.data.manipulator.IDataManipulatorBase;
import org.lanternpowered.server.data.manipulator.immutable.IImmutableVariantData;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.ImmutableVariantData;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import org.spongepowered.api.data.value.Value;

@SuppressWarnings("unchecked")
public class AbstractVariantData<E, M extends VariantData<E, M, I>, I extends ImmutableVariantData<E, I, M>>
        extends AbstractData<M, I> implements IVariantData<E, M, I> {

    private final Key<? extends Value<E>> variantKey;

    protected AbstractVariantData(Class<M> manipulatorType, Class<I> immutableManipulatorType, Key<? extends Value.Mutable<E>> variantKey, E defaultVariant) {
        super(manipulatorType, immutableManipulatorType);
        getValueCollection().register(variantKey, defaultVariant);
        this.variantKey = variantKey;
    }

    protected AbstractVariantData(I manipulator) {
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractVariantData(M manipulator) {
        this((IDataManipulatorBase<M, I>) manipulator);
    }

    protected AbstractVariantData(IDataManipulatorBase<M, I> manipulator) {
        super(manipulator);
        if (manipulator instanceof IVariantData) {
            this.variantKey = ((IVariantData<E, M, I>) manipulator).getVariantKey();
        } else {
            this.variantKey = ((IImmutableVariantData<E, I, M>) manipulator).getVariantKey();
        }
    }

    @Override
    public Value.Mutable<E> type() {
        return getValue(this.variantKey).get().asMutable();
    }

    @Override
    public Key<? extends Value<E>> getVariantKey() {
        return this.variantKey;
    }
}
