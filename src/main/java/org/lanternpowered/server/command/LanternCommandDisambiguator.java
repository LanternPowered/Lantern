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

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.dispatcher.Disambiguator;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public final class LanternCommandDisambiguator implements Disambiguator {

    private final LanternGame game;

    /**
     * Disambiguator that takes preferences from the global configuration,
     * falling back to {@link SimpleDispatcher#FIRST_DISAMBIGUATOR}.
     *
     * @param game The game instance to be used
     */
    public LanternCommandDisambiguator(LanternGame game) {
        this.game = game;
    }

    @Override
    public Optional<CommandMapping> disambiguate(@Nullable CommandSource source, String aliasUsed, List<CommandMapping> availableOptions) {
        if (availableOptions.size() > 1) {
            final String chosenPlugin = this.game.getGlobalConfig().getCommandAliases().get(aliasUsed.toLowerCase());
            if (chosenPlugin != null) {
                Optional<PluginContainer> container = this.game.getPluginManager().getPlugin(chosenPlugin);
                if (!container.isPresent()) {
                    this.game.getServer().getConsole().sendMessage(t("Unable to find plugin '" + chosenPlugin +
                            "' for command '" + aliasUsed + "', falling back to default"));
                } else {
                    final Set<CommandMapping> ownedCommands = this.game.getCommandManager().getOwnedBy(container.get());
                    final List<CommandMapping> ownedMatchingCommands = ImmutableList.copyOf(Iterables.filter(availableOptions,
                            Predicates.in(ownedCommands)));
                    if (ownedMatchingCommands.isEmpty()) {
                        this.game.getServer().getConsole().sendMessage(t("Plugin " + container.get().getName() + " was specified as the "
                                + "preferred owner for " + aliasUsed + ", but does not have any such command!"));
                    } else if (ownedMatchingCommands.size() > 1) {
                        throw new IllegalStateException("Plugin " + container.get().getName() + " seems to have multiple commands registered as "
                                + aliasUsed + "! This is a programming error!");
                    } else {
                        return Optional.of(ownedMatchingCommands.get(0));
                    }
                }
            }
        }
        return SimpleDispatcher.FIRST_DISAMBIGUATOR.disambiguate(source, aliasUsed, availableOptions);
    }

}
