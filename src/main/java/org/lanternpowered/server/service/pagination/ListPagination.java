package org.lanternpowered.server.service.pagination;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.spongepowered.api.service.pagination.PaginationCalculator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pagination working with a list of values.
 */
class ListPagination extends ActivePagination {
    private final List<List<Text>> pages;

    public ListPagination(CommandSource src, PaginationCalculator<CommandSource> calc, List<Map.Entry<Text, Integer>> lines,
            Text title, Text header, Text footer, String padding) {
        super(src, calc, title, header, footer, padding);
        List<List<Text>> pages = new ArrayList<List<Text>>();
        List<Text> currentPage = new ArrayList<Text>();
        int currentPageLines = 0;

        for (Map.Entry<Text, Integer> ent : lines) {
            if (getMaxContentLinesPerPage() > 0 && ent.getValue() + currentPageLines > getMaxContentLinesPerPage() && currentPageLines != 0) {
                currentPageLines = 0;
                pages.add(currentPage);
                currentPage = new ArrayList<Text>();
            }
            currentPageLines += ent.getValue();
            currentPage.add(ent.getKey());
        }
        if (currentPageLines > 0) {
            pages.add(currentPage);
        }
        this.pages = pages;
    }

    @Override
    protected Iterable<Text> getLines(int page) throws CommandException {
        if (page < 1) {
            throw new CommandException(t("Page %s does not exist!", page));
        } else if (page > this.pages.size()) {
            throw new CommandException(t("Page %s is too high", page));
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
