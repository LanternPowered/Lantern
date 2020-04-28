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
package org.lanternpowered.server.inject

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.lanternpowered.server.ext.inject
import org.lanternpowered.server.ext.injectLazily

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
