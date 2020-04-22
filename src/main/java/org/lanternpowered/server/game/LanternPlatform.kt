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
package org.lanternpowered.server.game

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.game.version.LanternMinecraftVersion
import org.spongepowered.api.Platform
import org.spongepowered.api.plugin.PluginContainer
import java.util.HashMap

class LanternPlatform(
        private val containers: Map<Platform.Component, PluginContainer>
) : Platform {

    private val platformMap: MutableMap<String, Any> = object : HashMap<String, Any>() {
        override fun put(key: String, value: Any): Any? {
            check(!containsKey(key)) { "Cannot set the value of the existing key $key" }
            return super.put(key, value)
        }
    }

    init {
        val apiContainer = getContainer(Platform.Component.API)
        val implContainer = getContainer(Platform.Component.IMPLEMENTATION)
        this.platformMap["Type"] = this.type
        this.platformMap["ApiName"] = apiContainer.name
        this.platformMap["ApiVersion"] = apiContainer.version
        this.platformMap["ImplementationName"] = implContainer.name
        this.platformMap["ImplementationVersion"] = implContainer.version
        this.platformMap["MinecraftVersion"] = this.minecraftVersion
    }

    override fun getType(): Platform.Type = Platform.Type.SERVER
    override fun getExecutionType(): Platform.Type = Platform.Type.SERVER
    override fun asMap(): Map<String, Any> = this.platformMap
    override fun getMinecraftVersion(): LanternMinecraftVersion = LanternMinecraftVersion.CURRENT

    override fun getContainer(component: Platform.Component): PluginContainer =
            this.containers[Platform.Component.API] ?: error("No ${component.name.toLowerCase()} container.")

    override fun toString(): String = ToStringHelper(this)
            .add("type", this.type)
            .add("executionType", this.executionType)
            .add("version", getContainer(Platform.Component.IMPLEMENTATION).version)
            .add("apiVersion", getContainer(Platform.Component.API).version)
            .add("minecraftVersion", this.minecraftVersion)
            .toString()
}
