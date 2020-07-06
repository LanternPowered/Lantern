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
package org.lanternpowered.server.command;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.google.common.collect.Collections2;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CommandHelp extends CommandProvider {

    public CommandHelp() {
        super(0, "help", "?");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        final Comparator<CommandMapping> comparator = Comparator.comparing(CommandMapping::getPrimaryAlias);
        specBuilder
                .arguments(
                        GenericArguments.optional(new CommandElement(Text.of("command")) {
                            @Nullable
                            @Override
                            protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
                                return args.next();
                            }

                            @Override
                            public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
                                final String nextArg = args.nextIfPresent().orElse("");
                                return Lantern.getGame().getCommandManager().getAliases().stream()
                                        .filter(new StartsWithPredicate(nextArg))
                                        .collect(Collectors.toList());
                            }
                        })
                )
                .description(Text.of("View a list of all commands"))
                .extendedDescription(Text.of(
                        "View a list of all commands. Hover over\n" +
                        " a command to view its description. Click\n" +
                        " a command to insert it into your chat bar."))
                .executor((src, args) -> {
                    Optional<String> command = args.getOne("command");
                    if (command.isPresent()) {
                        Optional<? extends CommandMapping> mapping = Sponge.getCommandManager().get(command.get());
                        if (mapping.isPresent()) {
                            CommandCallable callable = mapping.get().getCallable();
                            Optional<? extends Text> desc;
                            // Format the command spec differently, lets include the actual
                            // command name in the usage message
                            if (callable instanceof CommandSpec) {
                                Text.Builder builder = Text.builder();
                                callable.getShortDescription(src).ifPresent(des -> builder.append(des, Text.newLine()));
                                builder.append(t("commands.generic.usage", t("/%s %s", command.get(), callable.getUsage(src))));
                                final Optional<Text> extendedDescription = ((CommandSpec) callable).getExtendedDescription(src);
                                extendedDescription.ifPresent(text -> builder.append(Text.newLine(), text));
                                src.sendMessage(builder.build());
                            } else if ((desc = callable.getHelp(src)).isPresent()) {
                                src.sendMessage(desc.get());
                            } else {
                                src.sendMessage(t("commands.generic.usage", t("/%s %s", command.get(), callable.getUsage(src))));
                            }
                            return CommandResult.success();
                        }
                        throw new CommandException(Text.of("No such command: ", command.get()));
                    }

                    Lantern.getAsyncScheduler().submit(() -> {
                        TreeSet<CommandMapping> commands = new TreeSet<>(comparator);
                        commands.addAll(Collections2.filter(Sponge.getCommandManager().getAll().values(),
                                input -> input.getCallable().testPermission(src)));


                        final Text title = Text.builder("Available commands:").color(TextColors.DARK_GREEN).build();
                        final List<Text> lines = commands.stream()
                                .map(c -> getDescription(src, c))
                                .collect(Collectors.toList());

                        // Console sources cannot see/use the pagination
                        if (!(src instanceof ConsoleSource)) {
                            Sponge.getGame().getServiceManager().provide(PaginationService.class).get().builder()
                                    .title(title)
                                    .padding(Text.of(TextColors.DARK_GREEN, "="))
                                    .contents(lines)
                                    .sendTo(src);
                        } else {
                            src.sendMessage(title);
                            src.sendMessages(lines);
                        }
                        return null;
                    });

                    return CommandResult.success();
                });
    }

    private static Text getDescription(CommandSource source, CommandMapping mapping) {
        final Optional<Text> description = mapping.getCallable().getShortDescription(source);
        final Text.Builder text = Text.builder("/" + mapping.getPrimaryAlias());
        text.color(TextColors.GREEN);
        //End with a space, so tab completion works immediately.
        text.onClick(TextActions.suggestCommand("/" + mapping.getPrimaryAlias() + " "));
        mapping.getCallable().getHelp(source)
                .filter(longDescription -> !longDescription.isEmpty())
                .ifPresent(longDescription -> text.onHover(TextActions.showText(longDescription)));
        return Text.of(text, " ", description.orElse(mapping.getCallable().getUsage(source)));
    }
}
