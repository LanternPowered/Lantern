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
import org.lanternpowered.api.util.optional.optional
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
