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
package org.lanternpowered.api.behavior

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.inject.Named
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Represents a behavior type. For each type will a
 * behavior pipeline be available that can be invoked.
 *
 * All the [BehaviorType]s should be a kotlin object.
 */
abstract class BehaviorType {

    /**
     * The name of this behavior type
     */
    val name: String

    /**
     * Constructs a new [BehaviorType] with the given name.
     */
    constructor(name: String) {
        this.name = name
    }

    /**
     * Constructs a new [BehaviorType] with the name of
     * the class or [Named] annotation if present.
     */
    constructor() {
        @Suppress("LeakingThis") // Not leaking anything
        val clazz = this::class
        this.name = clazz.findAnnotation<Named>()?.value ?: clazz.simpleName ?: clazz.java.simpleName
    }
}

private val types = ConcurrentHashMap<Class<*>, Any>()

/**
 * Extracts the [BehaviorType] instance from the
 */
@JvmName("getType")
fun <T : BehaviorType> KClass<T>.getBehaviorType(): T {
    return types.computeIfAbsent(this::class.java) {
        val objInstance = this::objectInstance.get()
        if (objInstance != null) {
            objInstance
        } else {
            // Alternatively instantiate the behavior type by using a empty constructor
            val constructor = this.java.getDeclaredConstructor()
            constructor.isAccessible = true // In case it's not accessible
            constructor.newInstance()
        }
    }.uncheckedCast()
}
