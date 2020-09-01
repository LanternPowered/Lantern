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
import org.lanternpowered.api.text.placeholder.PlaceholderParser
import org.lanternpowered.api.text.placeholder.PlaceholderText
import org.lanternpowered.api.text.placeholder.PlaceholderTextBuilder

class LanternPlaceholderTextBuilder : PlaceholderTextBuilder {

    private var context: PlaceholderContext? = null
    private var parser: PlaceholderParser? = null

    override fun setContext(context: PlaceholderContext): PlaceholderTextBuilder = this.apply {
        this.context = context
    }

    override fun setParser(parser: PlaceholderParser): PlaceholderTextBuilder = this.apply {
        this.parser = parser
    }

    override fun reset(): PlaceholderTextBuilder = this.apply {
        this.context = null
        this.parser = null
    }

    override fun build(): PlaceholderText {
        val context = checkNotNull(this.context) { "The context must be set" }
        val parser = checkNotNull(this.parser) { "The context must be set" }
        return LanternPlaceholderText(parser, context)
    }
}
