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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowProperty;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class FurnaceClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Input slot
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Fuel slot
            FLAG_DISABLE_SHIFT_INSERTION, // Output slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);
    private static final int MAX_PROGRESS_VALUE = 1000;

    public FurnaceClientContainer(Text title) {
        super(title);
    }

    @Override
    public <T> void bindPropertySupplier(ContainerProperty<T> propertyType, Supplier<T> supplier) {
        super.bindPropertySupplier(propertyType, supplier);
        if (propertyType == ContainerProperties.SMELT_PROGRESS) {
            bindInternalProperty(2, () -> MAX_PROGRESS_VALUE - (int) (((Double) supplier.get()) * (double) MAX_PROGRESS_VALUE));
        } else if (propertyType == ContainerProperties.FUEL_PROGRESS) {
            bindInternalProperty(0, () -> (int) (((Double) supplier.get()) * (double) MAX_PROGRESS_VALUE));
        }
    }

    @Override
    protected void collectInitMessages(List<Message> messages) {
        final int containerId = getContainerId();
        messages.add(new MessagePlayOutWindowProperty(containerId, 1, MAX_PROGRESS_VALUE));
        messages.add(new MessagePlayOutWindowProperty(containerId, 3, MAX_PROGRESS_VALUE));
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), MessagePlayOutOpenWindow.WindowType.FURNACE,
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
    protected boolean disableShiftClickWhenFull() {
        return false;
    }
}
