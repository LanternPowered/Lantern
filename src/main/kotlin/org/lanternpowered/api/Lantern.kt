/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.api

import org.lanternpowered.api.entity.spawn.EntitySpawner
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.text.translation.TranslationRegistry
import org.lanternpowered.api.x.XGameRegistry
import org.lanternpowered.api.x.XServer
import org.lanternpowered.api.x.cause.XCauseStackManager
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.service.ServiceManager

object Lantern {

    @JvmStatic inline val server: XServer get() = Sponge.getServer() as XServer
    @JvmStatic inline val game: Game get() = Sponge.getGame()
    @JvmStatic inline val registry: XGameRegistry get() = Sponge.getRegistry() as XGameRegistry
    @JvmStatic inline val causeStackManager: XCauseStackManager get() = Sponge.getCauseStackManager() as XCauseStackManager
    @JvmStatic inline val pluginManager: PluginManager get() = Sponge.getPluginManager()
    @JvmStatic inline val eventManager: EventManager get() = Sponge.getEventManager()
    @JvmStatic inline val scheduler: Scheduler get() = Sponge.getScheduler()
    @JvmStatic inline val serviceManager: ServiceManager get() = Sponge.getServiceManager()
    @JvmStatic inline val entitySpawner: EntitySpawner get() = this.server.entitySpawner
    @JvmStatic inline val translationRegistry: TranslationRegistry get() = this.registry.translations
}
