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
package org.lanternpowered.server.config.user;

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
            Lantern.getLogger().error("A error occurred while reloading the white-list config.", e);
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
