/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.service.pagination;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;
import static org.spongepowered.api.command.args.GenericArguments.integer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

public class LanternPaginationService implements PaginationService {

    static class SourcePaginations {
        private final Map<UUID, ActivePagination> paginations = new ConcurrentHashMap<>();
        private volatile UUID lastUuid;

        public ActivePagination get(UUID uuid) {
            return this.paginations.get(uuid);
        }

        public void put(ActivePagination pagination) {
            synchronized (this.paginations) {
                this.paginations.put(pagination.getId(), pagination);
                this.lastUuid = pagination.getId();
            }
        }

        public Set<UUID> keys() {
            return this.paginations.keySet();
        }

        public UUID getLastUuid() {
            return this.lastUuid;
        }
    }
    final ConcurrentMap<MessageReceiver, SourcePaginations> activePaginations = new MapMaker().weakKeys().makeMap();
    private final AtomicBoolean commandRegistered = new AtomicBoolean();

    void registerCommandOnce() {
        if (this.commandRegistered.compareAndSet(false, true)) {
            Sponge.getGame().getCommandManager().register(Lantern.getMinecraftPlugin(), CommandSpec.builder()
                    .description(t("Helper command for paginations occurring"))
                    .arguments(new ActivePaginationCommandElement(t("pagination-id")))
                    .child(CommandSpec.builder()
                            .description(t("Go to the next page"))
                            .executor((src, args) -> {
                                args.<ActivePagination>getOne("pagination-id").get().nextPage();
                                return CommandResult.success();
                            }).build(), "next", "n")
                    .child(CommandSpec.builder()
                            .description(t("Go to the previous page"))
                            .executor((src, args) -> {
                                args.<ActivePagination>getOne("pagination-id").get().previousPage();
                                return CommandResult.success();
                            }).build(), "previous", "prev", "p")
                    .child(CommandSpec.builder()
                            .description(t("Go to a specific page"))
                            .arguments(integer(t("page")))
                            .executor((src, args) -> {
                                args.<ActivePagination>getOne("pagination-id").get().specificPage(args.<Integer>getOne("page").get());
                                return CommandResult.success();
                            }).build(), "page")
                    .build(), "pagination", "page");
        }

    }

    @Override
    public PaginationList.Builder builder() {
        return new LanternPaginationBuilder(this);
    }

    SourcePaginations getPaginationState(MessageReceiver source, boolean create) {
        SourcePaginations ret = this.activePaginations.get(source);
        if (ret == null && create) {
            ret = new SourcePaginations();
            SourcePaginations existing = this.activePaginations.putIfAbsent(source, ret);
            if (existing != null) {
                ret = existing;
            }
        }
        return ret;
    }

    private class ActivePaginationCommandElement extends CommandElement {

        protected ActivePaginationCommandElement(@Nullable Text key) {
            super(key);
        }

        @Nullable
        @Override
        protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
            UUID id;
            SourcePaginations paginations = getPaginationState(source, false);
            if (paginations == null) {
                throw args.createError(t("Source %s has no paginations!", source));
            }

            Object state = args.getState();
            try {
                id = UUID.fromString(args.next());
            } catch (IllegalArgumentException ex) { // TODO: Just use last valid input?
                if (paginations.getLastUuid() != null) {
                    args.setState(state);
                    return paginations.get(paginations.getLastUuid());
                } else {
                    throw args.createError(t("Input was not a valid UUID!"));
                }
            }
            ActivePagination pagination = paginations.get(id);
            if (pagination == null) {
                throw args.createError(t("No pagination registered for id %s", id.toString()));
            }
            return paginations.get(id);
        }

        @Override
        public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
            SourcePaginations paginations = getPaginationState(src, false);
            if (paginations == null) {
                return ImmutableList.of();
            }

            final Optional<String> optNext = args.nextIfPresent();
            if (optNext.isPresent()) {
                return paginations.keys().stream()
                        .map(Object::toString)
                        .filter(new StartsWithPredicate(optNext.get()))
                        .collect(ImmutableList.toImmutableList());
            } else {
                return ImmutableList.copyOf(Iterables.transform(paginations.keys(), Object::toString));
            }
        }
    }
}
