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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.block.entity.IBlockEntityInventory;
import org.lanternpowered.server.inventory.type.slot.LanternFilteringSlot;
import org.spongepowered.api.block.entity.Jukebox;

public class JukeboxInventory extends LanternFilteringSlot implements IBlockEntityInventory {

    @Override
    protected void queueUpdate() {
        super.queueUpdate();
        // Stop the record if it's already playing,
        // don't eject the current one, who's interacting
        // with the inventory should handle that
        getCarrierAs(Jukebox.class).ifPresent(Jukebox::stop);
    }
}
