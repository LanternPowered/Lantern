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

import com.google.common.collect.ImmutableList
import org.spongepowered.api.text.SelectorText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.selector.Selector
import java.util.Objects

class LanternSelectorText : LanternText, SelectorText {

    private val selector: Selector

    internal constructor(selector: Selector) {
        this.selector = selector
    }

    internal constructor(format: TextFormat, children: ImmutableList<Text>, clickAction: ClickAction<*>?,
                         hoverAction: HoverAction<*>?, shiftClickAction: ShiftClickAction<*>?,
                         selector: Selector) : super(format, children, clickAction, hoverAction, shiftClickAction) {
        this.selector = selector
    }

    override fun getSelector() = this.selector
    override fun toBuilder() = Builder(this)

    override fun equals(other: Any?) = other === this ||
            (other is LanternSelectorText && super.equals(other) && other.selector == this.selector)
    override fun hashCode() = Objects.hash(super.hashCode(), this.selector)

    override fun toStringHelper() = super.toStringHelper()
            .addValue(this.selector)

    class Builder : AbstractBuilder<Builder>, SelectorText.Builder {

        private var selector: Selector? = null

        constructor()

        internal constructor(text: Text) : super(text) {
            if (text is LanternSelectorText) {
                this.selector = text.selector
            }
        }

        override fun getSelector(): Selector = checkNotNull(this.selector) { "The selector is not yet set" }
        override fun selector(selector: Selector) = apply { this.selector = selector }

        override fun build(): SelectorText {
            val selector = checkNotNull(this.selector) { "The selector must be set" }
            return LanternSelectorText(this.format, ImmutableList.copyOf(this.children), this.clickAction,
                    this.hoverAction, this.shiftClickAction, selector)
        }


        override fun equals(other: Any?) = other === this ||
                (other is Builder && super.equals(other) && other.selector == this.selector)
        override fun hashCode() = Objects.hash(super.hashCode(), this.selector)

        override fun toStringHelper() = super.toStringHelper()
                .addFirstValue(this.selector)

        override fun from(value: Text) = Builder(value)
        override fun reset() = Builder()
    }
}
