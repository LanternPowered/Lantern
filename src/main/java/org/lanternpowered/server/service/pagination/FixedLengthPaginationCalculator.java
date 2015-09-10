package org.lanternpowered.server.service.pagination;

import org.spongepowered.api.service.pagination.PaginationCalculator;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandSource;

/**
 * A pagination calculator that has a fixed length per page.
 */
class FixedLengthPaginationCalculator implements PaginationCalculator<CommandSource> {
    private final int linesPerPage;

    public FixedLengthPaginationCalculator(int linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

    @Override
    public int getLinesPerPage(CommandSource source) {
        return this.linesPerPage;
    }

    @Override
    public int getLines(CommandSource source, Text text) {
        return 1;
    }

    @Override
    public Text center(CommandSource source, Text text, String padding) {
        return text;
    }
}
