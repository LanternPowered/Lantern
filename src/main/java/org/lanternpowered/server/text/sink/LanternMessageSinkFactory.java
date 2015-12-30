/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.text.sink;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.Sets2;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinkFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternMessageSinkFactory implements MessageSinkFactory {

    private static class PermissionSink extends MessageSink {

        private final String permission;

        private PermissionSink(String permission) {
            this.permission = permission;
        }

        @Override
        public Iterable<CommandSource> getRecipients() {
            PermissionService service =  LanternGame.get().getServiceManager().provideUnchecked(PermissionService.class);
            return Iterables.concat(Iterables.transform(service.getKnownSubjects().values(), new Function<SubjectCollection, Iterable<CommandSource>>() {
                @Nullable
                @Override
                public Iterable<CommandSource> apply(SubjectCollection input) {
                    return Iterables.filter(Iterables.transform(Maps.filterValues(input.getAllWithPermission(PermissionSink.this.permission),
                                    Predicates.equalTo(true)).keySet(), func -> func.getCommandSource().orElse(null)), Predicates.notNull());
                }
            }));
        }
    }

    @Override
    public MessageSink toPermission(String permission) {
        return new PermissionSink(checkNotNull(permission, "permission"));
    }

    public static final MessageSink TO_ALL = new MessageSink() {
        @Override
        public Iterable<CommandSource> getRecipients() {
            return ImmutableSet.<CommandSource>builder()
                    .add(LanternGame.get().getServer().getConsole())
                    .addAll(LanternGame.get().getServer().getOnlinePlayers())
                    .build();
        }
    };

    public static final MessageSink TO_ALL_PLAYERS = new MessageSink() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public Iterable<CommandSource> getRecipients() {
            return (Collection) LanternGame.get().getServer().getOnlinePlayers();
        }
    };

    public static final MessageSink TO_NONE = new MessageSink() {
        @Override
        public Iterable<CommandSource> getRecipients() {
            return ImmutableSet.of();
        }
    };

    @Override
    public MessageSink toAllPlayers() {
        return TO_ALL_PLAYERS;
    }

    @Override
    public MessageSink toNone() {
        return TO_NONE;
    }

    @Override
    public MessageSink toAll() {
        return TO_ALL;
    }

    private static class CombinedSink extends MessageSink {

        private final Iterable<MessageSink> contents;

        private CombinedSink(Iterable<MessageSink> contents) {
            this.contents = contents;
        }

        @Override
        public Text transformMessage(CommandSource target, Text text) {
            Text ret = text;
            for (MessageSink sink : this.contents) {
                Text xformed = sink.transformMessage(target, ret);
                if (xformed != null) {
                    ret = xformed;
                }
            }
            return ret;
        }

        @Override
        public Iterable<CommandSource> getRecipients() {
            return ImmutableSet.copyOf(Iterables.concat(Iterables.transform(this.contents,
                    (MessageSink sink) -> sink == null ? Collections.<CommandSource>emptyList() : sink.getRecipients())));
        }
    }

    @Override
    public MessageSink combined(MessageSink... sinks) {
        return new CombinedSink(ImmutableList.copyOf(sinks));
    }

    private static class FixedSink extends MessageSink {

        private final Set<CommandSource> contents;

        private FixedSink(Set<CommandSource> provided) {
            this.contents = Collections.unmodifiableSet(Sets2.newWeakHashSet(provided));
        }

        @Override
        public Iterable<CommandSource> getRecipients() {
            return this.contents;
        }
    }

    @Override
    public MessageSink to(Set<CommandSource> sources) {
        return new FixedSink(sources);
    }
}
