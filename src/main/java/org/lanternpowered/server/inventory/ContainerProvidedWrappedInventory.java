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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link AbstractChildrenInventory} that contains a viewed
 * {@link AbstractMutableInventory} that will be displayed using
 * the supplied  {@link AbstractContainer}s.
 */
public final class ContainerProvidedWrappedInventory extends AbstractChildrenInventory implements IContainerProvidedInventory {

    private final Function<LanternPlayer, AbstractContainer> supplier;

    /**
     * Constructs a new {@link ContainerProvidedWrappedInventory}.
     *
     * @param viewed The inventory that will be viewed
     * @param supplier The container supplier
     */
    public ContainerProvidedWrappedInventory(AbstractMutableInventory viewed, Supplier<AbstractContainer> supplier) {
        this(viewed, player -> supplier.get());
    }

    /**
     * Constructs a new {@link ContainerProvidedWrappedInventory}.
     *
     * @param viewed The inventory that will be viewed
     * @param supplier The container supplier
     */
    public ContainerProvidedWrappedInventory(AbstractMutableInventory viewed, Function<LanternPlayer, AbstractContainer> supplier) {
        checkNotNull(supplier, "supplier");
        initWithChildren(ImmutableList.of(viewed), false);
        this.supplier = supplier;
    }

    @Override
    public AbstractContainer createContainer(Player viewer) {
        return this.supplier.apply((LanternPlayer) viewer);
    }
 }
