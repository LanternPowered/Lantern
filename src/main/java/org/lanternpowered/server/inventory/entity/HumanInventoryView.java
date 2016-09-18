/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.inventory.entity;

import org.spongepowered.api.item.inventory.Inventory;

/**
 * The different kind of {@link Inventory} views that can be
 * used for the {@link LanternPlayerInventory}. This mainly
 * modifies the insertion/poll order of item stacks. And the
 * which sub {@link Inventory}s are available.
 */
public enum HumanInventoryView {
    /**
     * The hotbar inventory view. Contains only the hotbar.
     */
    HOTBAR,
    /**
     * The main inventory view. Contains only the main inventory,
     * excludes the hotbar.
     */
    MAIN,
    /**
     * The main and hotbar inventory.
     */
    MAIN_AND_PRIORITY_HOTBAR,
    /**
     * The main and hotbar inventory, but the main inventory
     * has priority for offer/poll functions.
     */
    PRIORITY_MAIN_AND_HOTBAR,
    /**
     * The reverse order for the main and hotbar inventory. Starting
     * from the bottom right corner, then going left until the row
     * is finished and doing this for every row until the most
     * upper one is reached.
     */
    REVERSE_MAIN_AND_HOTBAR,
    /**
     * All the inventories but the main inventory has priority over
     * the hotbar.
     */
    ALL_PRIORITY_MAIN,
    /**
     * A view that uses the raw inventory slot indexes of vanilla
     * minecraft, this is mainly for networking only and this doesn't
     * contain the crafting grid.
     *
     * Inventory arrangement:
     * - Hotbar
     * - Main inventory
     * - Equipment inventory
     * - Off hand slot
     */
    RAW_INVENTORY,
}
