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

import org.spongepowered.api.item.inventory.Carrier;

import java.util.function.Supplier;

import javax.annotation.Nullable;

@FunctionalInterface
public interface CarrierBasedTransformer<R extends AbstractInventory, B extends AbstractArchetypeBuilder<R, ? super R, B>> {

    /**
     * Attempts to generate/load a {@link LanternInventoryArchetype} for the
     * given {@link Carrier}. A {@link Supplier} is applied to retrieve a
     * copy of the {@link AbstractBuilder} that this transformer is applied to.
     * It is recommend to cache the {@link LanternInventoryArchetype} and only
     * get a new {@link AbstractBuilder} instance when creating a new variant.
     * Returning {@code null} can be used to fallback to the default
     * {@link LanternInventoryArchetype}.
     *
     * @param carrier The carrier
     * @param builderSupplier The builder copy supplier
     * @return The inventory archetype, or {@code null} when falling back to default
     */
    @Nullable
    default LanternInventoryArchetype<R> apply(Carrier carrier, Supplier<B> builderSupplier) {
        return apply(carrier.getClass(), builderSupplier);
    }

    /**
     * Attempts to generate/load a {@link LanternInventoryArchetype} for the
     * given {@link Carrier} type. A {@link Supplier} is applied to retrieve a
     * copy of the {@link AbstractBuilder} that this transformer is applied to.
     * It is recommend to cache the {@link LanternInventoryArchetype} and only
     * get a new {@link AbstractBuilder} instance when creating a new variant.
     * Returning {@code null} can be used to fallback to the default
     * {@link LanternInventoryArchetype}.
     *
     * @param carrierType The carrier type
     * @param builderSupplier The builder copy supplier
     * @return The inventory archetype, or {@code null} when falling back to default
     */
    @Nullable
    LanternInventoryArchetype<R> apply(Class<? extends Carrier> carrierType, Supplier<B> builderSupplier);

}
