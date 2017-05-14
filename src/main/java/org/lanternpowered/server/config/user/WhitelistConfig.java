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
package org.lanternpowered.server.config.user;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.service.CloseableService;
import org.lanternpowered.server.util.Reloadable;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.whitelist.WhitelistService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

@Singleton
public final class WhitelistConfig extends SimpleUserConfig implements WhitelistService, Reloadable, CloseableService {

    private static final String FILE_NAME = "whitelist.json";

    @Inject
    public WhitelistConfig(@Named(DirectoryKeys.CONFIG) Path configDirectory) throws IOException {
        super(configDirectory.resolve(FILE_NAME), false);
    }

    @Override
    public Collection<GameProfile> getWhitelistedProfiles() {
        return getEntries().stream().map(UserEntry::getProfile).collect(ImmutableList.toImmutableList());
    }

    @Override
    public boolean isWhitelisted(GameProfile profile) {
        return getEntryByProfile(profile).isPresent();
    }

    @Override
    public boolean addProfile(GameProfile profile) {
        if (isWhitelisted(profile)) {
            return true;
        }
        addEntry(new UserEntry((LanternGameProfile) profile));
        return false;
    }

    @Override
    public boolean removeProfile(GameProfile profile) {
        return removeEntry(profile.getUniqueId());
    }

    @Override
    public void reload() {
        try {
            load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            save();
        } catch (IOException e) {
            Lantern.getLogger().error("A error occurred while saving the white-list config.", e);
        }
    }
}
