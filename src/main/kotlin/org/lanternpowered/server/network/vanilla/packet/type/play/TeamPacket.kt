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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.scoreboard.CollisionRule
import org.spongepowered.api.scoreboard.Visibility

sealed class TeamPacket : Packet {

    abstract val teamName: String

    data class Remove(override val teamName: String) : TeamPacket()

    data class Create(
            override val teamName: String,
            override val displayName: Text,
            override val prefix: Text,
            override val suffix: Text,
            override val nameTagVisibility: Visibility,
            override val collisionRule: CollisionRule,
            override val color: NamedTextColor?,
            override val friendlyFire: Boolean,
            override val seeFriendlyInvisibles: Boolean,
            override val members: List<Text>
    ) : CreateOrUpdate(), Members

    data class Update(
            override val teamName: String,
            override val displayName: Text,
            override val prefix: Text,
            override val suffix: Text,
            override val nameTagVisibility: Visibility,
            override val collisionRule: CollisionRule,
            override val color: NamedTextColor?,
            override val friendlyFire: Boolean,
            override val seeFriendlyInvisibles: Boolean
    ) : CreateOrUpdate()

    abstract class CreateOrUpdate : TeamPacket() {

        abstract val displayName: Text
        abstract val prefix: Text
        abstract val suffix: Text
        abstract val nameTagVisibility: Visibility
        abstract val collisionRule: CollisionRule
        abstract val color: NamedTextColor?
        abstract val friendlyFire: Boolean
        abstract val seeFriendlyInvisibles: Boolean
    }

    data class AddMembers(
            override val teamName: String,
            override val members: List<Text>
    ) : TeamPacket(), Members

    data class RemoveMembers(
            override val teamName: String,
            override val members: List<Text>
    ) : TeamPacket(), Members

    interface Members {

        val members: List<Text>
    }
}
