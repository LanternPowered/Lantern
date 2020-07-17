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
@file:Suppress("UNUSED_PARAMETER")

package org.lanternpowered.api.plugin

import org.lanternpowered.api.Lantern

typealias Plugin = org.spongepowered.plugin.jvm.Plugin
typealias PluginContainer = org.spongepowered.plugin.PluginContainer

val PluginContainer.id: String
    get() = metadata.id

val PluginContainer.name: String
    get() = metadata.name.orElse(metadata.id)

val PluginContainer.version: String
    get() = metadata.version

interface PluginManager : org.spongepowered.api.plugin.PluginManager {

    companion object : PluginManager by Lantern.pluginManager
}
