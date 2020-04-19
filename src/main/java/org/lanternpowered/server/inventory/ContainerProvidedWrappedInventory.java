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
