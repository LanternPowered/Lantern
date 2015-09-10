package org.lanternpowered.server.service.pagination;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

import org.spongepowered.api.service.pagination.PaginationCalculator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Pagination occurring for an iterable -- we don't know its size.
 */
class IterablePagination extends ActivePagination {

    private final PeekingIterator<Map.Entry<Text, Integer>> countIterator;
    private int lastPage;

    public IterablePagination(CommandSource src, PaginationCalculator<CommandSource> calc, Iterable<Map.Entry<Text, Integer>> counts, Text title,
            Text header, Text footer, String padding) {
        super(src, calc, title, header, footer, padding);
        this.countIterator = Iterators.peekingIterator(counts.iterator());
    }

    @Override
    protected Iterable<Text> getLines(int page) throws CommandException {
        if (!this.countIterator.hasNext()) {
            throw new CommandException(Texts.of("Already at end of iterator"));
        }

        if (page <= this.lastPage) {
            throw new CommandException(Texts.of("Cannot go backward in an IterablePagination"));
        } else if (page > this.lastPage + 1) {
            getLines(page - 1);
        }
        this.lastPage = page;

        if (getMaxContentLinesPerPage() <= 0) {
            return Lists.newArrayList(Iterators.transform(this.countIterator, new Function<Map.Entry<Text, Integer>, Text>() {

                @Nullable
                @Override
                public Text apply(Map.Entry<Text, Integer> input) {
                    return input.getKey();
                }
            }));
        }

        List<Text> ret = new ArrayList<Text>(getMaxContentLinesPerPage());
        int addedLines = 0;
        while (addedLines <= getMaxContentLinesPerPage()) {
            if (!this.countIterator.hasNext()) {
                break;
            }
            if (addedLines + this.countIterator.peek().getValue() > getMaxContentLinesPerPage()) {
                break;
            }
            Map.Entry<Text, Integer> ent = this.countIterator.next();
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
        throw new CommandException(Texts.of("Cannot go backwards in a streaming pagination"));
    }
}
