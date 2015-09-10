package org.lanternpowered.server.service.pagination;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.spongepowered.api.service.pagination.PaginationCalculator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Holds logic for an active pagination that is occurring.
 */
abstract class ActivePagination {

    private static final Text SLASH_TEXT = Texts.of("/");
    private static final Text DIVIDER_TEXT = Texts.of(" ");
    private final WeakReference<CommandSource> src;
    private final UUID id = UUID.randomUUID();
    private final Text nextPageText;
    private final Text prevPageText;
    private final Text title;
    private final Text header;
    private final Text footer;
    private int currentPage;
    private final int maxContentLinesPerPage;
    protected final PaginationCalculator<CommandSource> calc;
    private final String padding;

    public ActivePagination(CommandSource src, PaginationCalculator<CommandSource> calc, Text title,
            Text header, Text footer, String padding) {
        this.src = new WeakReference<CommandSource>(src);
        this.calc = calc;
        this.title = title;
        this.header = header;
        this.footer = footer;
        this.padding = padding;
        this.nextPageText = Texts.of("»").builder()
                .color(TextColors.BLUE)
                .style(TextStyles.UNDERLINE)
                .onClick(TextActions.runCommand("/pagination " + this.id.toString() + " next")).build();
        this.prevPageText = Texts.of("«").builder()
                .color(TextColors.BLUE)
                .style(TextStyles.UNDERLINE)
                .onClick(TextActions.runCommand("/pagination " + this.id.toString() + " prev")).build();
        int maxContentLinesPerPage = calc.getLinesPerPage(src) - 1;
        if (title != null) {
            maxContentLinesPerPage -= calc.getLines(src, title);
        }
        if (header != null) {
            maxContentLinesPerPage -= calc.getLines(src, header);
        }
        if (footer != null) {
            maxContentLinesPerPage -= calc.getLines(src, footer);
        }
        this.maxContentLinesPerPage = maxContentLinesPerPage;

    }

    public UUID getId() {
        return this.id;
    }

    protected abstract Iterable<Text> getLines(int page) throws CommandException;

    protected abstract boolean hasPrevious(int page);

    protected abstract boolean hasNext(int page);

    protected abstract int getTotalPages();

    public void nextPage() throws CommandException {
        specificPage(this.currentPage + 1);
    }

    public void previousPage() throws CommandException {
        specificPage(this.currentPage - 1);
    }

    public void currentPage() throws CommandException {
        specificPage(this.currentPage);
    }

    protected int getCurrentPage() {
        return this.currentPage;
    }

    protected int getMaxContentLinesPerPage() {
        return this.maxContentLinesPerPage;
    }

    public void specificPage(int page) throws CommandException {
        CommandSource src = this.src.get();
        if (src == null) {
            throw new CommandException(t("Source for pagination %s is no longer active!", getId()));
        }
        this.currentPage = page;

        List<Text> toSend = new ArrayList<Text>();
        Text title = this.title;
        if (title != null) {
            toSend.add(title);
        }
        Text header = this.header;
        if (header != null) {
            toSend.add(header);
        }

        for (Text line : getLines(page)) {
            toSend.add(line);
        }

        Text footer = calculateFooter(page);
        if (footer != null) {
            toSend.add(this.calc.center(src, footer, this.padding));
        }
        if (this.footer != null) {
            toSend.add(this.footer);
        }
        src.sendMessage(toSend);
    }

    protected Text calculateFooter(int currentPage) {
        boolean hasPrevious = hasPrevious(currentPage);
        boolean hasNext = hasNext(currentPage);

        TextBuilder ret = Texts.builder();
        if (hasPrevious) {
            ret.append(this.prevPageText).append(DIVIDER_TEXT);
        }
        boolean needsDiv = false;
        int totalPages = getTotalPages();
        if (totalPages > 1) {
            ret.append(Texts.of(currentPage)).append(SLASH_TEXT).append(Texts.of(totalPages));
            needsDiv = true;
        }
        if (hasNext) {
            if (needsDiv) {
                ret.append(DIVIDER_TEXT);
            }
            ret.append(this.nextPageText);
        }
        if (this.title != null) {
            ret.color(this.title.getColor());
            ret.style(this.title.getStyle());
        }
        return ret.build();
    }
}
