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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

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
