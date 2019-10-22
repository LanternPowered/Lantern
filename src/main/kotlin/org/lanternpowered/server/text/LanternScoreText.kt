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
package org.lanternpowered.server.text

import com.google.common.collect.ImmutableList
import org.lanternpowered.api.ext.*
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.text.ScoreText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.format.TextFormat
import java.util.Objects

class LanternScoreText : LanternText, ScoreText {

    internal val score: Score
    internal val override: String?

    internal constructor(score: Score) {
        this.score = score
        this.override = null
    }

    internal constructor(format: TextFormat, children: ImmutableList<Text>, clickAction: ClickAction<*>?,
                         hoverAction: HoverAction<*>?, shiftClickAction: ShiftClickAction<*>?,
                         score: Score, override: String?) : super(format, children, clickAction, hoverAction, shiftClickAction) {
        this.score = score
        this.override = override
    }

    override fun getScore() = this.score
    override fun getOverride() = this.override.optional()

    override fun toBuilder() = Builder(this)

    override fun equals(other: Any?) = other === this ||
            (other is LanternScoreText && super.equals(other) && other.score == this.score && other.override == this.override)
    override fun hashCode() = Objects.hash(super.hashCode(), this.score, this.override)

    override fun toStringHelper() = super.toStringHelper()
            .addFirstValue(this.score)
            .add("override", this.override)

    class Builder : AbstractBuilder<Builder>, ScoreText.Builder {

        private var score: Score? = null
        private var override: String? = null

        constructor()

        internal constructor(text: Text) : super(text) {
            if (text is LanternScoreText) {
                this.score = text.score
                this.override = text.override
            }
        }

        override fun getScore() = checkNotNull(this.score) { "The score isn't set" }
        override fun getOverride() = this.override.optional()

        override fun score(score: Score) = apply { this.score = score }
        override fun override(override: String?) = apply { this.override = override }

        override fun build(): ScoreText {
            val score = checkNotNull(this.score) { "The score must be set" }
            return LanternScoreText(this.format, ImmutableList.copyOf(this.children), this.clickAction,
                    this.hoverAction, this.shiftClickAction, score, this.override)
        }

        override fun equals(other: Any?) = other === this ||
                (other is Builder && super.equals(other) && other.score == this.score && other.override == this.override)
        override fun hashCode() = Objects.hash(super.hashCode(), this.score, this.override)

        override fun toStringHelper() = super.toStringHelper()
                .addFirstValue(this.score)
                .add("override", this.override)

        override fun from(value: Text) = Builder(value)
        override fun reset() = Builder()
    }
}
