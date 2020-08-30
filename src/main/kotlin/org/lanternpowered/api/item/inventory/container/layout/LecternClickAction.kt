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
package org.lanternpowered.api.item.inventory.container.layout

/**
 * Represents the cause type of a page change caused by the client.
 */
sealed class LecternClickAction {

    /**
     * Is used when a player clicks on a text component with
     * a "change page" click action.
     */
    class ToPage(val page: Int) : LecternClickAction()

    /**
     * Is used when a player clicks on the next button in the lectern.
     */
    object NextPage : LecternClickAction()

    /**
     * Is used when a player clicks on the previous button in the lectern.
     */
    object PreviousPage : LecternClickAction()

    /**
     * Is used when a player clicks on the pickup button.
     */
    object Pickup : LecternClickAction()
}
