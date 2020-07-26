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

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.scoreboard.Scoreboard
import org.lanternpowered.api.text.Text

/**
 * Represents a context to render [Text].
 *
 * @property locale The locale to translate translatable text with
 * @property player The player for who the text will be rendered, if any
 * @property scoreboard The scoreboard that is active in the context, if any
 */
data class FormattedTextRenderContext(
        val locale: Locale,
        val player: Player?,
        val scoreboard: Scoreboard?
)
