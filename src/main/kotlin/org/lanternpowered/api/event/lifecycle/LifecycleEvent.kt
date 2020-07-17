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
package org.lanternpowered.api.event.lifecycle

import org.lanternpowered.api.Server
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent
import org.spongepowered.api.event.lifecycle.EngineLifecycleEvent
import org.spongepowered.api.event.lifecycle.LifecycleEvent
import org.spongepowered.api.event.lifecycle.StartedEngineEvent
import org.spongepowered.api.event.lifecycle.StartingEngineEvent
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent

typealias LifecycleEvent = LifecycleEvent
typealias ConstructPluginEvent = ConstructPluginEvent

typealias ServerLifecycleEvent = EngineLifecycleEvent<Server>
typealias StartingServerEvent = StartingEngineEvent<Server>
typealias StartedServerEvent = StartedEngineEvent<Server>
typealias StoppingServerEvent = StoppingEngineEvent<Server>
