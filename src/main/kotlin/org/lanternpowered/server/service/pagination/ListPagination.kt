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

import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.spongepowered.api.command.exception.CommandException

/**
 * Pagination working with a list of values.
 */
internal class ListPagination(
        source: () -> Audience?,
        calculator: PaginationCalculator,
        lines: List<Pair<Text, Int>>,
        title: Text?,
        header: Text?,
        footer: Text?,
        padding: Text
) : ActivePagination(source, calculator, title, header, footer, padding) {

    private val pages: List<List<Text>>

    override val totalPages: Int
        get() = this.pages.size

    init {
        val pages = ArrayList<List<Text>>()
        var currentPage = ArrayList<Text>()
        var currentPageLines = 0
        for ((key, value) in lines) {
            val finiteLinesPerPage = this.maxContentLinesPerPage > 0
            val willExceedPageLength = value + currentPageLines > this.maxContentLinesPerPage
            val currentPageNotEmpty = currentPageLines != 0
            val spillToNextPage = finiteLinesPerPage && willExceedPageLength && currentPageNotEmpty
            if (spillToNextPage) {
                this.padPage(currentPage, currentPageLines, true)
                currentPageLines = 0
                pages.add(currentPage)
                currentPage = ArrayList()
            }
            currentPageLines += value
            currentPage.add(key)
        }
        // last page is not yet committed
        val lastPageNotEmpty = currentPageLines > 0
        if (lastPageNotEmpty) {
            if (pages.isNotEmpty()) {
                // Only pad if we have a previous page
                this.padPage(currentPage, currentPageLines, false)
            }
            pages.add(currentPage)
        }
        this.pages = pages
    }

    override fun getLines(page: Int): Iterable<Text> {
        val size = this.pages.size
        return when {
            size == 0 -> emptyList()
            page < 1 -> throw CommandException(textOf("Page $page does not exist!"))
            page > size -> throw CommandException(textOf("Page $page is greater than the max of $size!"))
            else -> this.pages[page - 1]
        }
    }

    override fun hasPrevious(page: Int): Boolean = page > 1
    override fun hasNext(page: Int): Boolean = page < this.pages.size
}
