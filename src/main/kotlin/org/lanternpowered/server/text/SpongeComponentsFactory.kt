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
package org.lanternpowered.server.text

import net.kyori.adventure.text.event.ClickEvent
import org.spongepowered.api.adventure.SpongeComponents
import org.spongepowered.api.command.CommandCause
import java.util.function.Consumer

object SpongeComponentsFactory : SpongeComponents.Factory {

    override fun callbackClickEvent(callback: Consumer<CommandCause>): ClickEvent =
            ClickEvent.runCommand(ClickActionCallbacks.getOrCreateCallbackCommand(callback))
}
