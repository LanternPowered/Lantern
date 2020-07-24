package org.lanternpowered.server.event.impl

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.text.Text
import org.spongepowered.api.block.entity.Sign
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.event.block.entity.ChangeSignEvent

class LanternChangeSignEvent(
        private val cause: Cause,
        private val originalText: ListValue.Immutable<Text>,
        private val text: ListValue.Mutable<Text>,
        private val sign: Sign
) : CancellableEvent(), ChangeSignEvent {

    override fun getOriginalText(): ListValue.Immutable<Text> = this.originalText
    override fun getCause(): Cause = this.cause
    override fun getText(): ListValue.Mutable<Text> = this.text
    override fun getSign(): Sign = this.sign
}
