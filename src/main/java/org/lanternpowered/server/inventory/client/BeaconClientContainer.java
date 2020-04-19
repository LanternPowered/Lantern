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

import org.lanternpowered.server.effect.potion.LanternPotionEffectType;
import org.lanternpowered.server.inventory.behavior.event.BeaconEffectsEvent;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public class BeaconClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION, // Payment slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    static class Title {
        static final Text DEFAULT = t("container.beacon");
    }

    public BeaconClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), ClientWindowTypes.BEACON, getTitle());
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

    @Override
    public <T> void bindPropertySupplier(ContainerProperty<T> propertyType, Supplier<T> supplier) {
        super.bindPropertySupplier(propertyType, supplier);
        int index = -1;
        if (propertyType == ContainerProperties.PRIMARY_POTION_EFFECT) {
            index = 0;
        } else if (propertyType == ContainerProperties.SECONDARY_POTION_EFFECT) {
            index = 1;
        }
        if (index != -1) {
            final Supplier<Optional<PotionEffectType>> supplier1 = (Supplier<Optional<PotionEffectType>>) supplier;
            bindInternalProperty(index, () -> supplier1.get()
                    .map(type -> ((LanternPotionEffectType) type).getInternalId()).orElse(-1));
        }
    }

    public void handleEffects(@Nullable PotionEffectType primaryEffect, @Nullable PotionEffectType secondaryEffect) {
        // Force the input item to update
        queueSilentSlotChange(this.slots[0]);
        tryProcessBehavior(behavior -> behavior.handleEvent(this, new BeaconEffectsEvent(primaryEffect, secondaryEffect)));
    }
}
