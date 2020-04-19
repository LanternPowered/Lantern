/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
