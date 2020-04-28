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
package org.lanternpowered.server.inject.option

import com.google.inject.BindingAnnotation

/**
 * Represents a option that can be provided through the launch parameters. The
 * type of the field or parameter defines which type that is being parsed.
 *
 * @property value The keys of the option.
 * @property description The description of the option.
 */
@BindingAnnotation
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class Option(vararg val value: String, val description: String = "")
