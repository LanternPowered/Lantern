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
package org.lanternpowered.server.text.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.api.util.type.typeTokenOf
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataException
import org.spongepowered.api.data.persistence.Queries.TEXT_AUTHOR
import org.spongepowered.api.data.persistence.Queries.TEXT_PAGE_LIST
import org.spongepowered.api.data.persistence.Queries.TEXT_TITLE
import org.spongepowered.api.text.BookView
import org.spongepowered.api.text.Text
import java.util.Optional

/**
 * An implementation of [AbstractDataBuilder] and [TypeSerializer] for [BookView].
 */
class BookViewConfigSerializer : AbstractDataBuilder<BookView>(BookView::class.java, 1), TypeSerializer<BookView> {

    private fun getText(textView: Any): Text {
        if (textView !is DataView) {
            throw InvalidDataException("Expected DataView")
        }
        return Sponge.getDataManager().deserialize(Text::class.java, textView).get()
    }

    private fun findText(container: DataView, query: DataQuery): Optional<Text> {
        return if (container.contains(query)) Optional.of(getText(container.get(query).get())) else Optional.empty()
    }

    @Throws(InvalidDataException::class)
    override fun buildContent(container: DataView): Optional<BookView> {
        val builder = BookView.builder()
        findText(container, TEXT_TITLE).ifPresent { builder.title(it) }
        findText(container, TEXT_AUTHOR).ifPresent { builder.author(it) }
        if (container.contains(TEXT_PAGE_LIST)) {
            val pageList = container.get(TEXT_PAGE_LIST).get() as? List<*> ?: throw InvalidDataException("Expected List")
            pageList.forEach { textView -> builder.addPage(getText(textView!!)) }
        }
        return Optional.of(builder.build())
    }

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): BookView {
        if (!value.hasMapChildren()) {
            return BookView.builder().build()
        }
        val builder = BookView.builder()
        builder.author(value.getNode(NODE_AUTHOR).getValue(TOKEN_TEXT)!!)
        builder.title(value.getNode(NODE_TITLE).getValue(TOKEN_TEXT)!!)
        builder.addPages(value.getNode(NODE_PAGES).getValue(TOKEN_TEXT_LIST)!!)
        return builder.build()
    }

    @Throws(ObjectMappingException::class)
    override fun serialize(type: TypeToken<*>, bookView: BookView?, value: ConfigurationNode) {
        if (bookView == null) {
            return
        }
        value.getNode(NODE_AUTHOR).setValue(TOKEN_TEXT, bookView.author)
        value.getNode(NODE_TITLE).setValue(TOKEN_TEXT, bookView.title)
        value.getNode(NODE_PAGES).setValue(TOKEN_TEXT_LIST, bookView.pages)
    }

    companion object {

        private const val NODE_AUTHOR = "author"
        private const val NODE_TITLE = "title"
        private const val NODE_PAGES = "pages"

        private val TOKEN_TEXT = typeTokenOf<Text>()
        private val TOKEN_TEXT_LIST = typeTokenOf<List<Text>>()
    }
}
