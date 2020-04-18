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
package org.lanternpowered.server.text.book

import com.google.common.collect.ImmutableList
import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.Queries
import org.spongepowered.api.text.BookView
import org.spongepowered.api.text.Text

data class LanternBookView(
        internal val title: Text,
        internal val author: Text,
        internal val pages: ImmutableList<Text>
) : BookView {

    override fun getTitle() = this.title
    override fun getAuthor() = this.author
    override fun getPages() = this.pages
    override fun getContentVersion() = 1

    override fun toContainer(): DataContainer = DataContainer.createNew()
            .set(Queries.CONTENT_VERSION, this.contentVersion)
            .set(Queries.TEXT_TITLE, this.title.toContainer())
            .set(Queries.TEXT_AUTHOR, this.author.toContainer())
            .set(Queries.TEXT_PAGE_LIST, this.pages.map { it.toContainer() }.toList())
}

class LanternBookViewBuilder : BookView.Builder {

    private var title: Text = Text.empty()
    private var author: Text = Text.empty()
    private var pages: MutableList<Text> = ArrayList()

    override fun title(title: Text) = apply { this.title = title }
    override fun author(author: Text) = apply { this.author = author }

    override fun addPage(page: Text) = apply { this.pages.add(page) }
    override fun addPages(pages: MutableCollection<Text>) = apply { this.pages.addAll(pages) }
    override fun addPages(vararg pages: Text) = apply { this.pages.addAll(pages.asList()) }

    override fun insertPage(i: Int, page: Text) = apply { this.pages.add(i, page) }
    override fun insertPages(i: Int, pages: MutableCollection<Text>) = apply { this.pages.addAll(i, pages) }
    override fun insertPages(i: Int, vararg pages: Text) = apply { this.pages.addAll(i, pages.asList()) }

    override fun removePage(page: Text) = apply { this.pages.remove(page) }
    override fun removePage(i: Int) = apply { this.pages.removeAt(i) }
    override fun removePages(pages: MutableCollection<Text>) = apply { this.pages.removeAll(pages) }
    override fun removePages(vararg pages: Text) = apply { this.pages.removeAll(pages.asList()) }

    override fun clearPages() = apply { this.pages.clear() }

    override fun from(value: BookView) = apply {
        value as LanternBookView
        this.title = value.title
        this.author = value.author
        this.pages = ArrayList(value.pages)
    }

    override fun reset() = apply {
        this.title = Text.empty()
        this.author = Text.empty()
        this.pages.clear()
    }

    override fun build() = LanternBookView(this.title, this.author, this.pages.toImmutableList())
}
