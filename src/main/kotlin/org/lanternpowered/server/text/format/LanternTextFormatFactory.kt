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
package org.lanternpowered.server.text.format

import org.lanternpowered.api.x.text.format.XTextFormatFactory
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextStyle

object LanternTextFormatFactory : XTextFormatFactory {

    override fun format(color: TextColor, style: TextStyle) =
            LanternTextFormat(color, style)

    override fun style(bold: Boolean?, italic: Boolean?, underline: Boolean?, strikeThrough: Boolean?, obfuscated: Boolean?) =
            LanternTextStyle(bold, italic, underline, strikeThrough, obfuscated)

    override fun emptyFormat() = LanternTextFormat.EMPTY
}
