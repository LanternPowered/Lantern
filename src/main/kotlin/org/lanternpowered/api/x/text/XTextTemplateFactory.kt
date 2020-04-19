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
package org.lanternpowered.api.x.text

import org.spongepowered.api.text.TextTemplate

interface XTextTemplateFactory : TextTemplate.Factory {

    override fun template(openArg: String, closeArg: String, elements: Array<Any>) = template(openArg, closeArg, elements.asList())

    fun template(openArg: String, closeArg: String, elements: Collection<Any>): TextTemplate
}
