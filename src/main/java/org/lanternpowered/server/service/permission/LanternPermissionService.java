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
package org.lanternpowered.server.service.permission;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.lanternpowered.server.console.LanternConsoleSource;
import org.lanternpowered.server.network.rcon.RconServer;
import org.lanternpowered.server.network.rcon.RconSource;
import org.lanternpowered.server.service.permission.base.FixedParentMemorySubjectData;
import org.lanternpowered.server.service.permission.base.GlobalMemorySubjectData;
import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionDescription.Builder;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.rcon.RconService;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * Permission service representing the vanilla operator permission structure.
 *
 * <p>Really doesn't do much else. Don't use this guys.
 */
public class LanternPermissionService implements PermissionService {

    private static final String SUBJECTS_DEFAULT = "default";
    private static final Function<String, CommandSource> NO_COMMAND_SOURCE = s -> null;

    private final Game game;
    private final Map<String, PermissionDescription> descriptionMap = new LinkedHashMap<>();
    @Nullable private Collection<PermissionDescription> descriptions;
    private final ConcurrentMap<String, SubjectCollection> subjects = new ConcurrentHashMap<>();
    private final LanternSubjectCollection defaultCollection;
    private final LanternSubject defaultData;

    @Inject
    public LanternPermissionService(Game game) {
        this.game = game;
        this.subjects.put(SUBJECTS_DEFAULT, (this.defaultCollection = this.newCollection(SUBJECTS_DEFAULT)));
        this.subjects.put(SUBJECTS_USER, new UserCollection(this));
        this.subjects.put(SUBJECTS_GROUP, new OpLevelCollection(this));

        this.subjects.put(SUBJECTS_COMMAND_BLOCK, new DataFactoryCollection(SUBJECTS_COMMAND_BLOCK, this,
                s -> new FixedParentMemorySubjectData(LanternPermissionService.this, getGroupForOpLevel(2)), NO_COMMAND_SOURCE));

        this.subjects.put(SUBJECTS_SYSTEM, new DataFactoryCollection(SUBJECTS_SYSTEM, this,
                s -> new FixedParentMemorySubjectData(LanternPermissionService.this, getGroupForOpLevel(4)),
                s -> {
                    if (s.equals(LanternConsoleSource.NAME)) {
                        return Sponge.getServer().getConsole();
                    } else if (s.startsWith(RconSource.NAME_PREFIX)) {
                        String hostName = s.substring(RconSource.NAME_FULL_PREFIX.length(), s.length() - RconSource.NAME_POSTFIX.length());
                        RconService rconService = Sponge.getServiceManager().provideUnchecked(RconService.class);
                        if (rconService instanceof RconServer) {
                            return ((RconServer) rconService).getByHostName(hostName).orElse(null);
                        }
                    }
                    return null;
                }));

        this.defaultData = this.getDefaultCollection().get(SUBJECTS_DEFAULT);
    }

    public Subject getGroupForOpLevel(int level) {
        return getGroupSubjects().get("op_" + level);
    }

    @Override
    public SubjectCollection getUserSubjects() {
        return getSubjects(PermissionService.SUBJECTS_USER);
    }

    @Override
    public SubjectCollection getGroupSubjects() {
        return getSubjects(PermissionService.SUBJECTS_GROUP);
    }

    @Override
    public LanternSubject getDefaults() {
        return this.defaultData;
    }

    @Override
    public void registerContextCalculator(ContextCalculator calculator) {
    }

    @Override
    public SubjectCollection getSubjects(String identifier) {
        SubjectCollection ret = this.subjects.get(identifier);
        if (ret == null) {
            SubjectCollection existingRet = this.subjects.putIfAbsent(identifier, (ret = newCollection(identifier)));
            if (existingRet != null) {
                ret = existingRet;
            }
        }
        return ret;
    }

    private LanternSubjectCollection newCollection(String identifier) {
        return new DataFactoryCollection(identifier, this, s -> new GlobalMemorySubjectData(LanternPermissionService.this), NO_COMMAND_SOURCE);
    }

    @Override
    public Map<String, SubjectCollection> getKnownSubjects() {
        return ImmutableMap.copyOf(this.subjects);
    }

    @Override
    public Optional<Builder> newDescriptionBuilder(Object instance) {
        Optional<PluginContainer> container = this.game.getPluginManager().fromInstance(checkNotNull(instance, "instance"));
        if (!container.isPresent()) {
            throw new IllegalArgumentException("The provided plugin object does not have an associated plugin container "
                    + "(in other words, is 'plugin' actually your plugin object?)");
        }
        return Optional.<Builder>of(new LanternPermissionDescription.Builder(this, container.get()));
    }

    public void addDescription(PermissionDescription permissionDescription) {
        checkNotNull(permissionDescription, "permissionDescription");
        checkNotNull(permissionDescription.getId(), "permissionId");
        this.descriptionMap.put(permissionDescription.getId().toLowerCase(), permissionDescription);
        this.descriptions = null;
    }

    @Override
    public Optional<PermissionDescription> getDescription(String permissionId) {
        return Optional.ofNullable(this.descriptionMap.get(checkNotNull(permissionId, "permissionId").toLowerCase()));
    }

    @Override
    public Collection<PermissionDescription> getDescriptions() {
        Collection<PermissionDescription> descriptions = this.descriptions;
        if (descriptions == null) {
            descriptions = ImmutableList.copyOf(this.descriptionMap.values());
            this.descriptions = descriptions;
        }
        return descriptions;
    }

    public LanternSubjectCollection getDefaultCollection() {
        return this.defaultCollection;
    }
}
