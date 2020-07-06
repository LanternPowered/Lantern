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
        valueContainer.remove(Keys.PAGES).ifPresent { pages ->
            dataView[PAGES] = pages.map { page -> JsonTextSerializer.serialize(page) }
        }
        valueContainer.remove(Keys.AUTHOR).ifPresent { text ->
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
            valueContainer[Keys.PAGES] = lines.map { page -> JsonTextSerializer.deserializeUnchecked(page) }
        }
        dataView.getString(AUTHOR).ifPresent { author ->
            valueContainer[Keys.AUTHOR] = fromLegacy(author)
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