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

import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowPropertyPacket;
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

    static class Title {
        static final Text DEFAULT = t("container.furnace");
    }

    public FurnaceClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    public <T> void bindPropertySupplier(ContainerProperty<T> propertyType, Supplier<T> supplier) {
        super.bindPropertySupplier(propertyType, supplier);
        if (propertyType == ContainerProperties.SMELT_PROGRESS) {
            final Supplier<Double> supplier1 = (Supplier<Double>) supplier;
            bindInternalProperty(2, () -> (int) (supplier1.get() * (double) MAX_PROGRESS_VALUE));
        } else if (propertyType == ContainerProperties.FUEL_PROGRESS) {
            final Supplier<Double> supplier1 = (Supplier<Double>) supplier;
            bindInternalProperty(0, () -> (int) (supplier1.get() * (double) MAX_PROGRESS_VALUE));
        }
    }

    @Override
    protected void collectInitMessages(List<Packet> packets) {
        final int containerId = getContainerId();
        packets.add(new SetWindowPropertyPacket(containerId, 1, MAX_PROGRESS_VALUE));
        packets.add(new SetWindowPropertyPacket(containerId, 3, MAX_PROGRESS_VALUE));
    }

    @Override
    protected Packet createInitMessage() {
        return new OpenWindowPacket(getContainerId(), ClientWindowTypes.FURNACE, getTitle());
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
    protected int getShiftFlags() {
        return SHIFT_CLICK_WHEN_FULL_TOP_AND_FILTER;
    }
}
