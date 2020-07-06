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

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.spongepowered.api.Platform;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;

public final class CommandVersion extends CommandProvider {

    private static final String UNKNOWN = "unknown";

    public CommandVersion() {
        super(4, "version");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .description(t("commands.version.description"))
                .executor((src, args) -> {
                    final Platform platform = Lantern.getGame().getPlatform();
                    PluginContainer plugin = platform.getContainer(Platform.Component.GAME);
                    src.sendMessage(t("commands.version.minecraft", plugin.getVersion().orElse(UNKNOWN),
                            LanternMinecraftVersion.CURRENT.getProtocol()));
                    plugin = platform.getContainer(Platform.Component.IMPLEMENTATION);
                    src.sendMessage(t("commands.version.implementation", plugin.getName(), plugin.getVersion().orElse(UNKNOWN)));
                    plugin = platform.getContainer(Platform.Component.API);
                    src.sendMessage(t("commands.version.api", plugin.getName(), plugin.getVersion().orElse(UNKNOWN)));
                    return CommandResult.success();
                });
    }
}
