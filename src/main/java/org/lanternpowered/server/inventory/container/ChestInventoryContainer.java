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
package org.lanternpowered.server.inventory.container;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.block.ChestInventory;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.text.Text;

public class ChestInventoryContainer extends LanternContainer {

    public ChestInventoryContainer(LanternPlayerInventory humanInventory, ChestInventory openInventory) {
        super(openInventory.getName(), humanInventory, openInventory);
    }

    @Override
    protected void openInventoryFor(LanternPlayer viewer) {
        viewer.getConnection().send(new MessagePlayOutOpenWindow(this.windowId, MessagePlayOutOpenWindow.WindowType.CONTAINER,
                Text.of(this.openInventory.getName()), this.openInventory.getSlots().size(), 0));
    }
}
