package org.lanternpowered.api.util.property

import org.lanternpowered.api.Lantern
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ServiceProperty<T>(private val type: Class<T>) : ReadOnlyProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = Lantern.serviceManager.provideUnchecked(this.type)
}
