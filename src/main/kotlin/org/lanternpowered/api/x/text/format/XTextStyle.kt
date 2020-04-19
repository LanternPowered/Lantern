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
package org.lanternpowered.api.x.text.format

import org.lanternpowered.api.text.format.TextStyle

interface XTextStyle : TextStyle {

    fun and(styles: Collection<TextStyle>): XTextStyle
    fun andNot(styles: Collection<TextStyle>): XTextStyle

}
