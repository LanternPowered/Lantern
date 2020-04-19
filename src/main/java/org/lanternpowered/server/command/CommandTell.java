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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public final class CommandTell extends CommandProvider {

    public CommandTell() {
        super(0, "tell", "msg", "w");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments2.remainingString(Text.of("message"))
                )
                .executor((src, args) -> {
                    Player player = args.<Player>getOne("player").get();
                    String message = args.<String>getOne("message").get();
                    player.sendMessage(Text.of(TextColors.GRAY, TextStyles.ITALIC,
                            t("commands.message.display.outgoing", player.getName(), message)));
                    src.sendMessage(Text.of(TextColors.GRAY, TextStyles.ITALIC,
                            t("commands.message.display.incoming", src.getName(), message)));
                    return CommandResult.success();
                });
    }
}
