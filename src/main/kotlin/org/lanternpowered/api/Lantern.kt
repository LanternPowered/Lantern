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

import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.service.ServiceProvider
import org.lanternpowered.api.world.WorldManager
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.command.manager.CommandManager
import org.spongepowered.api.scheduler.Scheduler

object Lantern {

    @JvmStatic inline val server: Server get() = Sponge.getServer() as Server
    @JvmStatic inline val game: Game get() = Sponge.getGame() as Game
    @JvmStatic inline val registry: GameRegistry get() = Sponge.getRegistry() as GameRegistry
    @JvmStatic inline val causeStackManager: CauseStackManager get() = Sponge.getServer().causeStackManager as CauseStackManager
    @JvmStatic inline val pluginManager: PluginManager get() = Sponge.getPluginManager() as PluginManager
    @JvmStatic inline val eventManager: EventManager get() = Sponge.getEventManager() as EventManager
    @JvmStatic inline val syncScheduler: Scheduler get() = Sponge.getServer().scheduler
    @JvmStatic inline val asyncScheduler: Scheduler get() = Sponge.getGame().asyncScheduler
    @JvmStatic inline val serviceProvider: ServiceProvider get() = Sponge.getServiceProvider() as ServiceProvider
    @JvmStatic inline val worldManager: WorldManager get() = this.server.worldManager
    @JvmStatic inline val commandManager: CommandManager get() = this.game.commandManager
    @JvmStatic inline val systemSubject: SystemSubject get() = this.game.systemSubject
}
