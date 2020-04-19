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
package org.lanternpowered.server.service.pagination;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Pagination occurring for an iterable -- we don't know its size.
 */
final class IterablePagination extends ActivePagination {

    private final PeekingIterator<Map.Entry<Text, Integer>> countIterator;
    private int lastPage;

    IterablePagination(MessageReceiver src, PaginationCalculator calc, Iterable<Map.Entry<Text, Integer>> counts, @Nullable Text title,
            @Nullable Text header, @Nullable Text footer, Text padding) {
        super(src, calc, title, header, footer, padding);
        this.countIterator = Iterators.peekingIterator(counts.iterator());
    }

    @Override
    protected Iterable<Text> getLines(int page) throws CommandException {
        if (!this.countIterator.hasNext()) {
            throw new CommandException(t("You're already at the end of the pagination list iterator."));
        }

        if (page < 1) {
            throw new CommandException(t("Page %s does not exist!", page));
        }

        if (page <= this.lastPage) {
            throw new CommandException(t("You cannot go to previous pages in an iterable pagination."));
        } else if (page > this.lastPage + 1) {
            getLines(page - 1);
        }
        this.lastPage = page;

        if (getMaxContentLinesPerPage() <= 0) {
            return Lists.newArrayList(Iterators.transform(this.countIterator, Map.Entry::getKey));
        }

        final List<Text> ret = new ArrayList<>(getMaxContentLinesPerPage());
        int addedLines = 0;
        while (addedLines <= getMaxContentLinesPerPage()) {
            if (!this.countIterator.hasNext()) {
                // Pad the last page, but only if it isn't the first.
                if (page > 1) {
                    padPage(ret, addedLines, false);
                }
                break;
            }
            if (addedLines + this.countIterator.peek().getValue() > getMaxContentLinesPerPage()) {
                // Add the continuation marker, pad if required
                padPage(ret, addedLines, true);
                break;
            }
            final Map.Entry<Text, Integer> ent = this.countIterator.next();
            ret.add(ent.getKey());
            addedLines += ent.getValue();
        }
        return ret;
    }

    @Override
    protected boolean hasPrevious(int page) {
        return false;
    }

    @Override
    protected boolean hasNext(int page) {
        return page == getCurrentPage() && this.countIterator.hasNext();
    }

    @Override
    protected int getTotalPages() {
        return -1;
    }

    @Override
    public void previousPage() throws CommandException {
        throw new CommandException(t("You cannot go to previous pages in an iterable pagination."));
    }
}
