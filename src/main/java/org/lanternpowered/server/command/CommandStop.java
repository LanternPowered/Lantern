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
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public final class CommandStop extends CommandProvider {

    public CommandStop() {
        super(4, "stop", "shutdown");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.optional(GenericArguments2.remainingString(Text.of("reason")))
                )
                .description(t("commands.stop.description"))
                .executor((src, args) -> {
                    final LanternServer server = Lantern.getGame().getServer();
                    if (args.hasAny("reason")) {
                        server.shutdown(Text.of(args.<String>getOne("reason").get()));
                    } else {
                        server.shutdown();
                    }
                    return CommandResult.success();
                });
    }
}
