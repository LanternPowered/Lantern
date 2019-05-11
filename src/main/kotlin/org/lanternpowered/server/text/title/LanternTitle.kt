/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.text.title

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.title.Title
import org.lanternpowered.api.text.title.TitleBuilder
import org.lanternpowered.api.util.ToStringHelper

data class LanternTitle(
        internal val title: Text?,
        internal val subtitle: Text?,
        internal val actionBar: Text?,
        internal val fadeIn: Int?,
        internal val stay: Int?,
        internal val fadeOut: Int?,
        internal val clear: Boolean,
        internal val reset: Boolean
) : Title {

    override fun getTitle() = this.title.optional()
    override fun getSubtitle() = this.subtitle.optional()
    override fun getActionBar() = this.actionBar.optional()
    override fun getFadeIn() = this.fadeIn.optional()
    override fun getStay() = this.stay.optional()
    override fun getFadeOut() = this.fadeOut.optional()
    override fun isClear() = this.clear
    override fun isReset() = this.reset

    override fun toBuilder() = LanternTitleBuilder().from(this)

    override fun toString() = ToStringHelper("Title")
            .omitNullValues()
            .add("title", this.title)
            .add("subtitle", this.subtitle)
            .add("actionBar", this.actionBar)
            .add("fadeIn", this.fadeIn)
            .add("stay", this.stay)
            .add("fadeOut", this.fadeOut)
            .add("clear", this.clear)
            .add("reset", this.reset)
            .toString()
}

class LanternTitleBuilder : TitleBuilder {

    private var title: Text? = null
    private var subtitle: Text? = null
    private var actionBar: Text? = null
    private var fadeIn: Int? = null
    private var stay: Int? = null
    private var fadeOut: Int? = null
    private var clear: Boolean = false
    private var reset: Boolean = false

    override fun getTitle() = this.title.optional()
    override fun getSubtitle() = this.subtitle.optional()
    override fun getActionBar() = this.actionBar.optional()
    override fun getFadeIn() = this.fadeIn.optional()
    override fun getStay() = this.stay.optional()
    override fun getFadeOut() = this.fadeOut.optional()
    override fun isClear() = this.clear
    override fun isReset() = this.reset

    override fun title(title: Text?) = apply { this.title = title }
    override fun subtitle(subtitle: Text?) = apply { this.subtitle = subtitle }
    override fun actionBar(actionBar: Text?) = apply { this.actionBar = actionBar }
    override fun fadeIn(fadeIn: Int?) = apply { this.fadeIn = fadeIn }
    override fun stay(stay: Int?) = apply { this.stay = stay }
    override fun fadeOut(fadeOut: Int?) = apply { this.fadeOut = fadeOut }
    override fun clear() = clear(true)
    override fun clear(clear: Boolean) = apply { this.clear = clear }
    override fun doReset() = doReset(true)
    override fun doReset(reset: Boolean) = apply { this.reset = reset }

    override fun from(value: Title) = apply {
        value as LanternTitle
        this.title = value.title
        this.subtitle = value.subtitle
        this.actionBar = value.actionBar
        this.fadeIn = value.fadeIn
        this.stay = value.stay
        this.fadeOut = value.fadeOut
        this.clear = value.clear
        this.reset = value.reset
    }

    override fun reset() = apply {
        this.title = null
        this.subtitle = null
        this.actionBar = null
        this.fadeIn = null
        this.stay = null
        this.fadeOut = null
        this.clear = false
        this.reset = false
    }

    override fun build() = LanternTitle(this.title, this.subtitle, this.actionBar, this.fadeIn, this.stay, this.fadeOut, this.clear, this.reset)

    override fun toString() = ToStringHelper(this::class)
            .omitNullValues()
            .add("title", this.title)
            .add("subtitle", this.subtitle)
            .add("actionBar", this.actionBar)
            .add("fadeIn", this.fadeIn)
            .add("stay", this.stay)
            .add("fadeOut", this.fadeOut)
            .add("clear", this.clear)
            .add("reset", this.reset)
            .toString()
}
