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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public final class CommandSetIdleTimeout extends CommandProvider {

    public CommandSetIdleTimeout() {
        super(3, "setidletimeout");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments2.integer(Text.of("timeout-minutes"), 0)
                )
                .executor((src, args) -> {
                    final int timeout = args.<Integer>getOne("timeout-minutes").get();
                    Sponge.getServer().setPlayerIdleTimeout(timeout);
                    src.sendMessage(t("commands.setidletimeout.success", timeout));
                    return CommandResult.success();
                });
    }
}
