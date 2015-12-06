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

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;

public final class CommandVersion implements Command {

    private final Game game;

    private final Translation description;
    private final Translation minecraftVersion;
    private final Translation implementationVersion;
    private final Translation apiVersion;

    public CommandVersion(LanternGame game) {
        TranslationManager manager = game.getRegistry().getTranslationManager();

        this.game = game;
        this.description = manager.get("commands.version.description");
        this.minecraftVersion = manager.get("commands.version.minecraft");
        this.implementationVersion = manager.get("commands.version.implementation");
        this.apiVersion = manager.get("commands.version.api");
    }

    @Override
    public CommandSpec build() {
        return CommandSpec.builder()
                .permission("minecraft.command.version")
                .description(Texts.of(this.description))
                .executor((src, args) -> {
                    src.sendMessage(Texts.of(this.minecraftVersion, LanternMinecraftVersion.CURRENT.getName(),
                            LanternMinecraftVersion.CURRENT.getProtocol()));
                    src.sendMessage(Texts.of(this.apiVersion, this.game.getPlatform()
                            .getApi().getVersion()));
                    src.sendMessage(Texts.of(this.implementationVersion, this.game.getPlatform()
                            .getImplementation().getVersion()));
                    return CommandResult.success();
                }).build();
    }
}
