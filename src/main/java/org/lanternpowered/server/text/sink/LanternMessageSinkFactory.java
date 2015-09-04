package org.lanternpowered.server.text.sink;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinkFactory;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

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
                                    Predicates.equalTo(true)).keySet(), new Function<Subject, CommandSource>() {
                                @Nullable
                                @Override
                                public CommandSource apply(@Nullable Subject input) {
                                    return input.getCommandSource().orNull();
                                }
                            }), Predicates.notNull());
                }
            }));
        }
    }

    @Override
    public MessageSink toPermission(String permission) {
        checkNotNull(permission, "permission");
        return new PermissionSink(permission);
    }

    public static final MessageSink TO_ALL = new MessageSink() {
        @Override
        public Iterable<CommandSource> getRecipients() {
            Set<CommandSource> ret = new HashSet<>(LanternGame.get().getServer().getOnlinePlayers());
            ret.add(LanternGame.get().getServer().getConsole());
            return ret;
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
            return Collections.emptyList();
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
            return ImmutableSet.copyOf(Iterables.concat(Iterables.transform(this.contents, new Function<MessageSink, Iterable<CommandSource>>() {
                @Nullable
                @Override
                public Iterable<CommandSource> apply(@Nullable MessageSink input) {
                    return input.getRecipients();
                }
            })));
        }
    }

    @Override
    public MessageSink combined(MessageSink... sinks) {
        return new CombinedSink(ImmutableList.copyOf(sinks));
    }

    private static class FixedSink extends MessageSink {
        private final Set<CommandSource> contents;

        private FixedSink(Set<CommandSource> provided) {
            Set<CommandSource> contents = Collections.newSetFromMap(new WeakHashMap<CommandSource, Boolean>());
            contents.addAll(provided);
            this.contents = Collections.unmodifiableSet(contents);
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