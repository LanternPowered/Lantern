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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

public final class MessagePlayOutOpenWindow implements Message {

    private final int windowId;
    private final WindowType windowType;
    private final Text title;
    private final int slotCount;
    private final int entityId;

    public MessagePlayOutOpenWindow(int windowId, WindowType windowType, Text title, int slotCount, int entityId) {
        this.windowType = windowType;
        this.slotCount = slotCount;
        this.windowId = windowId;
        this.entityId = entityId;
        this.title = title;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public WindowType getWindowType() {
        return this.windowType;
    }

    public Text getTitle() {
        return this.title;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getSlotCount() {
        return this.slotCount;
    }

    public enum WindowType {
        CONTAINER,
        CHEST,
        CRAFTING_TABLE,
        FURNACE,
        DISPENSER,
        ENCHANTING_TABLE,
        BREWING_STAND,
        VILLAGER,
        BEACON,
        ANVIL,
        HOPPER,
        DROPPER,
        HORSE,
        SHULKER_BOX,
        ;
    }
}
