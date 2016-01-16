/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.service.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.config.user.UserEntry;
import org.lanternpowered.server.config.user.ban.BanEntry;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.LanternUser;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.profile.LanternGameProfileManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.GuavaCollectors;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class LanternUserStorageService implements UserStorageService {

    private static final Cache<UUID, User> userCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    static User create(LanternGameProfile profile) {
        final User user = new LanternUser(profile);
        userCache.put(profile.getUniqueId(), user);
        return user;
    }

    @Nullable
    private static User findByUsername(String username) {
        final LanternGameProfileManager gameProfileManager = LanternGame.get()
                .getGameProfileManager();
        Optional<GameProfile> gameProfile = gameProfileManager.getCachedProfile(username);
        if (gameProfile.isPresent()) {
            return findByUUID(gameProfile.get().getUniqueId());
        }
        return null;
    }

    @Nullable
    private static User findByUUID(UUID uniqueId) {
        User user = userCache.getIfPresent(uniqueId);
        if (user != null) {
            return user;
        }
        user = getOnlinePlayer(uniqueId);
        if (user != null) {
            return user;
        }
        user = getFromStoredData(uniqueId);
        if (user != null) {
            return user;
        }
        user = getFromWhitelist(uniqueId);
        if (user != null) {
            return user;
        }
        user = getFromBanlist(uniqueId);
        return user;
    }

    @Nullable
    private static User getOnlinePlayer(UUID uniqueId) {
        Optional<Player> player = LanternGame.get().getServer().getPlayer(uniqueId);
        if (player.isPresent()) {
            return ((LanternPlayer) player.get()).getUserObject();
        }
        return null;
    }

    @Nullable
    private static User getFromStoredData(UUID uniqueId) {
        // TODO
        return null;
    }

    @Nullable
    private static User getFromWhitelist(UUID uniqueId) {
        final Optional<UserEntry> optEntry = LanternGame.get().getWhitelistConfig().getEntryByUUID(uniqueId);
        if (optEntry.isPresent()) {
            return create(optEntry.get().getProfile());
        }
        return null;
    }

    @Nullable
    private static User getFromBanlist(UUID uniqueId) {
        final Optional<BanEntry> optEntry = LanternGame.get().getBanConfig().getEntryByUUID(uniqueId);
        if (optEntry.isPresent()) {
            return create(((BanEntry.Profile) optEntry.get()).getProfile());
        }
        return null;
    }

    static Collection<GameProfile> getAllProfiles() {
        ImmutableList.Builder<GameProfile> profiles = ImmutableList.builder();
        for (Player player : LanternGame.get().getServer().getOnlinePlayers()) {
            profiles.add(player.getProfile());
        }
        // TODO: Add whitelist, ban, etc entries
        return profiles.build();
    }

    @Override
    public Optional<User> get(UUID uniqueId) {
        return Optional.ofNullable(findByUUID(checkNotNull(uniqueId, "uniqueId")));
    }

    @Override
    public Optional<User> get(String lastKnownName) {
        checkNotNull(lastKnownName, "lastKnownName");
        checkArgument(lastKnownName.length() >= 3 && lastKnownName.length() <= 16, "Invalid username %s", lastKnownName);
        return Optional.ofNullable(findByUsername(lastKnownName));
    }

    @Override
    public Optional<User> get(GameProfile profile) {
        return Optional.ofNullable(findByUUID(checkNotNull(profile, "profile").getUniqueId()));
    }

    @Override
    public User getOrCreate(GameProfile profile) {
        Optional<User> user = this.get(profile);
        if (user.isPresent()) {
            return user.get();
        }
        return create((LanternGameProfile) profile);
    }

    @Override
    public Collection<GameProfile> getAll() {
        return getAllProfiles();
    }

    @Override
    public boolean delete(GameProfile profile) {
        final LanternGame game = LanternGame.get();
        game.getOpsConfig().removeEntry(profile.getUniqueId());
        game.getWhitelistConfig().removeEntry(profile.getUniqueId());
        game.getBanConfig().removeEntry(profile.getUniqueId());
        return true;
    }

    @Override
    public boolean delete(User user) {
        return this.delete(user.getProfile());
    }

    @Override
    public Collection<GameProfile> match(String lastKnownName) {
        final String lastKnownName0 = checkNotNull(lastKnownName, "lastKnownName").toLowerCase(Locale.ROOT);
        return getAllProfiles().stream().filter(profile -> {
            final Optional<String> optName = profile.getName();
            return optName.isPresent() && optName.get().startsWith(lastKnownName0);
        }).collect(GuavaCollectors.toImmutableList());
    }

}
