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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.plugin.PluginContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class CommandProvider {

    private final List<String> aliases;
    private final Optional<Integer> opPermissionLevel;

    public CommandProvider(@Nullable Integer opPermissionLevel, String name, String... aliases) {
        this.aliases = ImmutableList.<String>builder()
                .add(checkNotNull(name, "name"))
                .addAll(Arrays.asList(checkNotNull(aliases, "aliases")))
                .build();
        this.opPermissionLevel = Optional.ofNullable(opPermissionLevel);
    }

    /**
     * Gets all the aliases for the provided command, including the
     * main command name at index 0 in the list.
     *
     * @return The aliases
     */
    public List<String> getAliases() {
        return Collections.unmodifiableList(this.aliases);
    }

    /**
     * Builds a {@link CommandSpec} for this command base.
     *
     * @return The command spec
     */
    public CommandSpec buildSpecFor(PluginContainer pluginContainer) {
        final CommandSpec.Builder builder = CommandSpec.builder();
        builder.permission(this.getPermissionFor(pluginContainer));
        this.completeSpec(pluginContainer, builder);
        return builder.build();
    }

    /**
     * Completes the {@link CommandSpec.Builder}, this is the place
     * to add the executor and arguments.
     *
     * @param specBuilder The spec builder
     */
    public abstract void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder);

    /**
     * Gets the name for the permission for the specified {@link PluginContainer}.
     *
     * @param pluginContainer The plugin container
     * @return The permission
     */
    public String getPermissionFor(PluginContainer pluginContainer) {
        return pluginContainer.getId() + ".command." + this.aliases.get(0);
    }

    /**
     * Gets the name for the permission for the specified {@link PluginContainer}.
     *
     * @param pluginContainer The plugin container
     * @return The permission
     */
    public String getChildPermissionFor(PluginContainer pluginContainer, String childCommand) {
        return this.getPermissionFor(pluginContainer) + '.' + childCommand.toLowerCase();
    }

    /**
     * Gets the op permission level for the provided command.
     *
     * @return The op permission level
     */
    public Optional<Integer> getOpPermissionLevel() {
        return this.opPermissionLevel;
    }
}
