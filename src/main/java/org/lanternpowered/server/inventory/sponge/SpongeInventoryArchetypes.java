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
package org.lanternpowered.server.inventory.sponge;

import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.type.LanternGridInventory;
import org.lanternpowered.server.inventory.type.LanternInventoryColumn;
import org.lanternpowered.server.inventory.type.LanternInventoryRow;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;

public final class SpongeInventoryArchetypes {

    ////////////////
    /// Menu Row ///
    ////////////////

    public static final LanternInventoryArchetype<LanternInventoryRow> MENU_ROW;

    ///////////////////
    /// Menu Column ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternInventoryColumn> MENU_COLUMN;

    ///////////////////
    /// Menu Button ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternSlot> MENU_BUTTON;


    ///////////////////
    /// Menu Column ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternGridInventory> MENU_GRID;

    static {
        final AbstractChildrenInventory.Builder<?> builder = AbstractChildrenInventory.builder();
        for (int x = 0; x < 9; x++) {
            builder.addLast(VanillaInventoryArchetypes.SLOT);
        }
        MENU_ROW = builder
                .type(LanternInventoryRow.class)
                .buildArchetype(InternalPluginsInfo.SpongePlatform.IDENTIFIER, "menu_row");
        MENU_COLUMN = builder
                .type(LanternInventoryColumn.class)
                .buildArchetype(InternalPluginsInfo.SpongePlatform.IDENTIFIER, "menu_column");
        MENU_BUTTON = AbstractSlot.builder()
                .type(LanternSlot.class)
                .buildArchetype(InternalPluginsInfo.SpongePlatform.IDENTIFIER, "menu_button");
        MENU_GRID = AbstractGridInventory.rowsBuilder()
                .row(0, MENU_ROW)
                .row(1, MENU_ROW)
                .row(2, MENU_ROW)
                .type(LanternGridInventory.class)
                .buildArchetype(InternalPluginsInfo.SpongePlatform.IDENTIFIER, "menu_grid");
    }

    private SpongeInventoryArchetypes() {
    }
}
