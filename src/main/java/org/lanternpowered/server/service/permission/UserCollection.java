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

import com.google.common.base.Throwables;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.service.permission.base.LanternSubject;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

/**
 * User collection keeping track of opped users.
 */
public class UserCollection extends LanternSubjectCollection {

    public UserCollection(LanternPermissionService service) {
        super(PermissionService.SUBJECTS_USER, service);
    }

    @Override
    public LanternSubject get(String identifier) {
        UUID uid = identifierToUUID(identifier);
        if (uid == null) {
            throw new IllegalArgumentException("Provided identifier must be a uuid, was " + identifier);
        }
        return get(uuidToGameProfile(uid));
    }

    public LanternSubject get(GameProfile profile) {
        return new UserSubject(profile, this);
    }

    private GameProfile uuidToGameProfile(UUID uuid) {
        try {
            return Lantern.getGame().getGameProfileManager().get(uuid, true).get();
        } catch (InterruptedException | ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public boolean hasRegistered(String identifier) {
        final UUID uuid = identifierToUUID(identifier);
        return uuid != null && Lantern.getGame().getOpsConfig().getEntryByUUID(uuid).isPresent();
    }

    @Nullable
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
        return (Iterable) Sponge.getServer().getOnlinePlayers();
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
}
