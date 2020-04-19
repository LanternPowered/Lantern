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
package org.lanternpowered.server.ext

/**
 * Wraps the degree rotation value between 0 and 360 (exclusive).
 */
fun Double.wrapDegRot(): Double {
    var v = this
    while (v < 0) v += 360
    v %= 360
    return v
}

/**
 * Wraps the degree rotation value between 0 and 360 (exclusive).
 */
fun Int.wrapDegRot(): Int {
    var v = this
    while (v < 0) v += 360
    v %= 360
    return v
}

/**
 * Wraps the degree rotation value between 0 and 360 (exclusive).
 */
fun Float.wrapDegRot(): Float {
    var v = this
    while (v < 0) v += 360
    v %= 360
    return v
}
