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

import static com.google.common.base.Preconditions.checkArgument;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenHorseWindowPacket;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

// Llama
// Horse
// Donkey
// Mule
public class EntityEquipmentClientContainer extends ClientContainer {

    private static final int[][] TOP_SLOT_FLAGS = new int[6][];
    private static final int[][] ALL_SLOT_FLAGS = new int[6][];

    static {
        for (int i = 0; i < TOP_SLOT_FLAGS.length; i++) {
            final int[] flags = new int[2 + (i * 3)];
            Arrays.fill(flags, FLAG_REVERSE_SHIFT_INSERTION);
            flags[0] |= FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION;
            flags[1] |= FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION;
            TOP_SLOT_FLAGS[i] = flags;
            ALL_SLOT_FLAGS[i] = compileAllSlotFlags(flags);
        }
    }

    private final int rowIndex;
    private final int entityId;

    static class Title {
        static final Text DEFAULT = t("Entity Equipment");
    }

    public EntityEquipmentClientContainer(int chestRows, int entityId) {
        super(Title.DEFAULT);
        checkArgument(chestRows >= 0 && chestRows <= 5);
        this.rowIndex = chestRows;
        this.entityId = entityId;
    }

    @Override
    protected Packet createInitMessage() {
        return new OpenHorseWindowPacket(getContainerId(), TOP_SLOT_FLAGS.length, this.entityId);
    }

    @Override
    protected int[] getTopSlotFlags() {
        return TOP_SLOT_FLAGS[this.rowIndex];
    }

    @Override
    protected int[] getSlotFlags() {
        return ALL_SLOT_FLAGS[this.rowIndex];
    }
}
