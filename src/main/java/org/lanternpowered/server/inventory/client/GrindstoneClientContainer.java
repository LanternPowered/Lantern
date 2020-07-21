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
package org.lanternpowered.server.inventory.client;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.network.message.Packet;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket;
import org.spongepowered.api.text.Text;

import java.util.List;

@SuppressWarnings("unchecked")
public class GrindstoneClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            0, // First input slot
            0, // Second input slot
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION | FLAG_IGNORE_DOUBLE_CLICK, // Result slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    static class Title {
        static final Text DEFAULT = t("container.grindstone_title");
    }

    public GrindstoneClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    protected Packet createInitMessage() {
        return new OpenWindowPacket(getContainerId(), ClientWindowTypes.GRINDSTONE, getTitle());
    }

    @Override
    protected int[] getTopSlotFlags() {
        return TOP_SLOT_FLAGS;
    }

    @Override
    protected int[] getSlotFlags() {
        return ALL_SLOT_FLAGS;
    }

    @Override
    protected void collectChangeMessages(List<Packet> packets) {
        if ((this.slots[0].dirtyState & BaseClientSlot.IS_DIRTY) != 0 ||
                (this.slots[1].dirtyState & BaseClientSlot.IS_DIRTY) != 0) {
            // Force update the result slot if one of the inputs is modified
            // queueSilentSlotChangeSafely(this.slots[2]);
        }
        super.collectChangeMessages(packets);
    }
}
