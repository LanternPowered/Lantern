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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.property.SmeltingProgress;
import org.lanternpowered.server.inventory.property.SmeltingProgressProperty;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowProperty;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class FurnaceClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Input slot
            FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Fuel slot
            FLAG_DISABLE_SHIFT_INSERTION, // Output slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    private Supplier<SmeltingProgressProperty> smeltingProgressPropertySupplier = () -> null;
    @Nullable private SmeltingProgress lastProgress;

    public FurnaceClientContainer(Text title) {
        super(title);
    }

    @Override
    public <T extends InventoryProperty<?,?>> void bindProperty(Class<T> propertyType, Supplier<T> supplier) {
        super.bindProperty(propertyType, supplier);
        if (propertyType == SmeltingProgressProperty.class) {
            this.smeltingProgressPropertySupplier = (Supplier<SmeltingProgressProperty>) supplier;
        }
    }

    @Override
    protected void collectPropertyChanges(List<Message> messages) {
        final SmeltingProgressProperty property = this.smeltingProgressPropertySupplier.get();
        final int containerId = getContainerId();
        if (!Objects.equals(property == null ? null : property.getValue(), this.lastProgress)) {
            if (property != null) {
                this.lastProgress = checkNotNull(property.getValue());
                messages.add(new MessagePlayOutWindowProperty(containerId, 0,
                        this.lastProgress.getMaxBurnTime() - this.lastProgress.getElapsedBurnTime()));
                messages.add(new MessagePlayOutWindowProperty(containerId, 1,
                        this.lastProgress.getMaxBurnTime()));
                messages.add(new MessagePlayOutWindowProperty(containerId, 2,
                        this.lastProgress.getElapsedSmeltTime()));
                messages.add(new MessagePlayOutWindowProperty(containerId, 3,
                        this.lastProgress.getMaxSmeltTime()));
            } else {
                this.lastProgress = null;
                messages.add(new MessagePlayOutWindowProperty(containerId, 0, 0));
                messages.add(new MessagePlayOutWindowProperty(containerId, 1, 1));
                messages.add(new MessagePlayOutWindowProperty(containerId, 2, 0));
                messages.add(new MessagePlayOutWindowProperty(containerId, 3, 1));
            }
        }
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
