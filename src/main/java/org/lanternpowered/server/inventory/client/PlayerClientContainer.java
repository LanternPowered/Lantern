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

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

public class PlayerClientContainer extends ClientContainer {

    private static final int[] SLOT_FLAGS = new int[] {
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION, // Crafting output slot
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 1
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 2
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 3
            FLAG_DISABLE_SHIFT_INSERTION, // Crafting input slot 4
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 1
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 2
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 3
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Equipment slot 4
            FLAG_DISABLE_SHIFT_INSERTION, // Offhand slot
    };
    private static final int[] ALL_SLOT_FLAGS;

    static {
        ALL_SLOT_FLAGS = new int[SLOT_FLAGS.length + MAIN_INVENTORY_FLAGS.length];
        System.arraycopy(SLOT_FLAGS, 0, ALL_SLOT_FLAGS, 0, SLOT_FLAGS.length - 1);
        System.arraycopy(MAIN_INVENTORY_FLAGS, 0, ALL_SLOT_FLAGS, SLOT_FLAGS.length - 1, MAIN_INVENTORY_FLAGS.length);
        // The offhand slot uses the last index
        ALL_SLOT_FLAGS[ALL_SLOT_FLAGS.length - 1] = SLOT_FLAGS[SLOT_FLAGS.length - 1];
    }

    public PlayerClientContainer(Text title) {
        super(title);
    }

    @Override
    protected int[] getSlotFlags() {
        return SLOT_FLAGS;
    }

    @Override
    protected int[] getAllSlotFlags() {
        return ALL_SLOT_FLAGS;
    }

    @Override
    protected int generateContainerId() {
        // A player container id is always 0
        return 0;
    }

    @Override
    protected Message createInitMessage() {
        return null;
    }
}
