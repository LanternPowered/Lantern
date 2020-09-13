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

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.format.NamedTextColor
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.util.reference.asProvider
import org.spongepowered.api.command.exception.CommandException
import org.spongepowered.api.service.pagination.PaginationList
import java.lang.ref.WeakReference
import java.util.Optional

internal class LanternPaginationList(
        private val service: LanternPaginationService,
        private val contents: Iterable<Text>,
        private val title: Text?,
        private val header: Text?,
        private val footer: Text?,
        private val paginationSpacer: Text,
        private val linesPerPage: Int
) : PaginationList {

    override fun getContents(): Iterable<Text> = this.contents
    override fun getTitle(): Optional<Text> = this.title.asOptional()
    override fun getHeader(): Optional<Text> = this.header.asOptional()
    override fun getFooter(): Optional<Text> = this.footer.asOptional()
    override fun getPadding(): Text = this.paginationSpacer
    override fun getLinesPerPage(): Int = this.linesPerPage

    override fun sendTo(receiver: Audience, page: Int) {
        val calculator = PaginationCalculator(this.linesPerPage)
        val counts = this.contents
                .map { input -> input to calculator.getLines(input) }

        var title: Text? = this.title
        if (title != null)
            title = calculator.center(title, this.paginationSpacer)

        val source: () -> Audience? = if (receiver is Player) {
            val uniqueId = receiver.uniqueId
            { Lantern.server.getPlayer(uniqueId).orNull() }
        } else {
            WeakReference(receiver).asProvider()
        }

        val pagination = if (this.contents is List<*>) {
            ListPagination(source, calculator, counts, title,
                    this.header, this.footer, this.paginationSpacer)
        } else {
            IterablePagination(source, calculator, counts, title,
                    this.header, this.footer, this.paginationSpacer)
        }
        this.service.getPaginationState(receiver, true)!!.put(pagination)
        try {
            pagination.specificPage(page)
        } catch (e: CommandException) {
            val text = e.text
            if (text != null)
                receiver.sendMessage(text.color(NamedTextColor.RED))
        }
    }
}
