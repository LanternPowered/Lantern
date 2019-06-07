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

import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.Carrier;

import java.lang.ref.WeakReference;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public interface CarrierReference<T> {

    static <T> CarrierReference<T> of(Class<T> carrierType) {
        return new CarrierReference<T>() {
            @Nullable private Object obj;

            @Override
            public Optional<Carrier> asCarrier() {
                return (Optional<Carrier>) get();
            }

            @Override
            public Optional<T> get() {
                if (this.obj == null) {
                    return Optional.empty();
                }
                if (this.obj instanceof WeakReference) {
                    return Optional.ofNullable(carrierType.cast(((WeakReference) this.obj).get()));
                }
                return Optional.of(carrierType.cast(this.obj));
            }

            @Override
            public <R> Optional<R> as(Class<R> ret) {
                return get().filter(ret::isInstance).map(ret::cast);
            }

            @Override
            public void set(@Nullable Carrier carrier) {
                this.obj = !carrierType.isInstance(carrier) ? null : (carrier instanceof TileEntity || carrier instanceof Entity) ?
                        new WeakReference<>(carrier) : carrier;
            }
        };
    }

    Optional<Carrier> asCarrier();

    Optional<T> get();

    <R> Optional<R> as(Class<R> ret);

    void set(@Nullable Carrier carrier);
}
