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

import org.lanternpowered.server.command.element.GenericArguments2;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextParseException;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.title.Title;

public final class CommandTitle extends CommandProvider {

    public CommandTitle() {
        super(2, "title");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(GenericArguments.playerOrSource(Text.of("player")))
                .child(CommandSpec.builder()
                        .executor((src, args) -> {
                            args.<Player>getOne("player").get().clearTitle();
                            src.sendMessage(t("commands.title.success"));
                            return CommandResult.success();
                        })
                        .build(), "clear")
                .child(CommandSpec.builder()
                        .arguments(GenericArguments2.remainingString(Text.of("title")))
                        .executor((src, args) -> {
                            Text title;
                            try {
                                title = TextSerializers.JSON.deserialize(args.<String>getOne("title").get());
                            } catch (TextParseException e) {
                                throw new CommandException(t("commands.tellraw.jsonException", e.getMessage()));
                            }
                            args.<Player>getOne("player").get().sendTitle(Title.builder().title(title).build());
                            src.sendMessage(t("commands.title.success"));
                            return CommandResult.success();
                        })
                        .build(), "title")
                .child(CommandSpec.builder()
                        .arguments(GenericArguments2.remainingString(Text.of("title")))
                        .executor((src, args) -> {
                            Text title;
                            try {
                                title = TextSerializers.JSON.deserialize(args.<String>getOne("title").get());
                            } catch (TextParseException e) {
                                throw new CommandException(t("commands.tellraw.jsonException", e.getMessage()));
                            }
                            args.<Player>getOne("player").get().sendTitle(Title.builder().subtitle(title).build());
                            src.sendMessage(t("commands.title.success"));
                            return CommandResult.success();
                        })
                        .build(), "subtitle")
                .child(CommandSpec.builder()
                        .executor((src, args) -> {
                            args.<Player>getOne("player").get().resetTitle();
                            src.sendMessage(t("commands.title.success"));
                            return CommandResult.success();
                        })
                        .build(), "reset")
                .child(CommandSpec.builder()
                        .arguments(
                                GenericArguments.integer(Text.of("fadeIn")),
                                GenericArguments.integer(Text.of("stay")),
                                GenericArguments.integer(Text.of("fadeOut"))
                        )
                        .executor((src, args) -> {
                            args.<Player>getOne("player").get().sendTitle(Title.builder()
                                    .fadeIn(args.<Integer>getOne("fadeIn").get())
                                    .stay(args.<Integer>getOne("stay").get())
                                    .fadeOut(args.<Integer>getOne("fadeOut").get())
                                    .build());
                            src.sendMessage(t("commands.title.success"));
                            return CommandResult.success();
                        })
                        .build(), "times");
    }
}
