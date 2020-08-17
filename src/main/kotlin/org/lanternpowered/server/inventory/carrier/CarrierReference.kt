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
package org.lanternpowered.server.inventory.carrier

import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.item.inventory.Carrier
import java.lang.ref.WeakReference

interface CarrierReference<T : Any> {

    fun carrier(): Carrier?

    fun get(): T?

    fun <R> ofType(type: Class<R>): R?

    fun set(carrier: Carrier?)

    companion object {

        fun <T : Any> of(type: Class<T>): CarrierReference<T> = CarrierReferenceImpl(type)
    }
}

private class CarrierReferenceImpl<T : Any>(private val type: Class<T>) : CarrierReference<T> {

    private var value: Any? = null

    override fun carrier(): Carrier? =
            this.get() as Carrier?

    override fun get(): T? {
        val value = this.value ?: return null
        if (value is WeakReference<*>)
            return value.get().uncheckedCast()
        return value.uncheckedCast()
    }

    override fun <R> ofType(type: Class<R>): R? {
        val carrier = this.get()
        return if (type.isInstance(carrier)) carrier.uncheckedCast() else null
    }

    override fun set(carrier: Carrier?) {
        if (!this.type.isInstance(carrier)) {
            this.value = null
        } else if (carrier is Entity || carrier is BlockEntity) {
            this.value = WeakReference(carrier)
        } else {
            this.value = carrier
        }
    }
}
