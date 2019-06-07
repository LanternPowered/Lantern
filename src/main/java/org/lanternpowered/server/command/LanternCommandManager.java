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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.command.CommandMessageFormatting.error;
import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.text.LanternTexts;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.InvocationCommandException;
import org.spongepowered.api.command.dispatcher.Disambiguator;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.TextMessageException;
import org.spongepowered.api.world.Location;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A simple implementation of {@link CommandManager}.
 * This service calls the appropriate events for a command.
 */
@Singleton
public class LanternCommandManager implements CommandManager {

    private final Logger logger;
    private final SimpleDispatcher dispatcher;
    private final Multimap<PluginContainer, CommandMapping> owners = HashMultimap.create();
    private final Map<CommandMapping, PluginContainer> reverseOwners = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    /**
     * Construct a simple {@link CommandManager}.
     *
     * @param logger The logger to log error messages to
     * @param disambiguator The function to resolve a single command when multiple options are available
     */
    @Inject
    public LanternCommandManager(Logger logger, Disambiguator disambiguator) {
        this.dispatcher = new SimpleDispatcher(disambiguator);
        this.logger = logger;
    }

    @Override
    public Optional<CommandMapping> register(Object plugin, CommandCallable callable, String... alias) {
        return register(plugin, callable, Arrays.asList(alias));
    }

    @Override
    public Optional<CommandMapping> register(Object plugin, CommandCallable callable, List<String> aliases) {
        return register(plugin, callable, aliases, Function.identity());
    }

    @Override
    public Optional<CommandMapping> register(Object plugin, CommandCallable callable, List<String> aliases,
            Function<List<String>, List<String>> callback) {
        checkNotNull(plugin, "plugin");

        Optional<PluginContainer> containerOptional = Sponge.getGame().getPluginManager().fromInstance(plugin);
        if (!containerOptional.isPresent()) {
            throw new IllegalArgumentException(
                    "The provided plugin object does not have an associated plugin container "
                            + "(in other words, is 'plugin' actually your plugin object?");
        }

        PluginContainer container = containerOptional.get();

        synchronized (this.lock) {
            // <namespace>:<alias> for all commands
            final List<String> aliasesWithPrefix = new ArrayList<>(aliases.size() * 2);
            for (String alias : aliases) {
                final Collection<CommandMapping> ownedCommands = this.owners.get(container);
                for (CommandMapping mapping : this.dispatcher.getAll(alias)) {
                    if (ownedCommands.contains(mapping)) {
                        throw new IllegalArgumentException("A plugin may not register multiple commands for the same alias ('" + alias + "')!");
                    }
                }

                aliasesWithPrefix.add(alias);
                aliasesWithPrefix.add(container.getId() + ':' + alias);
            }

            final Optional<CommandMapping> mapping = this.dispatcher.register(callable, aliasesWithPrefix, callback);
            if (mapping.isPresent()) {
                this.owners.put(container, mapping.get());
                this.reverseOwners.put(mapping.get(), container);
            }

            return mapping;
        }
    }

    @Override
    public Optional<CommandMapping> removeMapping(CommandMapping mapping) {
        synchronized (this.lock) {
            final Optional<CommandMapping> removed = this.dispatcher.removeMapping(mapping);
            removed.ifPresent(this::forgetMapping);
            return removed;
        }
    }

    private void forgetMapping(CommandMapping mapping) {
        Iterator<CommandMapping> it = this.owners.values().iterator();
        while (it.hasNext()) {
            if (it.next().equals(mapping)) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public Set<PluginContainer> getPluginContainers() {
        synchronized (this.lock) {
            return ImmutableSet.copyOf(this.owners.keySet());
        }
    }

    @Override
    public Set<CommandMapping> getCommands() {
        return this.dispatcher.getCommands();
    }

    @Override
    public Set<CommandMapping> getOwnedBy(Object instance) {
        final Optional<PluginContainer> container = Sponge.getGame().getPluginManager().fromInstance(instance);
        if (!container.isPresent()) {
            throw new IllegalArgumentException("The provided plugin object does not have an associated plugin container "
                    + "(in other words, is 'plugin' actually your plugin object?)");
        }
        synchronized (this.lock) {
            return ImmutableSet.copyOf(this.owners.get(container.get()));
        }
    }

    @Override
    public Optional<PluginContainer> getOwner(CommandMapping mapping) {
        return Optional.ofNullable(this.reverseOwners.get(checkNotNull(mapping, "mapping")));
    }

    @Override
    public Set<String> getPrimaryAliases() {
        return this.dispatcher.getPrimaryAliases();
    }

    @Override
    public Set<String> getAliases() {
        return this.dispatcher.getAliases();
    }

    @Override
    public Optional<CommandMapping> get(String alias) {
        return this.dispatcher.get(alias);
    }

    @Override
    public Optional<? extends CommandMapping> get(String alias, @Nullable CommandSource source) {
        return this.dispatcher.get(alias, source);
    }

    @Override
    public Set<? extends CommandMapping> getAll(String alias) {
        return this.dispatcher.getAll(alias);
    }

    @Override
    public Multimap<String, CommandMapping> getAll() {
        return this.dispatcher.getAll();
    }

    @Override
    public boolean containsAlias(String alias) {
        return this.dispatcher.containsAlias(alias);
    }

    @Override
    public boolean containsMapping(CommandMapping mapping) {
        return this.dispatcher.containsMapping(mapping);
    }

    @Override
    public CommandResult process(CommandSource source, String commandLine) {
        checkNotNull(source, "source");
        final String[] argSplit = commandLine.split(" ", 2);
        final CauseStack causeStack = CauseStack.currentOrEmpty();
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(source);
            final SendCommandEvent event = SpongeEventFactory.createSendCommandEvent(frame.getCurrentCause(),
                argSplit.length > 1 ? argSplit[1] : "", argSplit[0], CommandResult.empty());
            Sponge.getGame().getEventManager().post(event);
            if (event.isCancelled()) {
                return event.getResult();
            }

            // Only the first part of argSplit is used at the moment, do the other in the future if needed.
            argSplit[0] = event.getCommand();

            commandLine = event.getCommand();
            if (!event.getArguments().isEmpty()) {
                commandLine = commandLine + ' ' + event.getArguments();
            }

            try {
                return this.dispatcher.process(source, commandLine);
            } catch (InvocationCommandException ex) {
                if (ex.getCause() != null) {
                    throw ex.getCause();
                }
            } catch (CommandPermissionException ex) {
                Text text = ex.getText();
                if (text != null) {
                    source.sendMessage(error(text));
                }
            } catch (CommandException ex) {
                Text text = ex.getText();
                if (text != null) {
                    source.sendMessage(error(text));
                }

                if (ex.shouldIncludeUsage()) {
                    final Optional<CommandMapping> mapping = this.dispatcher.get(argSplit[0], source);
                    mapping.ifPresent(commandMapping -> source.sendMessage(error(t("commands.generic.usage",
                            t("/%s %s", argSplit[0], commandMapping.getCallable().getUsage(source))))));
                }
            }
        } catch (Throwable thr) {
            final Text.Builder excBuilder;
            if (thr instanceof TextMessageException) {
                final Text text = ((TextMessageException) thr).getText();
                excBuilder = text == null ? Text.builder("null") : Text.builder().append(text);
            } else {
                excBuilder = Text.builder(String.valueOf(thr.getMessage()));
            }
            if (source.hasPermission("sponge.debug.hover-stacktrace")) {
                final StringWriter writer = new StringWriter();
                thr.printStackTrace(new PrintWriter(writer));
                excBuilder.onHover(TextActions.showText(Text.of(writer.toString()
                        .replace("\t", "    ")
                        .replace("\r\n", "\n")
                        .replace("\r", "\n")))); // I mean I guess somebody could be running this on like OS 9?
            }
            source.sendMessage(error(t("Error occurred while executing command: %s", excBuilder.build())));
            this.logger.error(LanternTexts.toLegacy(t("Error occurred while executing command '%s' for source %s: %s",
                    commandLine, source.toString(), String.valueOf(thr.getMessage()))), thr);
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location targetPosition) {
        return getSuggestions(source, arguments, targetPosition, false);
    }

    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location targetPosition, boolean usingBlock) {
        final CauseStack causeStack = CauseStack.currentOrEmpty();
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(source);
            final List<String> suggestions;
            final String[] argSplit = arguments.split(" ", 2);
            // TODO: Fix this in the SimpleDispatcher -> in 'getCustomSuggestions' add after
            // 'argSplit.length == 1' the check '&& !arguments.endsWith(" ")'
            if (argSplit.length == 1 && !arguments.endsWith(" ")) {
                suggestions = this.dispatcher.getSuggestions(source, arguments, targetPosition);
            } else {
                final Optional<? extends CommandMapping> cmdOptional = this.dispatcher.get(argSplit[0], source);
                if (!cmdOptional.isPresent()) {
                    suggestions = ImmutableList.of();
                } else {
                    suggestions = cmdOptional.get().getCallable().getSuggestions(source, argSplit[1], targetPosition);
                }
            }

            final List<String> rawSuggestions = new ArrayList<>(suggestions);
            final TabCompleteEvent.Command event = SpongeEventFactory.createTabCompleteEventCommand(frame.getCurrentCause(),
                    ImmutableList.copyOf(suggestions), rawSuggestions, argSplit.length > 1 ? argSplit[1] : "", argSplit[0],
                    arguments, Optional.ofNullable(targetPosition), usingBlock);
            Sponge.getGame().getEventManager().post(event);
            if (event.isCancelled()) {
                return ImmutableList.of();
            } else {
                return ImmutableList.copyOf(event.getTabCompletions());
            }
        } catch (CommandException e) {
            source.sendMessage(error(t("Error getting suggestions: %s", e.getText())));
            return ImmutableList.of();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error occurred while tab completing '%s'", arguments), e);
        }
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return this.dispatcher.testPermission(source);
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return this.dispatcher.getShortDescription(source);
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return this.dispatcher.getHelp(source);
    }

    @Override
    public Text getUsage(CommandSource source) {
        return this.dispatcher.getUsage(source);
    }

    @Override
    public int size() {
        return this.dispatcher.size();
    }
}
