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

import org.lanternpowered.server.inventory.behavior.event.AnvilRenameEvent;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.OpenWindowMessage;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class AnvilClientContainer extends ClientContainer {

    private static final int[] TOP_SLOT_FLAGS = new int[] {
            0, // First input slot
            0, // Second input slot
            FLAG_REVERSE_SHIFT_INSERTION | FLAG_DISABLE_SHIFT_INSERTION | FLAG_IGNORE_DOUBLE_CLICK, // Result slot
    };
    private static final int[] ALL_SLOT_FLAGS = compileAllSlotFlags(TOP_SLOT_FLAGS);

    static class Title {
        static final Text DEFAULT = t("container.repair");
    }

    public AnvilClientContainer() {
        super(Title.DEFAULT);
    }

    @Override
    protected Message createInitMessage() {
        return new OpenWindowMessage(getContainerId(), ClientWindowTypes.ANVIL, getTitle());
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

    @Override
    public <T> void bindPropertySupplier(ContainerProperty<T> propertyType, Supplier<T> supplier) {
        super.bindPropertySupplier(propertyType, supplier);
        if (propertyType == ContainerProperties.REPAIR_COST) {
            final Supplier<Integer> supplier1 = (Supplier<Integer>) supplier;
            bindInternalProperty(0, supplier1::get);
        }
    }

    public void handleRename(String name) {
        // Force the output slot to update
        queueSilentSlotChange(this.slots[2]);
        tryProcessBehavior(behavior -> behavior.handleEvent(this, new AnvilRenameEvent(name)));
    }
}
