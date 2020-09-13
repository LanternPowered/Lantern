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

import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.text.LiteralText
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.api.text.format.TextDecoration
import org.lanternpowered.api.text.textOf
import org.spongepowered.api.command.exception.CommandException
import java.util.UUID

/**
 * Holds logic for an active pagination that is occurring.
 */
internal abstract class ActivePagination(
        private val source: () -> Audience?,
        private val calculator: PaginationCalculator,
        private val title: Text?,
        private val header: Text?,
        private val footer: Text?,
        private val padding: Text
) {

    companion object {
        private val SLASH_TEXT = textOf("/")
        private val DIVIDER_TEXT = textOf(" ")
        private val CONTINUATION_TEXT = textOf("...")
    }

    val id: UUID = UUID.randomUUID()

    private val nextPageText: Text
    private val previousPageText: Text

    protected var currentPage = 0
        private set

    protected val maxContentLinesPerPage: Int

    init {
        this.nextPageText = LiteralText.builder("»")
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.UNDERLINED, true)
                .clickEvent(ClickEvent.runCommand("/sponge:pagination $id next"))
                .hoverEvent(HoverEvent.showText(textOf("/page next")))
                .insertion("/sponge:page next")
                .build()
        this.previousPageText = LiteralText.builder("«")
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.UNDERLINED, true)
                .clickEvent(ClickEvent.runCommand("/sponge:pagination $id prev"))
                .hoverEvent(HoverEvent.showText(textOf("/page prev")))
                .insertion("/sponge:page prev")
                .build()

        var maxContentLinesPerPage = this.calculator.linesPerPage - 1
        if (this.title != null)
            maxContentLinesPerPage -= this.calculator.getLines(title)
        if (this.header != null)
            maxContentLinesPerPage -= this.calculator.getLines(header)
        if (this.footer != null)
            maxContentLinesPerPage -= this.calculator.getLines(footer)
        this.maxContentLinesPerPage = maxContentLinesPerPage
    }

    protected abstract fun getLines(page: Int): Iterable<Text>
    protected abstract fun hasPrevious(page: Int): Boolean
    protected abstract fun hasNext(page: Int): Boolean
    protected abstract val totalPages: Int

    fun nextPage() {
        this.specificPage(this.currentPage + 1)
    }

    open fun previousPage() {
        this.specificPage(this.currentPage - 1)
    }

    fun currentPage() {
        this.specificPage(this.currentPage)
    }

    fun specificPage(page: Int) {
        val src = this.source() ?: throw CommandException(textOf("Source for pagination $id is no longer active!"))
        this.currentPage = page
        val toSend = ArrayList<Text>()
        if (this.title != null)
            toSend.add(this.title)
        if (this.header != null)
            toSend.add(this.header)
        for (line in this.getLines(page))
            toSend.add(line)
        val footer = this.calculateFooter(page)
        toSend.add(this.calculator.center(footer, this.padding))
        if (this.footer != null)
            toSend.add(this.footer)
        for (line in toSend)
            src.sendMessage(line)
    }

    protected fun calculateFooter(currentPage: Int): Text {
        val hasPrevious = hasPrevious(currentPage)
        val hasNext = hasNext(currentPage)
        val result = LiteralText.builder()
        if (hasPrevious) {
            result.append(this.previousPageText).append(DIVIDER_TEXT)
        } else {
            result.append(textOf("«")).append(DIVIDER_TEXT)
        }
        var needsDivider = false
        val totalPages = this.totalPages
        if (totalPages > 1) {
            result.append(LiteralText.builder()
                    .content(currentPage.toString())
                    .clickEvent(ClickEvent.runCommand("/sponge:pagination $id $currentPage"))
                    .hoverEvent(HoverEvent.showText(textOf("/page $currentPage")))
                    .insertion("/sponge:page $currentPage"))
            result.append(SLASH_TEXT)
            result.append(LiteralText.builder()
                    .content(currentPage.toString())
                    .clickEvent(ClickEvent.runCommand("/sponge:pagination $id $totalPages"))
                    .hoverEvent(HoverEvent.showText(textOf("/page $totalPages")))
                    .insertion("/sponge:page $totalPages"))
            needsDivider = true
        }
        if (hasNext) {
            if (needsDivider)
                result.append(DIVIDER_TEXT)
            result.append(this.nextPageText)
        } else {
            if (needsDivider)
                result.append(DIVIDER_TEXT)
            result.append(textOf("»"))
        }
        result.color(this.padding.color())
        if (this.title != null)
            result.style(this.title.style())
        return result.build()
    }

    protected fun padPage(currentPage: MutableList<Text>, currentPageLines: Int, addContinuation: Boolean) {
        val maxContentLinesPerPage = this.maxContentLinesPerPage
        for (i in currentPageLines until maxContentLinesPerPage) {
            if (addContinuation && i == maxContentLinesPerPage - 1) {
                currentPage.add(CONTINUATION_TEXT)
            } else {
                currentPage.add(emptyText())
            }
        }
    }
}
