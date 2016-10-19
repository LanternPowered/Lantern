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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.slot.OutputSlot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.Inventory2D;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public final class AltParentProxyInventories {

    public static AbstractInventory getOriginal(Inventory inventory) {
        if (inventory instanceof AltParentProxyInventory) {
            return ((AltParentProxyInventory) inventory).delegate;
        }
        return (AbstractInventory) inventory;
    }

    public static AltParentProxyInventory get(Inventory parent, Inventory delegate) {
        if (delegate instanceof AltParentProxyInventory) {
            delegate = ((AltParentProxyInventory) delegate).delegate;
        }
        return cache.get(new CacheKey(parent, delegate));
    }

    static {
        register(CraftingInventory.class, AltParentProxyCraftingInventory::new);
        register(CraftingOutput.class, AltParentProxyCraftingOutput::new);
        register(Inventory.class, AltParentProxyInventory::new);
        register(Inventory2D.class, AltParentProxyInventory2D::new);
        register(InventoryColumn.class, AltParentProxyInventoryColumn::new);
        register(InventoryRow.class, AltParentProxyInventoryRow::new);
        register(GridInventory.class, AltParentProxyGridInventory::new);
        register(OrderedInventory.class, AltParentProxyOrderedInventory::new);
        register(OutputSlot.class, AltParentProxyOutputSlot::new);
        register(Slot.class, AltParentProxySlot::new);
    }

    private static final class CacheKey {

        private final Inventory parent;
        private final Inventory delegate;

        private CacheKey(Inventory parent, Inventory delegate) {
            this.parent = parent;
            this.delegate = delegate;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof CacheKey)) {
                return false;
            }
            final CacheKey other0 = (CacheKey) other;
            return other0.parent == this.parent && other0.delegate == this.delegate;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.parent, this.delegate);
        }
    }

    private static final LoadingCache<CacheKey, AltParentProxyInventory> cache =
            Caffeine.newBuilder().weakValues().build(AltParentProxyInventories::load);

    private static final Map<Class<?>, BiFunction<Inventory, Inventory, AltParentProxyInventory>> suppliers = new HashMap<>();
    private static final LoadingCache<Class<?>, BiFunction<Inventory, Inventory, AltParentProxyInventory>> supplierCache =
            Caffeine.newBuilder().build(AltParentProxyInventories::findSupplier);

    private static BiFunction<Inventory, Inventory, AltParentProxyInventory> findSupplier(Class<?> key) {
        BiFunction<Inventory, Inventory, AltParentProxyInventory> supplier;
        while (key != Object.class) {
            supplier = suppliers.get(key);
            if (supplier != null) {
                return supplier;
            }
            for (Class<?> interf : key.getInterfaces()) {
                supplier = suppliers.get(interf);
                if (supplier != null) {
                    return supplier;
                }
            }
            key = key.getSuperclass();
        }
        throw new IllegalStateException();
    }

    private static AltParentProxyInventory load(CacheKey key) {
        final BiFunction<Inventory, Inventory, AltParentProxyInventory> supplier = supplierCache.get(key.delegate.getClass());
        return supplier.apply(key.parent, key.delegate);
    }

    public static <T extends Inventory> void register(Class<T> type,
            BiFunction<Inventory, T, AltParentProxyInventory> supplier) {
        //noinspection unchecked
        suppliers.put(type, (BiFunction) supplier);
    }
}
