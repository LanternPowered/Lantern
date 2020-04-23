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

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.OpenWindowMessage;
import org.spongepowered.api.text.Text;

public class HopperClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_REVERSE_SHIFT_INSERTION, // Slot 1
            FLAG_REVERSE_SHIFT_INSERTION, // Slot 2
            FLAG_REVERSE_SHIFT_INSERTION, // Slot 3
            FLAG_REVERSE_SHIFT_INSERTION, // Slot 4
            FLAG_REVERSE_SHIFT_INSERTION, // Slot 5
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    static class Title {
        static final Text DEFAULT = t("container.hopper");
    }

    public HopperClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    protected Message createInitMessage() {
        return new OpenWindowMessage(getContainerId(), ClientWindowTypes.HOPPER, getTitle());
    }

    @Override
    protected int[] getTopSlotFlags() {
        return TOP_SLOT_FLAGS;
    }

    @Override
    protected int[] getSlotFlags() {
        return ALL_SLOT_FLAGS;
    }
}
