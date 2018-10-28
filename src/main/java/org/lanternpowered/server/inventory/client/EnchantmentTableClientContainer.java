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

import org.lanternpowered.server.inventory.behavior.event.EnchantButtonEvent;
import org.lanternpowered.server.item.enchantment.LanternEnchantmentType;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class EnchantmentTableClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION | FLAG_ONE_ITEM, // Input slot
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION, // Lapis lazuli slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    static class Title {
        static final Text DEFAULT = t("container.enchant");
    }

    public EnchantmentTableClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    public <T> void bindPropertySupplier(ContainerProperty<T> propertyType, Supplier<T> supplier) {
        super.bindPropertySupplier(propertyType, supplier);
        // First try to bind integer properties
        int index = -1;
        if (propertyType == ContainerProperties.REQUIRED_EXPERIENCE_LEVEL_1) {
            index = 0;
        } else if (propertyType == ContainerProperties.REQUIRED_EXPERIENCE_LEVEL_2) {
            index = 1;
        } else if (propertyType == ContainerProperties.REQUIRED_EXPERIENCE_LEVEL_3) {
            index = 2;
        } else if (propertyType == ContainerProperties.ENCHANTMENT_SEED) {
            index = 3;
        } else if (propertyType == ContainerProperties.SHOWN_ENCHANTMENT_LEVEL_1) {
            index = 7;
        } else if (propertyType == ContainerProperties.SHOWN_ENCHANTMENT_LEVEL_2) {
            index = 8;
        } else if (propertyType == ContainerProperties.SHOWN_ENCHANTMENT_LEVEL_3) {
            index = 9;
        }
        if (index != -1) {
            final Supplier<Integer> supplier1 = (Supplier<Integer>) supplier;
            bindInternalProperty(index, supplier1::get);
        } else {
            // Enchantment type properties
            if (propertyType == ContainerProperties.SHOWN_ENCHANTMENT_1) {
                index = 4;
            } else if (propertyType == ContainerProperties.SHOWN_ENCHANTMENT_2) {
                index = 5;
            } else if (propertyType == ContainerProperties.SHOWN_ENCHANTMENT_3) {
                index = 6;
            }
            if (index != -1) {
                final Supplier<Optional<EnchantmentType>> supplier1 = (Supplier<Optional<EnchantmentType>>) supplier;
                bindInternalProperty(index, () -> supplier1.get()
                        .map(enchantment -> ((LanternEnchantmentType) enchantment).getInternalId()).orElse(-1));
            }
        }
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), ClientWindowTypes.ENCHANTMENT, getTitle());
    }

    @Override
    protected int[] getTopSlotFlags() {
        return TOP_SLOT_FLAGS;
    }

    @Override
    protected int[] getSlotFlags() {
        return ALL_SLOT_FLAGS;
    }

    public void handleButton(int index) {
        tryProcessBehavior(behavior -> behavior.handleEvent(this, new EnchantButtonEvent(index)));
    }
}
