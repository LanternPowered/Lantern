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
package org.lanternpowered.api.data

import java.util.Optional
import java.util.function.Supplier

infix fun <V> Supplier<V>.eq(value: V?): Boolean =
        this.get() == value

infix fun <V> Supplier<V>.neq(value: V?): Boolean =
        this.get() != value

infix fun <V> V?.eq(value: V?): Boolean =
        this == value

infix fun <V> V?.neq(value: V?): Boolean =
        this != value

infix fun <V> Supplier<V?>.eq(value: Supplier<out V?>): Boolean =
        this.get() == value.get()

infix fun <V> Supplier<V?>.neq(value: Supplier<out V?>): Boolean =
        this.get() != value.get()

infix fun <V> V?.eq(value: Supplier<out V?>): Boolean =
        this == value.get()

infix fun <V> V?.neq(value: Supplier<out V?>): Boolean =
        this != value.get()

infix fun <V> Optional<V>.eq(value: Supplier<out V?>): Boolean =
        this.orElse(null) == value.get()

infix fun <V> Optional<V>.neq(value: Supplier<out V?>): Boolean =
        this.orElse(null) != value.get()
