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
package org.lanternpowered.server.inventory.user

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.util.optional.asOptional
import org.spongepowered.api.data.KeyValueMatcher
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.item.inventory.entity.PlayerInventory
import org.spongepowered.api.item.inventory.entity.StandardInventory
import org.spongepowered.api.item.inventory.entity.UserInventory
import java.util.Optional

class LanternUserInventory(
        private val playerInventory: PlayerInventory,
        private val carrier: User
) : StandardInventory by playerInventory, ExtendedInventory by playerInventory as ExtendedInventory, UserInventory {

    override fun getCarrier(): Optional<User> = this.carrier.asOptional()

    override fun query(matcher: KeyValueMatcher<*>): ExtendedInventory {
        TODO("Not yet implemented")
    }
}
