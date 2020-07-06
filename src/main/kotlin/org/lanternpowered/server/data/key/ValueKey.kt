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
package org.lanternpowered.server.data.key

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.first
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.asUnmodifiableList
import org.lanternpowered.api.util.type.typeTokenOf
import org.lanternpowered.server.data.value.ValueConstructorFactory
import org.lanternpowered.server.event.LanternEventManager
import org.lanternpowered.server.event.RegisteredListener
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.data.ChangeDataHolderEvent
import org.spongepowered.plugin.PluginContainer
import java.util.Objects
import java.util.function.BiPredicate

/**
 * Represents a [Key] that can be used to retrieve/offer data from [ValueContainer]s.
 *
 * Explicit registration is required by default for lantern created [Key]s. Keys built
 * directly using the [Key.Builder] don't have to be registered explicitly, this is to
 * be compatible with the specification of the API.
 *
 * @property key The key of the value key
 * @property valueToken The type of the value
 * @property elementToken The type of the element
 * @property requiresExplicitRegistration Whether this key needs to be registered explicitly on a key collection or registry
 */
open class ValueKey<V : Value<E>, E : Any> internal constructor(
        private val key: CatalogKey,
        private val valueToken: TypeToken<V>,
        private val elementToken: TypeToken<E>,
        private val elementComparator: Comparator<in E>,
        private val elementIncludesTester: BiPredicate<in E, in E>,
        private val defaultElementSupplier: () -> E?,
        val requiresExplicitRegistration: Boolean
) : Key<V>, CatalogType {

    private val mutableListeners = mutableListOf<RegisteredListener<ChangeDataHolderEvent.ValueChange>>()

    /**
     * An unmodifiable list of all the registered value change event listeners.
     */
    val listeners = this.mutableListeners.asUnmodifiableList()

    /**
     * The value constructor of the key.
     */
    open val valueConstructor by lazy { ValueConstructorFactory.getConstructor(this) }

    private val hashCode = Objects.hash(this.valueToken, this.key, this.elementToken)

    override fun getValueToken() = this.valueToken
    override fun getElementToken() = this.elementToken

    override fun <E : DataHolder> registerEvent(holderFilter: Class<E>, listener: EventListener<ChangeDataHolderEvent.ValueChange>) {
        val keyEventListener = ValueKeyEventListener(listener, holderFilter::isInstance, this)
        val plugin = CauseStack.current().first<PluginContainer>() ?: error("There no plugin in the cause stack.")
        val registeredListener = LanternEventManager.register(
                plugin, valueChangeEventTypeToken, Order.DEFAULT, keyEventListener)
        this.mutableListeners.add(registeredListener)
    }

    override fun getKey() = this.key
    override fun getElementComparator() = this.elementComparator
    override fun getElementIncludesTester() = this.elementIncludesTester

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        other as ValueKey<*,*>
        return this.valueToken == other.valueToken &&
                this.key == other.key &&
                this.elementToken == other.elementToken
    }

    override fun hashCode() = this.hashCode

    override fun toString() = ToStringHelper(this)
            .add("id", this.key)
            .add("valueToken", this.valueToken)
            .add("elementToken", this.elementToken)
            .toString()

    companion object {

        private val valueChangeEventTypeToken = typeTokenOf<ChangeDataHolderEvent.ValueChange>()
    }
}
