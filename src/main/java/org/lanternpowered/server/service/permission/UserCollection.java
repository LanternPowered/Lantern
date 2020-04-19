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
package org.lanternpowered.server.service.permission;

import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.util.Collection;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * User collection keeping track of opped users.
 */
public class UserCollection extends LanternSubjectCollection {

    UserCollection(LanternPermissionService service) {
        super(PermissionService.SUBJECTS_USER, service);
    }

    @Nullable
    private UUID parseUUID(String identifier) {
        try {
            return UUID.fromString(identifier);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public LanternSubject get(String identifier) {
        final UUID uuid = parseUUID(identifier);
        checkArgument(uuid != null, "Provided identifier must be a uuid, was %s", identifier);
        final GameProfile profile;
        try {
            profile = Sponge.getServer().getGameProfileManager().get(uuid, true).get();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to lookup game profile for " + uuid, e);
        }
        return new UserSubject(profile, this);
    }

    @Override
    public boolean isRegistered(String identifier) {
        final UUID uuid = parseUUID(identifier);
        return uuid != null && Lantern.getGame().getOpsConfig().getEntryByUUID(uuid).isPresent();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collection<Subject> getLoadedSubjects() {
        return (Collection) Sponge.getServer().getOnlinePlayers();
    }

    public LanternPermissionService getService() {
        return this.service;
    }
}
