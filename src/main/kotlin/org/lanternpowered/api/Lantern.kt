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
package org.lanternpowered.api

import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.service.ServiceManager
import org.lanternpowered.api.x.cause.XCauseStackManager
import org.spongepowered.api.scheduler.Scheduler

object Lantern {

    @JvmStatic inline val server: Server get() = Sponge.getServer()
    @JvmStatic inline val game: Game get() = Sponge.getGame()
    @JvmStatic inline val registry: GameRegistry get() = Sponge.getRegistry() as GameRegistry
    @JvmStatic inline val causeStackManager: XCauseStackManager get() = Sponge.getCauseStackManager() as XCauseStackManager
    @JvmStatic inline val pluginManager: PluginManager get() = Sponge.getPluginManager()
    @JvmStatic inline val eventManager: EventManager get() = Sponge.getEventManager()
    @JvmStatic inline val syncScheduler: Scheduler get() = Sponge.getServer().scheduler
    @JvmStatic inline val asyncScheduler: Scheduler get() = Sponge.getGame().asyncScheduler
    @JvmStatic inline val serviceManager: ServiceManager get() = Sponge.getServiceManager() as ServiceManager
}
