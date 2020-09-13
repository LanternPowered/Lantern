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

import com.google.common.collect.Iterators
import com.google.common.collect.PeekingIterator
import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.textOf
import org.spongepowered.api.command.exception.CommandException

/**
 * Pagination occurring for an iterable -- we don't know its size.
 */
internal class IterablePagination(
        src: () -> Audience?,
        calculator: PaginationCalculator,
        counts: Iterable<Pair<Text, Int>>,
        title: Text?,
        header: Text?,
        footer: Text?,
        padding: Text
) : ActivePagination(src, calculator, title, header, footer, padding) {

    private val countIterator: PeekingIterator<Pair<Text, Int>> = Iterators.peekingIterator(counts.iterator())
    private var lastPage = 0

    override val totalPages: Int
        get() = -1

    override fun getLines(page: Int): Iterable<Text> {
        if (!this.countIterator.hasNext())
            throw CommandException(textOf("You're already at the end of the pagination list iterator."))
        if (page < 1)
            throw CommandException(textOf("Page $page does not exist!"))
        if (page <= this.lastPage) {
            throw CommandException(textOf("You cannot go to previous pages in an iterable pagination."))
        } else if (page > this.lastPage + 1) {
            this.getLines(page - 1)
        }
        this.lastPage = page
        if (this.maxContentLinesPerPage <= 0)
            return this.countIterator.asSequence().map { entry -> entry.first }.asIterable()
        val result = ArrayList<Text>(this.maxContentLinesPerPage)
        var addedLines = 0
        while (addedLines <= this.maxContentLinesPerPage) {
            if (!this.countIterator.hasNext()) {
                // Pad the last page, but only if it isn't the first.
                if (page > 1)
                    this.padPage(result, addedLines, false)
                break
            }
            if (addedLines + this.countIterator.peek().second > this.maxContentLinesPerPage) {
                // Add the continuation marker, pad if required
                this.padPage(result, addedLines, true)
                break
            }
            val entry = this.countIterator.next()
            result.add(entry.first)
            addedLines += entry.second
        }
        return result
    }

    override fun hasPrevious(page: Int): Boolean = false

    override fun hasNext(page: Int): Boolean =
            page == this.currentPage && this.countIterator.hasNext()

    override fun previousPage(): Unit =
            throw CommandException(textOf("You cannot go to previous pages in an iterable pagination."))
}
