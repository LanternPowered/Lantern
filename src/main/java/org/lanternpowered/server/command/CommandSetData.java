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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.lanternpowered.server.data.persistence.DataTypeSerializer;
import org.lanternpowered.server.data.persistence.DataTypeSerializerContext;
import org.lanternpowered.server.data.translator.JsonTranslator;
import org.lanternpowered.server.game.Lantern;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class CommandSetData extends CommandProvider {

    private static final Gson gson = new GsonBuilder().setLenient().create();

    public CommandSetData() {
        super(3, "set-data");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        final ThreadLocal<Key<?>> currentKey = new ThreadLocal<>();

        specBuilder
                .arguments(
                        GenericArguments.playerOrSource(Text.of("player")),
                        new PatternMatchingCommandElement(Text.of("key")) {
                            @Override
                            protected Iterable<String> getChoices(CommandSource source) {
                                return Sponge.getGame().getRegistry().getAllOf(Key.class).stream().map(Key::getId).collect(Collectors.toList());
                            }

                            @Override
                            protected Object getValue(String choice) throws IllegalArgumentException {
                                final Optional<Key> ret = Sponge.getGame().getRegistry().getType(Key.class, choice);
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
                                final JsonElement element;
                                try {
                                    element = gson.fromJson(content, JsonElement.class);
                                } catch (JsonParseException e) {
                                    throw args.createError(t("Invalid json data: %s\nError: %s", content, e.getMessage()));
                                }
                                final Object data = element == null ? null : JsonTranslator.fromJson(element);
                                final Key key = currentKey.get();
                                final TypeToken<?> typeToken = key.getElementToken();
                                if (content.isEmpty()) {
                                    return null;
                                }
                                final DataTypeSerializer dataTypeSerializer = Lantern.getGame().getDataManager()
                                        .getTypeSerializer(typeToken).orElse(null);
                                if (dataTypeSerializer == null) {
                                    throw args.createError(Text.of("Unable to deserialize the data key value: {}, "
                                            + "no supported deserializer exists.", key.getId()));
                                } else {
                                    final DataTypeSerializerContext context = Lantern.getGame().getDataManager().getTypeSerializerContext();
                                    try {
                                        // Put it in a holder object, the command element separates iterable objects
                                        //noinspection unchecked
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
                    final Player target = args.<Player>getOne("player").get();
                    final Key key = args.<Key>getOne("key").get();
                    final Object data = args.<ValueHolder>getOne("data").get().data;
                    //noinspection unchecked
                    target.offer(key, data);
                    src.sendMessage(t("Successfully offered the data for the key %s to the player %s", key.getId(), target.getName()));
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
