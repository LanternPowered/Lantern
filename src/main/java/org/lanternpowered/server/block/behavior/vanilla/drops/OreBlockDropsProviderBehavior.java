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
package org.lanternpowered.server.block.behavior.vanilla.drops;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.block.behavior.simple.AbstractBlockDropsProviderBehavior;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class OreBlockDropsProviderBehavior extends AbstractBlockDropsProviderBehavior {

    private final Random random = new Random();
    private final IntSupplier baseQuantitySupplier;
    private final Supplier<ItemStackSnapshot> itemStackSnapshot;

    public OreBlockDropsProviderBehavior(Supplier<ItemStackSnapshot> itemStackSnapshot) {
        this(itemStackSnapshot, () -> 1);
    }

    public OreBlockDropsProviderBehavior(Supplier<ItemStackSnapshot> itemStackSnapshot, IntSupplier baseQuantitySupplier) {
        checkNotNull(baseQuantitySupplier, "baseQuantitySupplier");
        checkNotNull(itemStackSnapshot, "itemStackSnapshot");
        this.itemStackSnapshot = itemStackSnapshot;
        this.baseQuantitySupplier = baseQuantitySupplier;
    }

    @Override
    protected void collectDrops(BehaviorContext context, List<ItemStackSnapshot> itemStacks) {
        final int factor = Math.max(0, this.random.nextInt(2 - context.get(Parameters.LOOTING_LEVEL).orElse(0)) - 1) + 1;
        final int quantity = this.baseQuantitySupplier.getAsInt() * factor;
        final ItemStack itemStack = this.itemStackSnapshot.get().createStack();
        itemStack.setQuantity(quantity);
        itemStacks.add(itemStack.createSnapshot());
        // TODO: Bug here, there are more then 4 items added, but only 2 are spawned
        /*
        for (int i = 0; i < quantity; i++) {
            itemStacks.add(this.itemStackSnapshot.get());
        }
        */
    }
}
