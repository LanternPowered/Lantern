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
package org.lanternpowered.server.catalog

import org.lanternpowered.api.key.NamespacedKey
import java.util.Objects

open class LanternNamespacedKey(private val namespace: String, private val value: String) : NamespacedKey {

    override fun getNamespace(): String = this.namespace
    override fun getValue(): String = this.value

    override fun compareTo(other: NamespacedKey): Int {
        val i = this.namespace.compareTo(other.namespace)
        return if (i != 0) i else this.value.compareTo(other.value)
    }

    override fun getFormatted(): String = toString()
    override fun toString(): String = this.namespace + ':' + this.value
    override fun hashCode(): Int = Objects.hash(this.namespace, this.value)
    override fun equals(other: Any?): Boolean =
            other is LanternNamespacedKey && other.namespace == this.namespace && other.value == this.value
}
