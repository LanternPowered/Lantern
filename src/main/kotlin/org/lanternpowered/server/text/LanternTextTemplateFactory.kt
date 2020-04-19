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
package org.lanternpowered.server.text

import org.lanternpowered.api.x.text.XTextTemplateFactory
import org.spongepowered.api.text.TextTemplate

object LanternTextTemplateFactory : XTextTemplateFactory {

    override fun emptyTemplate() = LanternTextTemplate.EMPTY

    override fun template(openArg: String, closeArg: String, elements: Collection<Any>): TextTemplate {
        check(openArg.isNotEmpty()) { "open arg cannot be empty" }
        check(closeArg.isNotEmpty()) { "close arg cannot be empty" }
        return if (elements.isEmpty()) emptyTemplate() else LanternTextTemplate.of(openArg, closeArg, elements)
    }
}
