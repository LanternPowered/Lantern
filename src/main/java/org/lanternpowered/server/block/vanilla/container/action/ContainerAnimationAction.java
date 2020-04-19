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
package org.lanternpowered.server.block.vanilla.container.action;

import org.lanternpowered.server.block.action.BlockAction;
import org.lanternpowered.server.block.action.BlockActionData;

public final class ContainerAnimationAction implements BlockAction {

    public static final ContainerAnimationAction OPEN = new ContainerAnimationAction(true);
    public static final ContainerAnimationAction CLOSE = new ContainerAnimationAction(false);

    private final boolean open;

    private ContainerAnimationAction(boolean open) {
        this.open = open;
    }

    @Override
    public void fill(BlockActionData actionData) {
        actionData.set(0, 1);
        actionData.set(1, this.open ? 1 : 0);
    }

    @Override
    public BlockAction.Type type() {
        // The open action should be send to all the players
        // who start tracking the block
        return this.open ? BlockAction.Type.CONTINUOUS : BlockAction.Type.SINGLE;
    }
}
