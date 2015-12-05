/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.command;

import static org.spongepowered.api.command.args.GenericArguments.optional;
import static org.spongepowered.api.command.args.GenericArguments.string;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import com.google.common.collect.Collections2;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;

import java.util.TreeSet;

public final class CommandHelp implements Command {

    private final Comparator<CommandMapping> comparator = (o1, o2) -> o1.getPrimaryAlias().compareTo(o2.getPrimaryAlias());
    private final LanternGame game;

    public CommandHelp(LanternGame game) {
        this.game = game;
    }

    @Override
    public CommandSpec build() {
        return CommandSpec
                .builder()
                .arguments(optional(string(Texts.of("command"))))
                .description(Texts.of("View a list of all commands"))
                .extendedDescription(Texts.of("View a list of all commands. Hover over\n" + " a command to view its description."
                        + " Click\n a command to insert it into your chat bar."))
                .executor((src, args) -> {
                    Optional<String> command = args.getOne("command");
                    if (command.isPresent()) {
                        Optional<? extends CommandMapping> mapping = game.getCommandManager().get(command.get());
                        if (mapping.isPresent()) {
                            CommandCallable callable = mapping.get().getCallable();
                            Optional<? extends Text> desc = callable.getHelp(src);
                            if (desc.isPresent()) {
                                src.sendMessage(desc.get());
                            } else {
                                src.sendMessage(Texts.of("Usage: /", command.get(), callable.getUsage(src)));
                            }
                            return CommandResult.success();
                        }
                        throw new CommandException(Texts.of("No such command: ", command.get()));
                    }

                    TreeSet<CommandMapping> commands = new TreeSet<CommandMapping>(comparator);
                    commands.addAll(Collections2.filter(game.getCommandManager().getAll().values(),
                            input -> input.getCallable().testPermission(src)));

                    // Console sources cannot see/use the pagination
                    boolean paginate = !(src instanceof ConsoleSource);

                    Text title = Texts.builder("Available commands:").color(TextColors.DARK_GREEN).build();
                    Collection<Text> lines = Collections2.transform(commands, input -> getDescription(src, input));

                    if (paginate) {
                        PaginationBuilder builder = game.getServiceManager().provide(PaginationService.class).get().builder();
                        builder.title(title);
                        builder.contents(lines);
                        builder.sendTo(src);
                    } else {
                        src.sendMessage(title);
                        src.sendMessages(lines);
                    }
                    return CommandResult.success();
                }).build();
    }

    private Text getDescription(CommandSource source, CommandMapping mapping) {
        @SuppressWarnings("unchecked")
        final Optional<Text> description = (Optional<Text>) mapping.getCallable().getShortDescription(source);
        TextBuilder text = Texts.builder("/" + mapping.getPrimaryAlias());
        text.color(TextColors.GREEN);
        text.style(TextStyles.UNDERLINE);
        text.onClick(TextActions.suggestCommand("/" + mapping.getPrimaryAlias()));
        Optional<? extends Text> longDescription = mapping.getCallable().getHelp(source);
        if (longDescription.isPresent()) {
            text.onHover(TextActions.showText(longDescription.get()));
        }
        return Texts.of(text, " ", description.orElse(mapping.getCallable().getUsage(source)));
    }
}
