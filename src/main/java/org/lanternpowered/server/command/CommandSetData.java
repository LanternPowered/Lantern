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
package org.lanternpowered.server.command;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.base.Joiner;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.persistence.DataTypeSerializer;
import org.lanternpowered.server.data.persistence.DataTypeSerializerContext;
import org.lanternpowered.server.data.persistence.json.JsonDataFormat;
import org.lanternpowered.server.game.registry.type.data.DataSerializerRegistry;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public final class CommandSetData extends CommandProvider {

    public CommandSetData() {
        super(3, "set-data");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        final ThreadLocal<Key<?>> currentKey = new ThreadLocal<>();

        specBuilder
                .arguments(
                        new CommandElement(Text.of("target")) {
                            private final CommandElement player = GenericArguments.player(Text.of("target"));
                            private final CommandElement entity = GenericArguments.entityOrTarget(Text.of("target"));

                            @Override
                            public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
                                final CommandArgs.Snapshot snapshot = args.getSnapshot();
                                try {
                                    this.entity.parse(source, args, context);
                                } catch (ArgumentParseException ex) {
                                    args.applySnapshot(snapshot);
                                    try {
                                        this.player.parse(source, args, context);
                                    } catch (ArgumentParseException ex2) {
                                        throw ex;
                                    }
                                }
                            }

                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                return this.player.complete(src, args, context);
                            }
                        },
                        new PatternMatchingCommandElement(Text.of("key")) {
                            @Override
                            protected Iterable<String> getChoices(CommandSource source) {
                                return Sponge.getGame().getRegistry().getAllOf(Key.class).stream()
                                        .map(Key::getKey).map(CatalogKey::toString).collect(Collectors.toList());
                            }

                            @Override
                            protected Object getValue(String choice) throws IllegalArgumentException {
                                final Optional<Key> ret = Sponge.getGame().getRegistry().getType(Key.class, CatalogKey.resolve(choice));
                                if (!ret.isPresent()) {
                                    throw new IllegalArgumentException("Invalid input " + choice + " was found");
                                }
                                currentKey.set(ret.get());
                                return ret.get();
                            }
                        },
                        new CommandElement(Text.of("data")) {
                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                args.next();
                                final String content = args.getRaw().substring(args.getRawPosition()).trim();
                                while (args.hasNext()) {
                                    args.next();
                                }
                                final Object data;
                                try {
                                    data = JsonDataFormat.read(content, true).orElse(null); // Don't be too strict
                                } catch (IOException e) {
                                    throw args.createError(t("Invalid json data: %s\nError: %s", content, e.getMessage()));
                                }
                                final Key key = currentKey.get();
                                final TypeToken<?> typeToken = key.getElementToken();
                                if (content.isEmpty()) {
                                    return null;
                                }
                                final DataTypeSerializer dataTypeSerializer = DataSerializerRegistry.INSTANCE
                                        .getTypeSerializer(typeToken).orElse(null);
                                if (dataTypeSerializer == null) {
                                    throw args.createError(Text.of("Unable to deserialize the data key value: {}, "
                                            + "no supported deserializer exists.", key.getKey()));
                                } else {
                                    final DataTypeSerializerContext context = DataSerializerRegistry.INSTANCE.getTypeSerializerContext();
                                    try {
                                        // Put it in a holder object, the command element separates iterable objects
                                        return new ValueHolder(dataTypeSerializer.deserialize(typeToken, context, data));
                                    } catch (InvalidDataException e) {
                                        throw args.createError(t("Invalid data: %s", e.getMessage()));
                                    }
                                }
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                return new ArrayList<>();
                            }
                        }
                )
                .executor((src, args) -> {
                    final Collection<Entity> targets = args.getAll("target");
                    final Key key = args.<Key>getOne("key").get();
                    final Object data = args.<ValueHolder>getOne("data").get().data;
                    targets.forEach(target -> target.offer(key, data));
                    src.sendMessage(t("Successfully offered the data for the key %s to the targets: %s", key.getKey(),
                            Joiner.on(",").join(targets.stream().map(entity -> entity.getTranslation().get()).collect(Collectors.toList()))));
                    return CommandResult.success();
                });
    }

    private static final class ValueHolder {

        private final Object data;

        private ValueHolder(Object data) {
            this.data = data;
        }
    }
}
