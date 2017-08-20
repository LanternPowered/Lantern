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

public class ChestClientContainer extends ClientContainer {

    private static final int[][] TOP_SLOT_FLAGS = new int[7][];
    private static final int[][] ALL_SLOT_FLAGS = new int[7][];

    static {
        for (int i = 0; i < TOP_SLOT_FLAGS.length; i++) {
            final int[] flags = new int[i * 9];
            Arrays.fill(flags, FLAG_REVERSE_SHIFT_INSERTION);
            TOP_SLOT_FLAGS[i] = flags;
            ALL_SLOT_FLAGS[i] = compileAllSlotFlags(flags);
        }
    }

    private final int rowIndex;

    public ChestClientContainer(Text title, int rows) {
        super(title);
        checkArgument(rows >= 0 && rows <= 6);
        this.rowIndex = rows;
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), MessagePlayOutOpenWindow.WindowType.CONTAINER,
                getTitle(), getTopSlotFlags().length, 0);
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
