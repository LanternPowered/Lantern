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
package org.lanternpowered.server.data.io.store.item

import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.server.data.io.store.SimpleValueContainer
import org.lanternpowered.server.text.LanternTexts.fromLegacy
import org.lanternpowered.server.text.LanternTexts.toLegacy
import org.lanternpowered.server.text.translation.TranslationContext
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.BookView
import java.util.Locale

class WrittenBookItemTypeObjectSerializer : WritableBookItemTypeObjectSerializer() {

    override fun serializeValues(itemStack: ItemStack, valueContainer: SimpleValueContainer, dataView: DataView) {
        super.serializeValues(itemStack, valueContainer, dataView)
        valueContainer.remove(Keys.BOOK_PAGES).ifPresent { pages ->
            dataView[PAGES] = pages.map { page -> JsonTextSerializer.serialize(page) }
        }
        valueContainer.remove(Keys.BOOK_AUTHOR).ifPresent { text ->
            dataView[AUTHOR] = toLegacy(text)
        }
        valueContainer.remove(Keys.DISPLAY_NAME).ifPresent { text ->
            dataView[TITLE] = toLegacy(text)
        }
        valueContainer.remove(Keys.GENERATION).ifPresent { value ->
            dataView[GENERATION] = value
        }
    }

    override fun deserializeValues(itemStack: ItemStack, valueContainer: SimpleValueContainer, dataView: DataView) {
        super.deserializeValues(itemStack, valueContainer, dataView)
        dataView.getStringList(PAGES).ifPresent { lines ->
            valueContainer[Keys.BOOK_PAGES] = lines.map { page -> JsonTextSerializer.deserializeUnchecked(page) }
        }
        dataView.getString(AUTHOR).ifPresent { author ->
            valueContainer[Keys.BOOK_AUTHOR] = fromLegacy(author)
        }
        dataView.getString(TITLE).ifPresent { title ->
            valueContainer[Keys.DISPLAY_NAME] = fromLegacy(title)
        }
        dataView.getInt(GENERATION).ifPresent {
            value -> valueContainer[Keys.GENERATION] = value
        }
    }

    companion object {

        @JvmField
        val AUTHOR: DataQuery = DataQuery.of("author")

        @JvmField
        val TITLE: DataQuery = DataQuery.of("title")

        private val GENERATION = DataQuery.of("generation")

        @JvmStatic
        fun writeBookData(dataView: DataView, bookView: BookView, locale: Locale?) {
            TranslationContext.enter()
                    .locale(locale)
                    .enableForcedTranslations().use {
                        dataView[AUTHOR] = toLegacy(bookView.author)
                        dataView[TITLE] = toLegacy(bookView.title)
                        dataView.set(PAGES, bookView.pages.map { page -> JsonTextSerializer.serialize(page) })
                    }
        }
    }
}