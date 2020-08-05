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
package org.lanternpowered.server.entity.player

import org.spongepowered.api.entity.living.player.User
import java.util.UUID

internal interface IUser : User {

    @JvmDefault
    override fun getName(): String = this.profile.name.get()

    @JvmDefault
    override fun getUniqueId(): UUID = this.profile.uniqueId

    @JvmDefault
    override fun getIdentifier(): String = this.profile.uniqueId.toString()
}
