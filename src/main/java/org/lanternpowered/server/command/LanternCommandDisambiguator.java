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

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.dispatcher.Disambiguator;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternCommandDisambiguator implements Disambiguator {

    private final LanternGame game;

    /**
     * Disambiguator that takes preferences from the global configuration,
     * falling back to {@link SimpleDispatcher#FIRST_DISAMBIGUATOR}.
     *
     * @param game The game instance to be used
     */
    @Inject
    public LanternCommandDisambiguator(LanternGame game) {
        this.game = game;
    }

    @Override
    public Optional<CommandMapping> disambiguate(@Nullable CommandSource source, String aliasUsed, List<CommandMapping> availableOptions) {
        if (availableOptions.size() > 1) {
            final String chosenPlugin = this.game.getGlobalConfig().getCommandAliases().get(aliasUsed.toLowerCase());
            if (chosenPlugin != null) {
                final Optional<PluginContainer> container = this.game.getPluginManager().getPlugin(chosenPlugin);
                if (!container.isPresent()) {
                    this.game.getServer().getConsole().sendMessage(t("Unable to find plugin '%s' for command '%s', falling back to default",
                            chosenPlugin, aliasUsed));
                } else {
                    final Set<CommandMapping> ownedCommands = this.game.getCommandManager().getOwnedBy(container.get());
                    final List<CommandMapping> ownedMatchingCommands = availableOptions.stream()
                            .filter(Predicates.in(ownedCommands)::apply).collect(ImmutableList.toImmutableList());
                    if (ownedMatchingCommands.isEmpty()) {
                        this.game.getServer().getConsole().sendMessage(t("Plugin %s was specified as the preferred owner for %s, "
                                + "but does not have any such command!", container.get().getName(), aliasUsed));
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
