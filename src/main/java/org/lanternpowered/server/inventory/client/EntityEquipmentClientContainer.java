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
package org.lanternpowered.server.inventory.client;

import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.text.Text;

import java.util.Arrays;

// Llama
// Horse
// Donkey
// Mule
public class EntityEquipmentClientContainer extends ClientContainer {

    private static final int[][] SLOT_FLAGS = new int[6][];
    private static final int[][] ALL_SLOT_FLAGS = new int[6][];

    static {
        for (int i = 0; i < SLOT_FLAGS.length; i++) {
            final int[] flags = new int[2 + (i * 3)];
            Arrays.fill(flags, FLAG_REVERSE_SHIFT_INSERTION);
            flags[0] |= FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION;
            flags[1] |= FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION;
            SLOT_FLAGS[i] = flags;
            ALL_SLOT_FLAGS[i] = compileAllSlotFlags(flags);
        }
    }

    private final int rowIndex;
    private final int entityId;

    public EntityEquipmentClientContainer(Text title, int chestRows, int entityId) {
        super(title);
        checkArgument(chestRows >= 0 && chestRows <= 5);
        this.rowIndex = chestRows;
        this.entityId = entityId;
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), MessagePlayOutOpenWindow.WindowType.HORSE,
                getTitle(), SLOT_FLAGS.length, this.entityId);
    }

    @Override
    protected int[] getSlotFlags() {
        return SLOT_FLAGS[this.rowIndex];
    }

    @Override
    protected int[] getAllSlotFlags() {
        return ALL_SLOT_FLAGS[this.rowIndex];
    }
}
