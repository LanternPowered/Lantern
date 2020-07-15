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
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.inventory.vanilla.VanillaInventoryConstants.CHEST_COLUMNS;
import static org.lanternpowered.server.inventory.vanilla.VanillaInventoryConstants.MAX_CHEST_ROWS;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.OpenWindowMessage;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

public class ChestClientContainer extends ClientContainer {

    private static final int[][] TOP_SLOT_FLAGS = new int[7][];
    private static final int[][] ALL_SLOT_FLAGS = new int[7][];

    static {
        for (int i = 0; i < TOP_SLOT_FLAGS.length; i++) {
            final int[] flags = new int[i * CHEST_COLUMNS];
            Arrays.fill(flags, FLAG_REVERSE_SHIFT_INSERTION);
            TOP_SLOT_FLAGS[i] = flags;
            ALL_SLOT_FLAGS[i] = compileAllSlotFlags(flags);
        }
    }

    private final int rowIndex;

    static class Title {
        static final Text DEFAULT = t("container.chest");
    }

    public ChestClientContainer(int rows) {
        this(Title.DEFAULT, rows);
    }

    ChestClientContainer(Text title, int rows) {
        super(title);
        checkArgument(rows >= 0 && rows <= MAX_CHEST_ROWS, "invalid rows count %s", rows);
        this.rowIndex = rows;
    }

    @Override
    protected Message createInitMessage() {
        final ClientWindowType windowType = ClientWindowTypes.INSTANCE.get(ResourceKey.minecraft("generic_9x" + this.rowIndex));
        checkState(windowType != null, "Window type for %s rows is currently not supported."); // TODO
        return new OpenWindowMessage(getContainerId(), windowType, getTitle());
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
