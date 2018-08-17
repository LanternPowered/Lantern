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
package org.lanternpowered.server.inject

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.lanternpowered.api.ext.*

class LazyInjectionTest {

    class MyAwesomeService {

        fun get(): Int = 100
    }

    class TestObject {

        val service: MyAwesomeService by injectLazily()
    }

    class TestObject1 {

        val service: MyAwesomeService by inject()
    }

    @Test
    fun test() {
        val module = object : AbstractModule() {
            override fun configure() {
                install(InjectionPointProvider())
                install(InjectablePropertyProvider())
                bind(MyAwesomeService::class.java).toProvider(Provider { MyAwesomeService() })
            }
        }

        val injector = Guice.createInjector(module)
        var testObject = injector.getInstance(TestObject::class.java)
        assertEquals(100, testObject.service.get())
        testObject = TestObject()
        try {
            testObject.service.get()
            assertTrue(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var testObject1 = TestObject1()
        try {
            testObject1.service.get()
            assertTrue(false)
        } catch (e: Exception) {
        }
        testObject1 = injector.getInstance(TestObject1::class.java)
        assertEquals(100, testObject1.service.get())
    }
}
