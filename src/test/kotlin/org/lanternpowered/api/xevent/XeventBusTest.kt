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
package org.lanternpowered.api.xevent

import org.junit.Assert.assertTrue
import org.junit.Test
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.xevent.LanternXeventBus

class XeventBusTest {

    class TestXevent : Xevent {

        var someValue: Int = 0
    }

    @Test
    fun testA() {
        val newValue = 1000
        val bus = LanternXeventBus()
        bus.registerHandler(object : XeventHandler<TestXevent> {
            override fun handle(event: TestXevent) {
                event.someValue = newValue
            }
        })
        val event = TestXevent()
        assertTrue(event.someValue == 0)
        bus.post(event)
        assertTrue(event.someValue == newValue)
    }

    @Test
    fun testB() {
        val newValue = 1000
        val bus = LanternXeventBus()
        bus.registerHandler(object : XeventHandler<TestXevent> {
            override fun handle(event: TestXevent) {
                event.someValue = newValue
            }
        })
        val event = bus.post {
            val event1 = TestXevent()
            assertTrue(event1.someValue == 0)
            event1
        }
        assertTrue(event != null)
        assertTrue(event!!.someValue == newValue)
    }

    @Test
    fun testC() {
        val bus = LanternXeventBus()
        val event = bus.post(::TestXevent)
        assertTrue(event == null)
    }
}
