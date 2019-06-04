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

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.format.TextColor
import org.lanternpowered.api.text.format.TextFormat
import org.lanternpowered.api.text.format.TextStyle
import org.lanternpowered.api.text.format.TextStyles
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.api.x.text.XTextFactory
import org.spongepowered.api.scoreboard.Score
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.action.TextAction
import org.spongepowered.api.text.selector.Selector

object LanternTextFactory : XTextFactory {

    override fun of(vararg objects: Any): Text {
        // Shortcut for a lonely TextRepresentable
        if (objects.size == 1 && objects[0] is TextRepresentable) {
            return (objects[0] as TextRepresentable).toText()
        }

        val builder = Text.builder()
        var format = TextFormat.of()
        var hoverAction: HoverAction<*>? = null
        var clickAction: ClickAction<*>? = null
        var shiftClickAction: ShiftClickAction<*>? = null
        var changedFormat = false

        for (obj in objects) {
            // Text formatting + actions
            when (obj) {
                is TextFormat -> {
                    changedFormat = true
                    format = obj
                }
                is TextColor -> {
                    changedFormat = true
                    format = format.color(obj)
                }
                is TextStyle -> {
                    changedFormat = true
                    format = format.style(if (obj == TextStyles.RESET) TextStyles.NONE else format.style.and(obj))
                }
                is TextAction<*> -> {
                    changedFormat = true
                    when (obj) {
                        is HoverAction<*> -> hoverAction = obj
                        is ClickAction<*> -> clickAction = obj
                        is ShiftClickAction<*> -> shiftClickAction = obj
                    }
                }
                is TextRepresentable -> {
                    // Special content
                    changedFormat = false
                    val childBuilder = obj.toText().toBuilder()

                    // Merge format (existing format has priority)
                    childBuilder.format(format.merge(childBuilder.format))

                    // Overwrite text actions if *NOT* present
                    if (!childBuilder.clickAction.isPresent) {
                        childBuilder.onClick(clickAction)
                    }
                    if (!childBuilder.hoverAction.isPresent) {
                        childBuilder.onHover(hoverAction)
                    }
                    if (!childBuilder.shiftClickAction.isPresent) {
                        childBuilder.onShiftClick(shiftClickAction)
                    }
                    builder.append(childBuilder.build())
                }
                else -> {
                    // Simple content
                    changedFormat = false

                    val childBuilder = when (obj) {
                        is String -> Text.builder(obj)
                        is Translation -> Text.builder(obj)
                        is Translatable -> Text.builder(obj.translation)
                        is Selector -> Text.builder(obj)
                        is Score -> Text.builder(obj)
                        else -> Text.builder(obj.toString())
                    }

                    if (hoverAction != null) {
                        childBuilder.onHover(hoverAction)
                    }
                    if (clickAction != null) {
                        childBuilder.onClick(clickAction)
                    }
                    if (shiftClickAction != null) {
                        childBuilder.onShiftClick(shiftClickAction)
                    }

                    builder.append(childBuilder.format(format).build())
                }
            }
        }

        if (changedFormat) {
            // Did the formatting change without being applied to something?
            // Then just append an empty text with that formatting
            val childBuilder = Text.builder()
            if (hoverAction != null) {
                childBuilder.onHover(hoverAction)
            }
            if (clickAction != null) {
                childBuilder.onClick(clickAction)
            }
            if (shiftClickAction != null) {
                childBuilder.onShiftClick(shiftClickAction)
            }
            builder.append(childBuilder.format(format).build())
        }

        // Single content, reduce Text depth
        return if (builder.children.size == 1) builder.children[0] else builder.build()
    }

    override fun joinWith(separator: Text, vararg texts: Text): Text {
        when (texts.size) {
            0 -> return LanternLiteralText.EMPTY
            1 -> return texts[0]
            else -> {
                val builder = Text.builder()
                var appendSeparator = false
                for (text in texts) {
                    if (appendSeparator) {
                        builder.append(separator)
                    } else {
                        appendSeparator = true
                    }
                    builder.append(text)
                }
                return builder.build()
            }
        }
    }

    override fun joinWith(separator: Text, texts: Iterator<Text>): Text {
        if (!texts.hasNext()) {
            return LanternLiteralText.EMPTY
        }
        val first = texts.next()
        if (!texts.hasNext()) {
            return first
        }
        val builder = Text.builder().append(first)
        do {
            builder.append(separator)
            builder.append(texts.next())
        } while (texts.hasNext())
        return builder.build()
    }
}
