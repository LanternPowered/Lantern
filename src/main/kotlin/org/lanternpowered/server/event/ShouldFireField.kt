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
package org.lanternpowered.server.event

import org.lanternpowered.api.event.Event
import org.lanternpowered.lmbda.LambdaFactory
import org.lanternpowered.lmbda.kt.privateLookupIn
import org.lanternpowered.server.game.Lantern
import java.lang.invoke.MethodHandles
import java.lang.reflect.Field
import java.util.function.Consumer
import java.util.function.Supplier

internal class ShouldFireField(private val eventClass: Class<out Event>, field: Field) {

    private val setter: Consumer<Boolean>
    private val getter: Supplier<Boolean>

    /**
     * Sets the state of the field
     */
    fun setState(state: Boolean) {
        if (this.getter.get() == state)
            return
        Lantern.getLogger().debug("Updating ShouldFire field for class ${eventClass.name} with value $state")
        synchronized(this) {
            this.setter.accept(state)
        }
    }

    init {
        val lookup = lookup.privateLookupIn(field.declaringClass)
        this.setter = LambdaFactory.createConsumer(lookup.unreflectSetter(field))
        this.getter = LambdaFactory.createSupplier(lookup.unreflectGetter(field))
    }

    companion object {
        private val lookup = MethodHandles.lookup()
    }
}
