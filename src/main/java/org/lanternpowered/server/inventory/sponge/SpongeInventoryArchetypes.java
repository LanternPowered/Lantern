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
package org.lanternpowered.server.inventory.sponge;

import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.type.LanternGridInventory;
import org.lanternpowered.server.inventory.type.LanternInventoryColumn;
import org.lanternpowered.server.inventory.type.LanternInventoryRow;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.plugin.InternalPluginsInfo;

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
        final AbstractOrderedInventory.Builder<?> builder = AbstractOrderedInventory.builder();
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
