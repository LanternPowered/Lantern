/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.StartsWithPredicate;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public final class CommandHelp {

    public static final String PERMISSION = "minecraft.commands.help";

    private static final Field extendedDescriptionField;

    static {
        try {
            extendedDescriptionField = CommandSpec.class.getDeclaredField("extendedDescription");
            extendedDescriptionField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static CommandSpec create() {
        final Comparator<CommandMapping> comparator = (o1, o2) -> o1.getPrimaryAlias().compareTo(o2.getPrimaryAlias());
        return CommandSpec
                .builder()
                .permission(PERMISSION)
                .arguments(GenericArguments.optional(new CommandElement(Text.of("command")) {

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
                }))
                .description(Text.of("View a list of all commands"))
                .extendedDescription(Text.of("View a list of all commands. Hover over\n" + " a command to view its description."
                        + " Click\n a command to insert it into your chat bar."))
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
                                callable.getShortDescription(src).ifPresent(des -> builder.append(des, Text.NEW_LINE));
                                builder.append(t("commands.generic.usage", t("/%s %s", command.get(), callable.getUsage(src))));
                                Text extendedDescription;
                                try {
                                    // TODO: Why is there no method :(
                                    extendedDescription = (Text) extendedDescriptionField.get(callable);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                                if (extendedDescription != null) {
                                    builder.append(Text.NEW_LINE, extendedDescription);
                                }
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

                    Lantern.getGame().getScheduler().submitAsyncTask(() -> {
                        TreeSet<CommandMapping> commands = new TreeSet<>(comparator);
                        commands.addAll(Collections2.filter(Sponge.getCommandManager().getAll().values(),
                                input -> input.getCallable().testPermission(src)));

                        // Console sources cannot see/use the pagination
                        boolean paginate = !(src instanceof ConsoleSource);

                        Text title = Text.builder("Available commands:").color(TextColors.DARK_GREEN).build();
                        Collection<Text> lines = Collections2.transform(commands, input -> getDescription(src, input));

                        if (paginate) {
                            PaginationList.Builder builder = Sponge.getGame().getServiceManager()
                                    .provide(PaginationService.class).get().builder();
                            builder.title(title);
                            builder.contents(lines);
                            builder.sendTo(src);
                        } else {
                            src.sendMessage(title);
                            src.sendMessages(lines);
                        }
                        return null;
                    });

                    return CommandResult.success();
                }).build();
    }

    @SuppressWarnings("unchecked")
    private static Text getDescription(CommandSource source, CommandMapping mapping) {
        final Optional<Text> description = (Optional<Text>) mapping.getCallable().getShortDescription(source);
        Text.Builder text = Text.builder("/" + mapping.getPrimaryAlias());
        text.color(TextColors.GREEN);
        text.style(TextStyles.UNDERLINE);
        text.onClick(TextActions.suggestCommand("/" + mapping.getPrimaryAlias()));
        Optional<? extends Text> longDescription = mapping.getCallable().getHelp(source);
        if (longDescription.isPresent()) {
            text.onHover(TextActions.showText(longDescription.get()));
        }
        return Text.of(text, " ", description.orElse(mapping.getCallable().getUsage(source)));
    }

    private CommandHelp() {
    }

}
