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

import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.NBTComponentBuilder
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.renderer.AbstractComponentRenderer
import org.lanternpowered.api.text.BlockDataText
import org.lanternpowered.api.text.DataText
import org.lanternpowered.api.text.EntityDataText
import org.lanternpowered.api.text.KeybindText
import org.lanternpowered.api.text.LiteralText
import org.lanternpowered.api.text.ScoreText
import org.lanternpowered.api.text.SelectorText
import org.lanternpowered.api.text.StorageDataText
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextBuilder
import org.lanternpowered.api.text.TranslatableText
import org.lanternpowered.api.text.textOf
import java.text.MessageFormat

abstract class LanternTextRenderer<C> : AbstractComponentRenderer<C>() {

    private fun isChildRenderNeeded(text: Text): Boolean =
            text.children().isNotEmpty() || text.style().hoverEvent() != null

    protected fun <T : BuildableComponent<T, B>, B : TextBuilder<T, B>> B.applyChildren(text: Text, context: C): B = apply {
        for (child in text.children())
            append(render(child, context))
    }

    protected fun <T : BuildableComponent<T, B>, B : TextBuilder<T, B>> B.applyStyle(text: Text, context: C): B = apply {
        mergeStyle(text, Style.Merge.colorAndDecorations())
        clickEvent(text.clickEvent())
        val hoverEvent = text.hoverEvent()
        if (hoverEvent != null)
            hoverEvent(hoverEvent.withRenderedValue(this@LanternTextRenderer, context))
    }

    protected fun <T : BuildableComponent<T, B>, B : TextBuilder<T, B>> B.applyStyleAndChildren(text: Text, context: C): B =
            applyChildren(text, context).applyStyle(text, context)

    private fun <T : DataText<T, B>, B : NBTComponentBuilder<T, B>> B.applyNbt(text: DataText<*, *>): B = apply {
        nbtPath(text.nbtPath())
        interpret(text.interpret())
    }

    override fun renderText(text: LiteralText, context: C): Text {
        if (!isChildRenderNeeded(text))
            return text
        return LiteralText.builder(text.content())
                .applyStyleAndChildren(text, context)
                .build()
    }

    override fun renderStorageNbt(text: StorageDataText, context: C): Text {
        if (!isChildRenderNeeded(text))
            return text
        return StorageDataText.builder()
                .storage(text.storage())
                .applyNbt(text)
                .applyStyleAndChildren(text, context)
                .build()
    }

    override fun renderEntityNbt(text: EntityDataText, context: C): Text {
        if (!isChildRenderNeeded(text))
            return text
        return EntityDataText.builder()
                .selector(text.selector())
                .applyNbt(text)
                .applyStyleAndChildren(text, context)
                .build()
    }

    override fun renderBlockNbt(text: BlockDataText, context: C): Text {
        if (!isChildRenderNeeded(text))
            return text
        return BlockDataText.builder()
                .pos(text.pos())
                .applyNbt(text)
                .applyStyleAndChildren(text, context)
                .build()
    }

    override fun renderScore(text: ScoreText, context: C): Text {
        if (!isChildRenderNeeded(text))
            return text
        return ScoreText.builder()
                .objective(text.objective())
                .value(text.value())
                .name(text.name())
                .applyStyleAndChildren(text, context)
                .build()
    }

    override fun renderKeybind(text: KeybindText, context: C): Text {
        if (!isChildRenderNeeded(text))
            return text
        return KeybindText.builder()
                .keybind(text.keybind())
                .applyStyleAndChildren(text, context)
                .build()
    }

    override fun renderSelector(text: SelectorText, context: C): Text {
        if (!isChildRenderNeeded(text))
            return text
        return SelectorText.builder()
                .pattern(text.pattern())
                .applyStyleAndChildren(text, context)
                .build()
    }

    override fun renderTranslatable(text: TranslatableText, context: C): Text {
        val format = translate(context, text.key())
        if (format == null) {
            if (!isChildRenderNeeded(text))
                return text
            return TranslatableText.builder(text.key())
                    .applyStyleAndChildren(text, context)
                    .build()
        }

        val args = text.args()
        val builder = LiteralText.builder()

        if (args.isEmpty())
            return builder.content(format.format(null, StringBuffer(), null).toString())
                    .applyStyleAndChildren(text, context)
                    .build()

        val nulls = arrayOfNulls<Any>(args.size)
        val sb = format.format(nulls, StringBuffer(), null)
        val itr = format.formatToCharacterIterator(nulls)

        while (itr.index < itr.endIndex) {
            val end = itr.runLimit
            val index = itr.getAttribute(MessageFormat.Field.ARGUMENT) as Int?
            if (index != null) {
                builder.append(render(args[index], context))
            } else {
                builder.append(textOf(sb.substring(itr.index, end)))
            }
            itr.index = end
        }

        return builder.applyStyleAndChildren(text, context).build()
    }

    /**
     * Gets a message format from a key and context.
     *
     * @param context a context
     * @param key a translation key
     * @return a message format or `null` to skip translation
     */
    protected abstract fun translate(context: C, key: String): MessageFormat?
}
