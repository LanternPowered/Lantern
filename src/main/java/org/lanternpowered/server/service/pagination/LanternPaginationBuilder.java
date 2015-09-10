package org.lanternpowered.server.service.pagination;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.util.command.CommandMessageFormatting.error;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationCalculator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ProxySource;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

class LanternPaginationBuilder implements PaginationBuilder {
    private final LanternPaginationService service;
    private Iterable<Text> contents;
    private Text title;
    private Text header;
    private Text footer;
    private String paginationSpacer = "=";

    public LanternPaginationBuilder(LanternPaginationService service) {
        this.service = service;
    }

    @Override
    public PaginationBuilder contents(Iterable<Text> contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public PaginationBuilder contents(Text... contents) {
        this.contents = ImmutableList.copyOf(contents);
        return this;
    }

    @Override
    public PaginationBuilder title(Text title) {
        this.title = title;
        return this;
    }

    @Override
    public PaginationBuilder header(Text header) {
        this.header = header;
        return this;
    }

    @Override
    public PaginationBuilder footer(Text footer) {
        this.footer = footer;
        return this;
    }

    @Override
    public PaginationBuilder paddingString(String padding) {
        this.paginationSpacer = padding;
        return this;
    }

    @Override
    public void sendTo(final CommandSource source) {
        checkNotNull(this.contents, "contents");
        checkNotNull(source, "source");
        this.service.registerCommandOnce();

        CommandSource realSource = source;
        while (realSource instanceof ProxySource) {
            realSource = ((ProxySource)realSource).getOriginalSource();
        }
        @SuppressWarnings("unchecked")
        PaginationCalculator<CommandSource> calculator = (PaginationCalculator) this.service.calculators.get(realSource.getClass());
        if (calculator == null) {
            calculator = this.service.getUnpaginatedCalculator(); // TODO: or like 50 lines?
        }
        final PaginationCalculator<CommandSource> finalCalculator = calculator;
        Iterable<Map.Entry<Text, Integer>> counts = Iterables.transform(this.contents, new Function<Text, Map.Entry<Text, Integer>>() {
            @Nullable
            @Override
            public Map.Entry<Text, Integer> apply(@Nullable Text input) {
                int lines = finalCalculator.getLines(source, input);
                return Maps.immutableEntry(input, lines);
            }
        });

        Text title = this.title;
        if (title != null) {
            title = calculator.center(source, title, this.paginationSpacer);
        }

        ActivePagination pagination;
        if (this.contents instanceof List) { // If it started out as a list, it's probably reasonable to copy it to another list
            pagination = new ListPagination(source, calculator, ImmutableList.copyOf(counts), title, this.header, this.footer, this.paginationSpacer);
        } else {
            pagination = new IterablePagination(source, calculator, counts, title, this.header, this.footer, this.paginationSpacer);
        }

        this.service.getPaginationState(source, true).put(pagination);
        try {
            pagination.nextPage();
        } catch (CommandException e) {
            source.sendMessage(error(e.getText()));
        }

    }
}
