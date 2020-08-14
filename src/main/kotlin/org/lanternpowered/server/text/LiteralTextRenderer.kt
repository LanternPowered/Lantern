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

import net.kyori.adventure.translation.TranslationRegistry
import org.lanternpowered.api.text.BlockDataText
import org.lanternpowered.api.text.EntityDataText
import org.lanternpowered.api.text.KeybindText
import org.lanternpowered.api.text.LiteralText
import org.lanternpowered.api.text.ScoreText
import org.lanternpowered.api.text.SelectorText
import org.lanternpowered.api.text.StorageDataText
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TranslatableText
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.util.optional.orNull
import java.text.MessageFormat

/**
 * A [LanternTextRenderer] that renders all the [Text] components in their literal form.
 */
class LiteralTextRenderer(
        private val translationRegistry: TranslationRegistry
) : LanternTextRenderer<FormattedTextRenderContext>() {

    override fun renderKeybindIfNeeded(text: KeybindText, context: FormattedTextRenderContext): Text? =
            LiteralText.builder("[${text.keybind()}]")
                    .applyStyleAndChildren(text, context).build()

    override fun renderSelectorIfNeeded(text: SelectorText, context: FormattedTextRenderContext): Text? =
            LiteralText.builder(text.pattern())
                    .applyStyleAndChildren(text, context).build()

    override fun renderScoreIfNeeded(text: ScoreText, context: FormattedTextRenderContext): Text? {
        val value = text.value()
        if (value != null)
            return LiteralText.builder(value).applyStyleAndChildren(text, context).build()
        val scoreboard = context.scoreboard ?: return emptyText()
        val objective = scoreboard.getObjective(text.objective()).orNull() ?: return emptyText()
        var name = text.name()
        // This shows the readers own score
        if (name == "*") {
            name = context.player?.name ?: ""
            if (name.isEmpty())
                return emptyText()
        }
        val score = objective.getScore(textOf(name)).orNull() ?: return emptyText()
        return LiteralText.builder(score.score.toString()).applyStyleAndChildren(text, context).build()
    }

    // TODO: Lookup the actual data from the blocks, entities, etc.

    override fun renderBlockNbtIfNeeded(text: BlockDataText, context: FormattedTextRenderContext): Text? =
            LiteralText.builder("[${text.nbtPath()}] @ ${text.pos().asString()}")
                    .applyStyleAndChildren(text, context).build()

    override fun renderEntityNbtIfNeeded(text: EntityDataText, context: FormattedTextRenderContext): Text? =
            LiteralText.builder("[${text.nbtPath()}] @ ${text.selector()}")
                    .applyStyleAndChildren(text, context).build()

    override fun renderStorageNbtIfNeeded(text: StorageDataText, context: FormattedTextRenderContext): Text? =
            LiteralText.builder("[${text.nbtPath()}] @ ${text.storage()}")
                    .applyStyleAndChildren(text, context).build()

    override fun renderTranslatableIfNeeded(text: TranslatableText, context: FormattedTextRenderContext): Text? {
        val format = this.translate(text.key(), context)
        if (format != null)
            return super.renderTranslatableIfNeeded(text, context)
        return LiteralText.builder(text.key())
                .applyStyleAndChildren(text, context).build()
    }

    override fun translate(key: String, context: FormattedTextRenderContext): MessageFormat? =
            this.translationRegistry.translate(key, context.locale)
}
