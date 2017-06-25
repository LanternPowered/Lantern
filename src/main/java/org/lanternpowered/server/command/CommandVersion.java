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

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.spongepowered.api.Platform;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;

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
