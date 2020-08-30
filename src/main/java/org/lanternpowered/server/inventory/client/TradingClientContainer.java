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

import org.lanternpowered.server.inventory.behavior.event.SelectTradingOfferEvent;
import org.lanternpowered.server.inventory.container.ClientWindowTypes;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket;
import org.spongepowered.api.text.Text;

public class TradingClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_DISABLE_SHIFT_INSERTION, // Input slot 1
            FLAG_DISABLE_SHIFT_INSERTION, // Input slot 2
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION | FLAG_IGNORE_DOUBLE_CLICK, // Output slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    static class Title {
        static final Text DEFAULT = t("container.trading");
    }

    public TradingClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    protected Packet createInitMessage() {
        return new OpenWindowPacket(containerId, ClientWindowTypes.MERCHANT, getTitle());
    }

    @Override
    protected int[] getTopSlotFlags() {
        return TOP_SLOT_FLAGS;
    }

    @Override
    protected int[] getSlotFlags() {
        return ALL_SLOT_FLAGS;
    }

    public void handleSelectOffer(int index) {
        tryProcessBehavior(behavior -> behavior.handleEvent(this, new SelectTradingOfferEvent(index)));
    }
}
