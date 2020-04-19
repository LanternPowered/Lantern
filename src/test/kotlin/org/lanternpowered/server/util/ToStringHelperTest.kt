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

import org.junit.Test
import org.lanternpowered.api.util.ToStringHelper

class ToStringHelperTest {

    @Test
    fun testA() {
        val obj = SomeObject(1, "aaa")
        val toStringHelper = ToStringHelper(obj::class)
        toStringHelper.add("a", obj.a)
        toStringHelper.add("b", obj.b)
        assert(toStringHelper.toString() == obj.toString())
    }

    @Test
    fun testB() {
        val obj = SomeObject(1, "aaa")
        val toStringHelper = ToStringHelper(obj::class)
        toStringHelper.add("b", obj.b)
        toStringHelper.addFirst("a", obj.a)
        assert(toStringHelper.toString() == obj.toString())
    }

    data class SomeObject(val a: Int, val b: String)
}
