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

data class LanternPlaceholderText(
        private val parser: PlaceholderParser,
        private val context: PlaceholderContext
) : PlaceholderText {
    override fun getContext(): PlaceholderContext = this.context
    override fun getParser(): PlaceholderParser = this.parser
}
