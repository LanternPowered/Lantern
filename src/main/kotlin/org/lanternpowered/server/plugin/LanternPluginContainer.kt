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

import com.google.inject.Injector
import org.spongepowered.plugin.PluginCandidate
import org.spongepowered.plugin.jvm.JVMPluginContainer

class LanternPluginContainer(candidate: PluginCandidate) : JVMPluginContainer(candidate) {

    var injector: Injector? = null
}
