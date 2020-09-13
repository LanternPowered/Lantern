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
package org.lanternpowered.server.service.pagination

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.api.service.pagination.PaginationList

class LanternPaginationBuilder internal constructor(
        private val service: LanternPaginationService
) : PaginationList.Builder {

    companion object {

        val DefaultSpacer = textOf("=")
    }

    private var paginationList: PaginationList? = null
    private var contents: Iterable<Text>? = null
    private var title: Text? = null
    private var header: Text? = null
    private var footer: Text? = null
    private var padding: Text = DefaultSpacer
    private var linesPerPage = 20

    override fun contents(contents: Iterable<Text>): PaginationList.Builder = this.apply {
        this.contents = contents
        this.paginationList = null
    }

    override fun contents(vararg contents: Text): PaginationList.Builder = this.apply {
        this.contents = contents.toImmutableList()
        this.paginationList = null
    }

    override fun title(title: Text?): PaginationList.Builder = this.apply {
        this.title = title
        this.paginationList = null
    }

    override fun header(header: Text?): PaginationList.Builder = this.apply {
        this.header = header
        this.paginationList = null
    }

    override fun footer(footer: Text?): PaginationList.Builder = this.apply {
        this.footer = footer
        this.paginationList = null
    }

    override fun padding(padding: Text): PaginationList.Builder = this.apply {
        this.padding = padding
        this.paginationList = null
    }

    override fun linesPerPage(linesPerPage: Int): PaginationList.Builder = this.apply {
        this.linesPerPage = linesPerPage
        this.paginationList = null
    }

    override fun build(): PaginationList {
        val contents = checkNotNull(this.contents) { "The contents of the pagination list must be set!" }
        if (this.paginationList == null) {
            this.paginationList = LanternPaginationList(this.service, contents,
                    this.title, this.header, this.footer, this.padding, this.linesPerPage)
        }
        return this.paginationList!!
    }

    override fun from(list: PaginationList): PaginationList.Builder = this.apply {
        this.reset()
        this.contents = list.contents
        this.title = list.title.orElse(null)
        this.header = list.header.orElse(null)
        this.footer = list.footer.orElse(null)
        this.padding = list.padding
        this.paginationList = null
    }

    override fun reset(): PaginationList.Builder = this.apply {
        this.contents = null
        this.title = null
        this.header = null
        this.footer = null
        this.padding = DefaultSpacer
        this.paginationList = null
    }
}
