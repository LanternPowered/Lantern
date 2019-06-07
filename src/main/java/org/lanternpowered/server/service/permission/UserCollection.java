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
