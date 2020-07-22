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

typealias LifecycleEvent = org.spongepowered.api.event.lifecycle.LifecycleEvent
typealias ConstructPluginEvent = org.spongepowered.api.event.lifecycle.ConstructPluginEvent

typealias ServerLifecycleEvent = org.spongepowered.api.event.lifecycle.EngineLifecycleEvent<Server>
typealias StartingServerEvent = org.spongepowered.api.event.lifecycle.StartingEngineEvent<Server>
typealias StartedServerEvent = org.spongepowered.api.event.lifecycle.StartedEngineEvent<Server>
typealias StoppingServerEvent = org.spongepowered.api.event.lifecycle.StoppingEngineEvent<Server>

typealias RegisterWorldEvent = org.spongepowered.api.event.lifecycle.RegisterWorldEvent
typealias ProvideServiceEvent<T> = org.spongepowered.api.event.lifecycle.ProvideServiceEvent<T>
