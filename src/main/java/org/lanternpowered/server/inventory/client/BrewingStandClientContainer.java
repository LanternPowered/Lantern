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
        return new MessagePlayOutOpenWindow(getContainerId(), ClientWindowTypes.BREWING_STAND, getTitle());
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
