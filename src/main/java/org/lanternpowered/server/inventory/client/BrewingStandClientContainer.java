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

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.text.Text;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class BrewingStandClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | FLAG_ONE_ITEM, // Bottle slot 1
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | FLAG_ONE_ITEM, // Bottle slot 2
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | FLAG_ONE_ITEM, // Bottle slot 3
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Potion ingredient slot
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Blaze powder slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    static class Title {
        static final Text DEFAULT = t("container.brewing");
    }

    public BrewingStandClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    public <T> void bindPropertySupplier(ContainerProperty<T> propertyType, Supplier<T> supplier) {
        super.bindPropertySupplier(propertyType, supplier);
        if (propertyType == ContainerProperties.BREW_PROGRESS) {
            final Supplier<Double> supplier1 = (Supplier<Double>) supplier;
            bindInternalProperty(0, () -> (int) (supplier1.get() * 400.0));
        } else if (propertyType == ContainerProperties.FUEL_PROGRESS) {
            final Supplier<Double> supplier1 = (Supplier<Double>) supplier;
            bindInternalProperty(1, () -> (int) (supplier1.get() * 20.0));
        }
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), MessagePlayOutOpenWindow.WindowType.BREWING_STAND,
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
}
