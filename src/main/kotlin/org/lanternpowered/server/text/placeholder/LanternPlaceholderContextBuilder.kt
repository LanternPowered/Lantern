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
package org.lanternpowered.server.text.placeholder

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.Server
import org.lanternpowered.api.SystemSubject
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.text.placeholder.PlaceholderContext
import org.lanternpowered.api.text.placeholder.PlaceholderContextBuilder
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.WorldManager
import org.lanternpowered.server.entity.WeakEntityReference
import java.lang.ref.WeakReference
import java.util.function.Supplier

class LanternPlaceholderContextBuilder : PlaceholderContextBuilder {

    private var associatedObjectSupplier: (() -> Any?)? = null
    private var argument: String? = null

    override fun setArgumentString(string: String?): PlaceholderContextBuilder = this.apply {
        this.argument = if (string.isNullOrEmpty()) null else string
    }

    override fun setAssociatedObject(associatedObject: Any?): PlaceholderContextBuilder = this.apply {
        when (associatedObject) {
            null -> {
                this.associatedObjectSupplier = null
            }
            is Supplier<*> -> {
                val supplier = associatedObject.uncheckedCast<Supplier<Any?>>()
                this.associatedObjectSupplier = supplier::get
            }
            is Function<*> -> {
                this.associatedObjectSupplier = associatedObject.uncheckedCast()
            }
            is SystemSubject -> {
                this.associatedObjectSupplier = Lantern::systemSubject
            }
            is Server -> {
                this.associatedObjectSupplier = Lantern::server
            }
            is Entity -> {
                val reference = WeakEntityReference(associatedObject)
                this.associatedObjectSupplier = reference::entity
            }
            is World -> {
                val key = associatedObject.key
                this.associatedObjectSupplier = { WorldManager.getWorld(key).orNull() }
            }
            else -> {
                val reference = WeakReference(associatedObject)
                this.associatedObjectSupplier = reference::get
            }
        }
    }

    override fun setAssociatedObject(supplier: Supplier<Any?>?): PlaceholderContextBuilder =
            this.setAssociatedObject(supplier as Any)

    override fun reset(): PlaceholderContextBuilder = this.apply {
        this.associatedObjectSupplier = null
        this.argument = null
    }

    override fun build(): PlaceholderContext =
            LanternPlaceholderContext(this.argument, this.associatedObjectSupplier)
}
