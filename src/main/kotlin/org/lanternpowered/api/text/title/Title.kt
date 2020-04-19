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
@file:JvmName("TitleFactory")
@file:Suppress("UNUSED_PARAMETER", "NOTHING_TO_INLINE", "FunctionName")

package org.lanternpowered.api.text.title

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias Title = org.spongepowered.api.text.title.Title
typealias TitleBuilder = org.spongepowered.api.text.title.Title.Builder

/**
 * Constructs a new [title].
 *
 * @param fn The builder function
 * @return The constructed title
 */
inline fun title(fn: TitleBuilder.() -> Unit): Title {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return Title.builder().apply(fn).build()
}
