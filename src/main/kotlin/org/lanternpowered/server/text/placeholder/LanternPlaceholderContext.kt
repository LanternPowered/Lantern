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

import org.lanternpowered.api.text.placeholder.PlaceholderContext
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.orNull
import java.util.Optional

class LanternPlaceholderContext(
        private val argument: String?,
        val associatedObjectSupplier: (() -> Any?)?
) : PlaceholderContext {

    override fun getArgumentString(): Optional<String> = this.argument.asOptional()
    override fun getAssociatedObject(): Optional<Any> = this.associatedObjectSupplier?.invoke().asOptional()

    override fun toString(): String = ToStringHelper(this)
            .omitNullValues()
            .add("argument", this.argument)
            .add("associatedObject", this.associatedObject.orNull())
            .toString()
}
