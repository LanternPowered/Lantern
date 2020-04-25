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
package org.lanternpowered.server.util

import org.objectweb.asm.ClassReader

class DefineableClassLoader : ClassLoader {

    /**
     * Constructs a new [ClassLoader] with the given [parent].
     */
    constructor(parent: ClassLoader) : super(parent)

    /**
     * Constructs a new [ClassLoader].
     */
    constructor() : super(Thread.currentThread().contextClassLoader)

    /**
     * Defines a new [Class] for the given [ByteArray].
     */
    fun <T> defineClass(b: ByteArray): Class<T> {
        val reader = ClassReader(b)
        val name = reader.className.replace('/', '.')
        @Suppress("UNCHECKED_CAST")
        return defineClass(name, b, 0, b.size) as Class<T>
    }
}
