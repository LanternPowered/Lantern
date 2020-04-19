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
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TranslatableText
import org.lanternpowered.api.text.TranslatableTextBuilder
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import java.util.Objects

class LanternTranslatableText : LanternText, TranslatableText {

    internal val translation: Translation
    internal val arguments: ImmutableList<Any>

    constructor(translation: Translation, arguments: ImmutableList<Any>) {
        this.translation = translation
        this.arguments = arguments
    }

    internal constructor(format: TextFormat, children: ImmutableList<Text>, clickAction: ClickAction<*>?,
                         hoverAction: HoverAction<*>?, shiftClickAction: ShiftClickAction<*>?, translation: Translation,
                         arguments: ImmutableList<Any>) : super(format, children, clickAction, hoverAction, shiftClickAction) {
        this.translation = translation
        this.arguments = arguments
    }

    override fun getTranslation() = this.translation
    override fun getArguments() = this.arguments

    override fun toBuilder() = Builder(this)

    override fun equals(other: Any?) = other === this ||
            (other is LanternTranslatableText && super.equals(other) && other.translation == this.translation && other.arguments == this.arguments)
    override fun hashCode(): Int = Objects.hash(super.hashCode(), this.translation, this.arguments)

    override fun toStringHelper() = super.toStringHelper()
            .addFirstValue(this.translation)
            .add("arguments", this.arguments)

    class Builder : AbstractBuilder<Builder>, TranslatableTextBuilder {

        private var translation: Translation? = null
        private var arguments: ImmutableList<Any> = ImmutableList.of()

        constructor()

        internal constructor(text: Text) : super(text) {
            if (text is LanternTranslatableText) {
                this.translation = text.translation
                this.arguments = text.arguments
            }
        }

        override fun getTranslation() = checkNotNull(this.translation) { "The translation isn't set" }
        override fun getArguments() = this.arguments

        override fun from(value: Text) = Builder(value)
        override fun reset() = Builder()

        fun translation(translation: Translation, args: Iterable<Any>) = apply {
            this.translation = translation
            this.arguments = args.toImmutableList()
        }

        override fun translation(translation: Translation, vararg args: Any) =
                translation(translation, (args as Array<*>).toImmutableList())
        override fun translation(translatable: Translatable, vararg args: Any) =
                translation(translatable.translation, (args as Array<*>).toImmutableList())

        override fun build(): TranslatableText {
            val translation = checkNotNull(this.translation) { "The translation must be set" }
            return LanternTranslatableText(this.format, ImmutableList.copyOf(this.children), this.clickAction,
                    this.hoverAction, this.shiftClickAction, translation, this.arguments)
        }

        override fun equals(other: Any?) = other === this ||
                (other is Builder && super.equals(other) && other.translation == this.translation && other.arguments == this.arguments)
        override fun hashCode() = Objects.hash(super.hashCode(), this.translation, this.arguments)

        override fun toStringHelper() = super.toStringHelper()
                .addFirstValue(this.translation)
                .add("arguments", this.arguments)
    }
}
