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
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.service.rcon.RconService;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

/**
 * Permission service representing the vanilla operator permission structure.
 *
 * <p>Really doesn't do much else. Don't use this guys.
 */
public final class LanternPermissionService implements PermissionService {

    private static final String SUBJECTS_DEFAULT = "default";
    private static final Function<String, CommandSource> NO_COMMAND_SOURCE = s -> null;

    private final Game game;
    private final Map<String, PermissionDescription> descriptionMap = new LinkedHashMap<>();
    @Nullable private Collection<PermissionDescription> descriptions;
    private final ConcurrentMap<String, LanternSubjectCollection> subjects = new ConcurrentHashMap<>();
    private final LanternSubjectCollection defaultCollection;
    private final LanternSubject defaultData;

    @Inject
    private LanternPermissionService(Game game) {
        this.game = game;
        this.defaultData = new OpLevelCollection.OpLevelSubject(this, 0);
        this.subjects.put(SUBJECTS_DEFAULT, this.defaultCollection = newCollection(SUBJECTS_DEFAULT));
        this.subjects.put(SUBJECTS_USER, new UserCollection(this));
        this.subjects.put(SUBJECTS_GROUP, new OpLevelCollection(this));
        this.subjects.put(SUBJECTS_COMMAND_BLOCK, new DataFactoryCollection(SUBJECTS_COMMAND_BLOCK, this,
                s -> new FixedParentMemorySubjectData(this.defaultData, getGroupForOpLevel(2).asSubjectReference()), NO_COMMAND_SOURCE));
        this.subjects.put(SUBJECTS_SYSTEM, new DataFactoryCollection(SUBJECTS_SYSTEM, this,
                s -> new FixedParentMemorySubjectData(this.defaultData, getGroupForOpLevel(4).asSubjectReference()),
                s -> {
                    if (s.equals(LanternConsoleSource.NAME)) {
                        return Sponge.getServer().getConsole();
                    } else {
                        final Matcher matcher = RconSource.NAME_PATTERN.matcher(s);
                        if (matcher.matches()) {
                            final String hostName = matcher.group(1);
                            final RconService rconService = Sponge.getServiceManager().provideUnchecked(RconService.class);
                            if (rconService instanceof RconServer) {
                                return ((RconServer) rconService).getByHostName(hostName).orElse(null);
                            }
                        }
                    }
                    return null;
                }));
    }

    public Subject getGroupForOpLevel(int level) {
        return getGroupSubjects().get("op_" + level);
    }

    @Override
    public LanternSubjectCollection getUserSubjects() {
        return get(PermissionService.SUBJECTS_USER);
    }

    @Override
    public LanternSubjectCollection getGroupSubjects() {
        return get(PermissionService.SUBJECTS_GROUP);
    }

    private LanternSubjectCollection newCollection(String identifier) {
        checkNotNull(identifier, "identifier");
        return new DataFactoryCollection(identifier, this,
                s -> new GlobalMemorySubjectData(this.defaultData), NO_COMMAND_SOURCE);
    }

    public LanternSubjectCollection get(String identifier) {
        checkNotNull(identifier, "identifier");
        return this.subjects.computeIfAbsent(identifier, this::newCollection);
    }

    @Override
    public LanternSubject getDefaults() {
        return this.defaultData;
    }

    @Override
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    public SubjectReference newSubjectReference(String collectionIdentifier, String subjectIdentifier) {
        checkNotNull(collectionIdentifier, "collectionIdentifier");
        checkNotNull(subjectIdentifier, "subjectIdentifier");
        return new LanternSubjectReference(this, collectionIdentifier, subjectIdentifier);
    }

    @Override
    public CompletableFuture<SubjectCollection> loadCollection(String identifier) {
        return CompletableFuture.completedFuture(get(identifier));
    }

    @Override
    public Optional<SubjectCollection> getCollection(String identifier) {
        return Optional.of(get(identifier));
    }

    @Override
    public CompletableFuture<Boolean> hasCollection(String identifier) {
        return CompletableFuture.completedFuture(this.subjects.containsKey(identifier));
    }

    @Override
    public Map<String, SubjectCollection> getLoadedCollections() {
        return ImmutableMap.copyOf(this.subjects);
    }

    @Override
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(getLoadedCollections().keySet());
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Subject> calculator) {
    }

    @Override
    public Builder newDescriptionBuilder(Object instance) {
        final Optional<PluginContainer> container = this.game.getPluginManager().fromInstance(checkNotNull(instance, "instance"));
        if (!container.isPresent()) {
            throw new IllegalArgumentException("The provided plugin object does not have an associated plugin container "
                    + "(in other words, is 'plugin' actually your plugin object?)");
        }
        return new LanternPermissionDescription.Builder(this, container.get());
    }

    void addDescription(PermissionDescription permissionDescription) {
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
