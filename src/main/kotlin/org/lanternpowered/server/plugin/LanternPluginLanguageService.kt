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
package org.lanternpowered.server.plugin

import org.spongepowered.plugin.jvm.JVMPluginLanguageService

class LanternPluginLanguageService : JVMPluginLanguageService() {
    override fun getName(): String = "lantern"
    override fun getPluginLoader(): String = LanternPluginLoader::class.java.name
}
