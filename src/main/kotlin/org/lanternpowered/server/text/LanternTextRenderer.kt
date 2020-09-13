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
import net.kyori.adventure.text.event.HoverEvent
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

    protected fun <T : BuildableComponent<T, B>, B : TextBuilder<T, B>> B.applyChildren(text: Text, context: C): B = apply {
        for (child in text.children())
            this.append(render(child, context))
    }

    protected fun <T : BuildableComponent<T, B>, B : TextBuilder<T, B>> B.applyStyle(text: Text, context: C): B = apply {
        this.mergeStyle(text, Style.Merge.colorAndDecorations())
        this.clickEvent(text.clickEvent())
        this.insertion(text.insertion())
        this.hoverEvent(renderHoverEventIfNeeded(text.hoverEvent(), context) ?: text.hoverEvent())
    }

    protected fun <T : BuildableComponent<T, B>, B : TextBuilder<T, B>> B.applyStyleAndChildren(text: Text, context: C): B =
            this.applyChildren(text, context).applyStyle(text, context)

    private fun <T : DataText<T, B>, B : NBTComponentBuilder<T, B>> B.applyNbt(text: DataText<*, *>): B = apply {
        this.nbtPath(text.nbtPath())
        this.interpret(text.interpret())
    }

    final override fun renderText(text: LiteralText, context: C): Text =
            this.renderLiteralIfNeeded(text, context) ?: text

    final override fun renderStorageNbt(text: StorageDataText, context: C): Text =
            this.renderStorageNbtIfNeeded(text, context) ?: text

    final override fun renderEntityNbt(text: EntityDataText, context: C): Text =
            this.renderEntityNbtIfNeeded(text, context) ?: text

    final override fun renderBlockNbt(text: BlockDataText, context: C): Text =
            this.renderBlockNbtIfNeeded(text, context) ?: text

    final override fun renderScore(text: ScoreText, context: C): Text =
            this.renderScoreIfNeeded(text, context) ?: text

    final override fun renderKeybind(text: KeybindText, context: C): Text =
            this.renderKeybindIfNeeded(text, context) ?: text

    final override fun renderSelector(text: SelectorText, context: C): Text =
            this.renderSelectorIfNeeded(text, context) ?: text

    final override fun renderTranslatable(text: TranslatableText, context: C): Text =
            this.renderTranslatableIfNeeded(text, context) ?: text

    protected open fun renderLiteralIfNeeded(text: LiteralText, context: C): Text? {
        return this.renderIfNeeded(text, context) {
            LiteralText.builder(text.content())
        }
    }

    protected open fun renderStorageNbtIfNeeded(text: StorageDataText, context: C): Text? {
        return this.renderIfNeeded(text, context) {
            StorageDataText.builder()
                    .storage(text.storage())
                    .applyNbt(text)
        }
    }

    protected open fun renderEntityNbtIfNeeded(text: EntityDataText, context: C): Text? {
        return this.renderIfNeeded(text, context) {
            EntityDataText.builder()
                    .selector(text.selector())
                    .applyNbt(text)
        }
    }

    protected open fun renderBlockNbtIfNeeded(text: BlockDataText, context: C): Text? {
        return this.renderIfNeeded(text, context) {
            BlockDataText.builder()
                    .pos(text.pos())
                    .applyNbt(text)
        }
    }

    protected open fun renderScoreIfNeeded(text: ScoreText, context: C): Text? {
        return this.renderIfNeeded(text, context) {
            ScoreText.builder()
                    .objective(text.objective())
                    .value(text.value())
                    .name(text.name())
        }
    }

    protected open fun renderKeybindIfNeeded(text: KeybindText, context: C): Text? {
        return this.renderIfNeeded(text, context) {
            KeybindText.builder().keybind(text.keybind())
        }
    }

    protected open fun renderSelectorIfNeeded(text: SelectorText, context: C): Text? {
        return this.renderIfNeeded(text, context) {
            SelectorText.builder().pattern(text.pattern())
        }
    }

    protected open fun renderTranslatableIfNeeded(text: TranslatableText, context: C): Text? {
        val format = this.translate(text.key(), context)
        val args = text.args()

        if (format == null) {
            val renderedArgs = this.renderListIfNeeded(text.args(), context)
            return this.renderIfNeeded(text, context, force = renderedArgs != null) {
                TranslatableText.builder(text.key())
                        .args(renderedArgs ?: args)
            }
        }

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
                builder.append(this.render(args[index], context))
            } else {
                builder.append(textOf(sb.substring(itr.index, end)))
            }
            itr.index = end
        }

        return builder.applyStyleAndChildren(text, context).build()
    }

    private fun <T : Text, B : TextBuilder<T, B>> renderIfNeeded(
            text: Text, context: C, force: Boolean = false, builderSupplier: () -> B
    ): T? {
        val children = this.renderListIfNeeded(text.children(), context)
        val hoverEvent = this.renderHoverEventIfNeeded(text.hoverEvent(), context)
        if (children == null && hoverEvent == null && !force)
            return null
        val builder = builderSupplier()
        builder.append(children ?: text.children())
        builder.mergeStyle(text, Style.Merge.colorAndDecorations())
        builder.hoverEvent(hoverEvent ?: text.hoverEvent())
        builder.clickEvent(text.clickEvent())
        builder.insertion(text.insertion())
        return builder.build()
    }

    protected fun renderHoverEventIfNeeded(hoverEvent: HoverEvent<*>?, context: C): HoverEvent<*>? {
        if (hoverEvent == null)
            return null
        return when (val value = hoverEvent.value()) {
            is Text -> {
                val text = this.renderIfNeeded(value, context) ?: return null
                HoverEvent.showText(text)
            }
            is HoverEvent.ShowEntity -> {
                val name = value.name() ?: return null
                val text = this.renderIfNeeded(name, context) ?: return null
                HoverEvent.showEntity(HoverEvent.ShowEntity.of(value.type(), value.id(), text))
            }
            is HoverEvent.ShowItem -> null // TODO
            else -> hoverEvent.withRenderedValue(this, context)
        }
    }

    /**
     * Renders the given [Text] for the [context]. This function will return `null`
     * in case nothing changed to the contents when rendering.
     */
    fun renderIfNeeded(text: Text, context: C): Text? {
        return when (text) {
            is LiteralText -> this.renderLiteralIfNeeded(text, context)
            is TranslatableText -> this.renderTranslatableIfNeeded(text, context)
            is KeybindText -> this.renderKeybindIfNeeded(text, context)
            is ScoreText -> this.renderScoreIfNeeded(text, context)
            is SelectorText -> this.renderSelectorIfNeeded(text, context)
            is DataText<*, *> -> when (text) {
                is BlockDataText -> this.renderBlockNbtIfNeeded(text, context)
                is EntityDataText -> this.renderEntityNbtIfNeeded(text, context)
                is StorageDataText -> this.renderStorageNbtIfNeeded(text, context)
                else -> text
            }
            else -> text
        }
    }

    /**
     * Renders the given list of [Text], if it's needed.
     */
    fun renderListIfNeeded(list: List<Text>, context: C): List<Text>? {
        if (list.isEmpty())
            return null
        var resultList: MutableList<Text>? = null
        for (index in list.indices) {
            val text = list[index]
            val result = this.renderIfNeeded(text, context)
            if (result != null) {
                if (resultList == null) {
                    resultList = ArrayList(list.size)
                    resultList.addAll(list.subList(0, index))
                }
                resultList.add(result)
            } else {
                resultList?.add(text)
            }
        }
        return resultList
    }

    /**
     * Gets a message format from a key and context.
     *
     * @param context a context
     * @param key a translation key
     * @return a message format or `null` to skip translation
     */
    protected abstract fun translate(key: String, context: C): MessageFormat?
}
