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

import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ContainerPart;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

public interface VanillaOpenableInventory extends OpenableInventory, OrderedInventory {

    /**
     * Constructs a {@link ClientContainer} for this {@link OpenableInventory}.
     *
     * @return The constructed client container
     */
    ClientContainer constructClientContainer0(LanternContainer container);

    @Override
    default ClientContainer constructClientContainer(LanternContainer container) {
        final ClientContainer clientContainer = constructClientContainer0(container);
        final LanternPlayerInventory playerInventory = container.getPlayerInventory();
        // Bind the cursor item slot
        clientContainer.bindCursor(container.getCursorSlot());
        final ContainerPart part = clientContainer.getTop();
        // Register the top inventory slots
        ((LanternOrderedInventory) this).getIndexBySlots().object2IntEntrySet().forEach(entry -> {
            if (!playerInventory.getMain().isChild(entry.getKey())) {
                part.bindSlot(entry.getIntValue(), entry.getKey());
            }
        });
        return clientContainer;
    }
}
