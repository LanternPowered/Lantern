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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

import org.checkerframework.checker.nullness.qual.Nullable;

public class MessagePlayOutEntityEquipment implements Message {

    private final int entityId;
    private final int slotIndex;
    @Nullable private final Object itemStack;

    public MessagePlayOutEntityEquipment(int entityId, int slotIndex, @Nullable Object itemStack) {
        this.entityId = entityId;
        this.slotIndex = slotIndex;
        this.itemStack = itemStack;
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Nullable
    public Object getItem() {
        return this.itemStack;
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }
}
