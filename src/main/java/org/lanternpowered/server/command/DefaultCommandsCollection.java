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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.server.command.test.CommandOpenTestContainer;
import org.lanternpowered.api.inject.service.ServiceRef;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.service.permission.LanternPermissionService;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.Map;

@Singleton
public final class DefaultCommandsCollection {

    @Inject private CommandManager commandManager;
    @Inject private ServiceRef<PermissionService> permissionService;

    @Inject @Named(InternalPluginsInfo.Minecraft.IDENTIFIER) private PluginContainer minecraft;
    @Inject @Named(InternalPluginsInfo.Implementation.IDENTIFIER) private PluginContainer implementation;

    @Inject
    private DefaultCommandsCollection() {
    }

    public void load() {
        final Multimap<PluginContainer, CommandProvider> commandProviders = HashMultimap.create();
        // Minecraft Commands
        commandProviders.put(this.minecraft, new CommandBan());
        commandProviders.put(this.minecraft, new CommandBanIp());
        commandProviders.put(this.minecraft, new CommandBorder());
        commandProviders.put(this.minecraft, new CommandDeop());
        commandProviders.put(this.minecraft, new CommandDifficulty());
        commandProviders.put(this.minecraft, new CommandGameMode());
        commandProviders.put(this.minecraft, new CommandGameRule());
        commandProviders.put(this.minecraft, new CommandHelp());
        commandProviders.put(this.minecraft, new CommandKick());
        commandProviders.put(this.minecraft, new CommandListBans());
        commandProviders.put(this.minecraft, new CommandListPlayers());
        commandProviders.put(this.minecraft, new CommandMe());
        commandProviders.put(this.minecraft, new CommandOp());
        commandProviders.put(this.minecraft, new CommandPardon());
        commandProviders.put(this.minecraft, new CommandPardonIp());
        commandProviders.put(this.minecraft, new CommandParticle());
        commandProviders.put(this.implementation, new CommandParticleEffect());
        commandProviders.put(this.minecraft, new CommandPlaySound());
        commandProviders.put(this.minecraft, new CommandSay());
        commandProviders.put(this.minecraft, new CommandScoreboard());
        commandProviders.put(this.implementation, new CommandSetData());
        commandProviders.put(this.minecraft, new CommandSetIdleTimeout());
        commandProviders.put(this.minecraft, new CommandSetSpawn());
        commandProviders.put(this.minecraft, new CommandStop());
        commandProviders.put(this.minecraft, new CommandStopSound());
        commandProviders.put(this.minecraft, new CommandTeleport());
        commandProviders.put(this.minecraft, new CommandTell());
        commandProviders.put(this.minecraft, new CommandTime());
        commandProviders.put(this.minecraft, new CommandTitle());
        commandProviders.put(this.minecraft, new CommandToggleDownfall());
        commandProviders.put(this.minecraft, new CommandTp());
        commandProviders.put(this.implementation, new CommandVersion());
        commandProviders.put(this.minecraft, new CommandWeather());
        commandProviders.put(this.minecraft, new CommandWhitelist());
        // Testing Commands
        commandProviders.put(this.implementation, new CommandOpenTestContainer());

        for (Map.Entry<PluginContainer, CommandProvider> entry : commandProviders.entries()) {
            final PluginContainer plugin = entry.getKey();
            this.commandManager.register(plugin, entry.getValue().buildSpecFor(plugin), entry.getValue().getAliases());
        }

        final PermissionService permissionService = this.permissionService.get();
        if (permissionService instanceof LanternPermissionService) {
            final LanternPermissionService lanternPermissionService = (LanternPermissionService) permissionService;
            //noinspection Convert2streamapi
            for (Map.Entry<PluginContainer, CommandProvider> entry : commandProviders.entries()) {
                entry.getValue().getOpPermissionLevel().ifPresent(level -> lanternPermissionService.getGroupForOpLevel(level).getSubjectData()
                        .setPermission(SubjectData.GLOBAL_CONTEXT, entry.getValue().getPermissionFor(entry.getKey()), Tristate.TRUE));
            }
        } else {
            //noinspection Convert2streamapi
            for (Map.Entry<PluginContainer, CommandProvider> entry : commandProviders.entries()) {
                if (entry.getValue().getOpPermissionLevel().orElse(0) == 0) {
                    permissionService.getDefaults().getTransientSubjectData().setPermission(
                            SubjectData.GLOBAL_CONTEXT, entry.getValue().getPermissionFor(entry.getKey()), Tristate.TRUE);
                }
            }
        }
    }
}
