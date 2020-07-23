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
package org.lanternpowered.server.plugin.inject

import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.config.DefaultConfig

object ModuleAnnotations {

    @DefaultConfig(sharedRoot = false)
    private val nonSharedDefaultConfigField = Unit

    @DefaultConfig(sharedRoot = true)
    private val sharedDefaultConfigField = Unit

    @ConfigDir(sharedRoot = false)
    private val nonSharedConfigDirField = Unit

    @ConfigDir(sharedRoot = true)
    private val sharedConfigDirField = Unit

    private inline fun <reified A : Annotation> getFieldAnnotation(field: String): A =
            this::class.java.getDeclaredField(field).getAnnotation(A::class.java)

    val nonSharedDefaultConfig: DefaultConfig = getFieldAnnotation("nonSharedDefaultConfigField")
    val sharedDefaultConfig: DefaultConfig = getFieldAnnotation("sharedDefaultConfigField")

    val nonSharedConfigDir: ConfigDir = getFieldAnnotation("nonSharedConfigDirField")
    val sharedConfigDir: ConfigDir = getFieldAnnotation("sharedConfigDirField")
}
