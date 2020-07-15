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

import org.lanternpowered.api.cause.CauseStack
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.event.cause.Cause

object LanternCommandCauseFactory : CommandCause.Factory {
    override fun create(): CommandCause = LanternCommandCause(CauseStack.current().currentCause)
}

private class LanternCommandCause(private val cause: Cause) : CommandCause {
    override fun getCause() = this.cause
}
