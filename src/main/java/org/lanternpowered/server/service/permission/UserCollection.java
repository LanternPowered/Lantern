/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.service.permission;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.util.UUID;

/**
 * User collection keeping track of opped users.
 */
public class UserCollection extends LanternSubjectCollection {

    private final LanternPermissionService service;

    public UserCollection(LanternPermissionService service) {
        super(PermissionService.SUBJECTS_USER);
        this.service = service;
    }

    @Override
    public Subject get(String identifier) {
        UUID uid = identifierToUUID(identifier);
        if (uid == null) {
            throw new IllegalArgumentException("Provided identifier must be a uuid, was " + identifier);
        }
        return get(uuidToGameProfile(uid));
    }

    protected Subject get(GameProfile profile) {
        return new UserSubject(profile, this);
    }

    private GameProfile uuidToGameProfile(UUID uuid) {
        return (GameProfile) LanternGame.get().getGameProfileManager().get(uuid, true);
    }

    @Override
    public boolean hasRegistered(String identifier) {
        UUID uuid = identifierToUUID(identifier);
        if (uuid == null) {
            return false;
        }
        return LanternGame.get().getOpsConfig().getEntryByUUID(uuid) != null;
    }

    private UUID identifierToUUID(String identifier) {
        try {
            return UUID.fromString(identifier);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Iterable<Subject> getAllSubjects() {
        return (Iterable) LanternGame.get().getServer().getOnlinePlayers();
        /*return ImmutableSet.copyOf(Iterables.concat(
                Iterables.<Object, Subject>transform(SpongePermissionService.getOps().getValues().values(),
                        new Function<Object, Subject>() {
                        @Nullable
                        @Override
                        public Subject apply(Object input) {
                            GameProfile profile = ((GameProfile) ((UserListOpsEntry) input).value);
                            return get(profile);
                        }
                        // WARNING: This gives dupes
                    }), Sponge.getGame().getServer().getOnlinePlayers()));*/
    }

    public LanternPermissionService getService() {
        return this.service;
    }
}