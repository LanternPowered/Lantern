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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.logger

import kotlin.reflect.KClass

typealias Logger = org.apache.logging.log4j.Logger
typealias LoggerFactory = org.apache.logging.log4j.LogManager
typealias Marker = org.apache.logging.log4j.Marker

/**
 * Constructs a new [Logger] for the target class.
 *
 * @param clazz The target class
 * @return The constructed logger
 */
inline fun <T : Any> loggerOf(clazz: KClass<T>): Logger = LoggerFactory.getLogger(clazz.java)

/**
 * Constructs a new [Logger] with the specified name.
 *
 * @param name The name
 * @return The constructed logger
 */
inline fun loggerOf(name: String): Logger = LoggerFactory.getLogger(name)