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
package org.lanternpowered.server.command

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.first
import org.lanternpowered.api.cause.get
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.Locatable
import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.command.CommandCause
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.world.Location
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.math.vector.Vector3d
import java.util.Optional

object LanternCommandCauseFactory : CommandCause.Factory {
    override fun create(): CommandCause = LanternCommandCause(CauseStack.currentCause)
}

private class LanternCommandCause(private val cause: Cause) : CommandCause {

    override fun getSubject(): Subject =
            this.cause[CauseContextKeys.SUBJECT] ?: this.cause.first() ?: Lantern.systemSubject

    override fun getAudience(): Audience =
            this.cause[CauseContextKeys.AUDIENCE] ?: this.cause.first() ?: Lantern.systemSubject

    override fun getLocation(): Optional<Location> {
        var location = this.cause[CauseContextKeys.LOCATION]
        if (location != null)
            return location.asOptional()
        location = this.targetBlock.orNull()?.location?.orNull()
        if (location != null)
            return location.asOptional()
        return this.cause.first<Locatable>()?.serverLocation.asOptional()
    }

    override fun getRotation(): Optional<Vector3d> {
        val rotation = this.cause[CauseContextKeys.ROTATION]
        if (rotation != null)
            return rotation.asOptional()
        return this.cause.first<Entity>()?.rotation.asOptional()
    }

    override fun getTargetBlock(): Optional<BlockSnapshot> {
        val target = this.cause[CauseContextKeys.BLOCK_TARGET]
        if (target != null)
            return target.asOptional()
        return this.cause.first<BlockSnapshot>().asOptional()
    }

    override fun sendMessage(message: Text) {
        this.audience.sendMessage(message)
    }

    override fun getCause() = this.cause
}
