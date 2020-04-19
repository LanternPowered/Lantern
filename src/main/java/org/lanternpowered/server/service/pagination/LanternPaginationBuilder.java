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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternPaginationBuilder implements PaginationList.Builder {

    private final LanternPaginationService service;

    @Nullable private PaginationList paginationList;
    @Nullable private Iterable<Text> contents;
    @Nullable private Text title;
    @Nullable private Text header;
    @Nullable private Text footer;

    private Text paginationSpacer = Text.of("=");
    private int linesPerPage = 20;

    LanternPaginationBuilder(LanternPaginationService service) {
        this.service = service;
    }

    @Override
    public PaginationList.Builder contents(Iterable<Text> contents) {
        checkNotNull(contents, "The contents cannot be null!");
        this.contents = contents;
        this.paginationList = null;
        return this;
    }

    @Override
    public PaginationList.Builder contents(Text... contents) {
        checkNotNull(contents, "The contents cannot be null!");
        this.contents = ImmutableList.copyOf(contents);
        this.paginationList = null;
        return this;
    }

    @Override
    public PaginationList.Builder title(@Nullable Text title) {
        this.title = title;
        this.paginationList = null;
        return this;
    }

    @Override
    public PaginationList.Builder header(@Nullable Text header) {
        this.header = header;
        this.paginationList = null;
        return this;
    }

    @Override
    public PaginationList.Builder footer(@Nullable Text footer) {
        this.footer = footer;
        this.paginationList = null;
        return this;
    }

    @Override
    public PaginationList.Builder padding(Text padding) {
        checkNotNull(padding, "The padding cannot be null!");
        this.paginationSpacer = padding;
        this.paginationList = null;
        return this;
    }

    @Override
    public PaginationList.Builder linesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        return this;
    }

    @Override
    public PaginationList build() {
        checkState(this.contents != null, "The contents of the pagination list cannot be null!");
        if (this.paginationList == null) {
            this.paginationList = new LanternPaginationList(this.service, this.contents, this.title, this.header, this.footer,
                    this.paginationSpacer, this.linesPerPage);
        }
        return this.paginationList;
    }

    @Override
    public PaginationList.Builder from(PaginationList list) {
        this.reset();
        this.contents = list.getContents();
        this.title = list.getTitle().orElse(null);
        this.header = list.getHeader().orElse(null);
        this.footer = list.getFooter().orElse(null);
        this.paginationSpacer = list.getPadding();
        this.paginationList = null;
        return this;
    }

    @Override
    public PaginationList.Builder reset() {
        this.contents = null;
        this.title = null;
        this.header = null;
        this.footer = null;
        this.paginationSpacer = Text.of("=");
        this.paginationList = null;
        return this;
    }
}

