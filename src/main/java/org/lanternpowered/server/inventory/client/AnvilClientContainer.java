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

import org.lanternpowered.server.inventory.behavior.event.AnvilRenameEvent;
import org.lanternpowered.server.inventory.behavior.event.BeaconEffectsEvent;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.text.Text;

import java.util.List;

import javax.annotation.Nullable;

public class AnvilClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            0, // First input slot
            0, // Second input slot
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION | FLAG_IGNORE_DOUBLE_CLICK, // Result slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    public AnvilClientContainer(Text title) {
        super(title);
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), MessagePlayOutOpenWindow.WindowType.ANVIL,
                getTitle(), TOP_SLOT_FLAGS.length, 0);
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
    protected void collectChangeMessages(List<Message> messages) {
        if ((this.slots[0].dirtyState & BaseClientSlot.IS_DIRTY) != 0 ||
                (this.slots[1].dirtyState & BaseClientSlot.IS_DIRTY) != 0) {
            // Force update the result slot if one of the inputs is modified
            queueSilentSlotChangeSafely(this.slots[2]);
        }
        super.collectChangeMessages(messages);
    }

    public void handleRename(String name) {
        // Force the output slot to update
        queueSilentSlotChange(this.slots[2]);
        tryProcessBehavior(behavior -> behavior.handleEvent(this, new AnvilRenameEvent(name)));
    }
}
