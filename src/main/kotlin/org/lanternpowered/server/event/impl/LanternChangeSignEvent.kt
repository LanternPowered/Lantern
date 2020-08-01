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
package org.lanternpowered.server.event.impl

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.text.Text
import org.spongepowered.api.block.entity.Sign
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.event.block.entity.ChangeSignEvent

class LanternChangeSignEvent(
        cause: Cause,
        private val originalText: ListValue.Immutable<Text>,
        private val text: ListValue.Mutable<Text>,
        private val sign: Sign
) : CancellableEvent(cause), ChangeSignEvent {

    override fun getOriginalText(): ListValue.Immutable<Text> = this.originalText
    override fun getText(): ListValue.Mutable<Text> = this.text
    override fun getSign(): Sign = this.sign
}
