/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
