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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.placeholder.PlaceholderContext
import org.lanternpowered.api.text.placeholder.PlaceholderParser
import org.lanternpowered.api.text.placeholder.PlaceholderParserBuilder
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import java.util.function.Function

class LanternPlaceholderParserBuilder : AbstractCatalogBuilder<PlaceholderParser, PlaceholderParserBuilder>(),
        PlaceholderParserBuilder {

    private var parser: ((PlaceholderContext) -> Text)? = null

    override fun build(key: NamespacedKey): PlaceholderParser {
        val parser = checkNotNull(this.parser) { "The parser must be set" }
        return LanternPlaceholderParser(key, parser)
    }

    override fun parser(parser: Function<PlaceholderContext, Text>): PlaceholderParserBuilder = this.apply {
        this.parser = parser::apply
    }

    override fun reset(): PlaceholderParserBuilder = this.apply {
        super.reset()
        this.parser = null
    }
}
