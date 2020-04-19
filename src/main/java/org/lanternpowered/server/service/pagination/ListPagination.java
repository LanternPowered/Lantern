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

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Pagination working with a list of values.
 */
class ListPagination extends ActivePagination {

    private final List<List<Text>> pages;

    ListPagination(MessageReceiver src, PaginationCalculator calc, List<Map.Entry<Text, Integer>> lines,
            @Nullable Text title, @Nullable Text header, @Nullable Text footer, Text padding) {
        super(src, calc, title, header, footer, padding);
        List<List<Text>> pages = new ArrayList<>();
        List<Text> currentPage = new ArrayList<>();
        int currentPageLines = 0;

        for (Map.Entry<Text, Integer> ent : lines) {
            final boolean finiteLinesPerPage  = getMaxContentLinesPerPage() > 0;
            final boolean willExceedPageLength = ent.getValue() + currentPageLines > getMaxContentLinesPerPage();
            final boolean currentPageNotEmpty = currentPageLines != 0;
            final boolean spillToNextPage = finiteLinesPerPage && willExceedPageLength && currentPageNotEmpty;
            if (spillToNextPage) {
                padPage(currentPage, currentPageLines, true);
                currentPageLines = 0;
                pages.add(currentPage);
                currentPage = new ArrayList<>();
            }
            currentPageLines += ent.getValue();
            currentPage.add(ent.getKey());
        }
        //last page is not yet committed
        final boolean lastPageNotEmpty = currentPageLines > 0;
        if (lastPageNotEmpty) {
            if (!pages.isEmpty()) {
                // Only pad if we have a previous page
                padPage(currentPage, currentPageLines, false);
            }
            pages.add(currentPage);
        }
        this.pages = pages;
    }

    @Override
    protected Iterable<Text> getLines(int page) throws CommandException {
        final int size = this.pages.size();
        if (size == 0) {
            return ImmutableList.of();
        } else if (page < 1) {
            throw new CommandException(t("Page %s does not exist!", page));
        } else if (page > size) {
            throw new CommandException(t("Page %s is greater than the max of %s!", page, size));
        }
        return this.pages.get(page - 1);
    }

    @Override
    protected boolean hasPrevious(int page) {
        return page > 1;
    }

    @Override
    protected boolean hasNext(int page) {
        return page < this.pages.size();
    }

    @Override
    protected int getTotalPages() {
        return this.pages.size();
    }
}
