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
package org.lanternpowered.server.service.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.lanternpowered.api.inject.service.ServiceRef;
import org.lanternpowered.server.config.user.OpsConfig;
import org.lanternpowered.server.config.user.UserEntry;
import org.lanternpowered.server.config.user.WhitelistConfig;
import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.config.user.ban.BanEntry;
import org.lanternpowered.server.data.io.UserIO;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.ProxyUser;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.service.CloseableService;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.server.Server;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.service.whitelist.WhitelistService;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class LanternUserStorageService implements UserStorageService, CloseableService {

    private final Cache<UUID, ProxyUser> userCache = CacheBuilder.newBuilder()
            .weakValues()
            // Remove the internal user instance to save the player data
            .removalListener((RemovalListener<UUID, ProxyUser>) notification -> {
                final ProxyUser user = notification.getValue();
                final User internalUser = user.getInternalUser();
                if (internalUser != null && internalUser.isOnline()) {
                    user.setInternalUser(null);
                }
            })
            .build();

    @Inject private Server server;
    @Inject private GameProfileManager profileManager;
    @Inject private OpsConfig opsConfig;
    @Inject private ServiceRef<WhitelistService> whitelistService;
    @Inject private ServiceRef<BanService> banService;

    @Inject
    private LanternUserStorageService() {
    }

    @Nullable
    private ProxyUser getUser(UUID uniqueId) {
        checkNotNull(uniqueId, "uniqueId");
        ProxyUser user = this.userCache.getIfPresent(uniqueId);
        if (user != null) {
            return user;
        }
        user = getFromOnlinePlayer(uniqueId);
        if (user == null) {
            user = getFromWhitelistService(uniqueId);
            if (user == null) {
                user = getFromBanService(uniqueId);
                if (user == null) {
                    user = getFromOpsConfig(uniqueId);
                    // Last the stored data, the user name isn't
                    // included in the player data by default.
                    if (user == null) {
                        user = getFromStoredData(uniqueId);
                    }
                }
            }
        }
        if (user != null) {
            this.userCache.put(uniqueId, user);
        }
        return user;
    }

    /**
     * Attempts to get a {@link User} from the online players.
     *
     * @param uniqueId The unique id
     * @return The user
     */
    @Nullable
    private ProxyUser getFromOnlinePlayer(UUID uniqueId) {
        return this.server.getPlayer(uniqueId).map(player -> ((LanternPlayer) player).getProxyUser()).orElse(null);
    }

    /**
     * Attempts to get a {@link User} from the {@link WhitelistService}.
     *
     * @param uniqueId The unique id
     * @return The user
     */
    @Nullable
    private ProxyUser getFromWhitelistService(UUID uniqueId) {
        final LanternGameProfile gameProfile;
        final WhitelistService whitelistService = this.whitelistService.get();
        if (whitelistService instanceof WhitelistConfig) {
            gameProfile = ((WhitelistConfig) whitelistService).getEntryByUUID(uniqueId)
                    .map(UserEntry::getProfile).orElse(null);
        } else {
            gameProfile = (LanternGameProfile) whitelistService.getWhitelistedProfiles().stream()
                    .filter(profile -> profile.getUniqueId().equals(uniqueId))
                    .findFirst().orElse(null);
        }
        return gameProfile == null ? null : new ProxyUser(gameProfile);
    }

    /**
     * Attempts to get a {@link User} from the {@link BanService}.
     *
     * @param uniqueId The unique id
     * @return The user
     */
    @Nullable
    private ProxyUser getFromBanService(UUID uniqueId) {
        final LanternGameProfile gameProfile;
        final BanService banService = this.banService.get();
        if (banService instanceof BanConfig) {
            gameProfile = ((BanConfig) banService).getEntryByUUID(uniqueId)
                    .map(entry -> ((BanEntry.Profile) entry).getProfile()).orElse(null);
        } else {
            gameProfile = banService.getBanFor(new LanternGameProfile(uniqueId, null))
                    .map(entry -> ((BanEntry.Profile) entry).getProfile()).orElse(null);
        }
        return gameProfile == null ? null : new ProxyUser(gameProfile);
    }

    /**
     * Attempts to get a {@link User} from the {@link OpsConfig}.
     *
     * @param uniqueId The unique id
     * @return The user
     */
    @Nullable
    private ProxyUser getFromOpsConfig(UUID uniqueId) {
        return this.opsConfig.getEntryByUUID(uniqueId).map(entry -> new ProxyUser(entry.getProfile())).orElse(null);
    }

    /**
     * Attempts to get a {@link User} from the stored data.
     *
     * @param uniqueId The unique id
     * @return The user
     */
    @Nullable
    private ProxyUser getFromStoredData(UUID uniqueId) {
        Optional<String> optName = Optional.empty();
        try {
            optName = UserIO.loadName(Lantern.getGame().getSavesDirectory(), uniqueId);
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while loading the player data for {}", uniqueId, e);
        }
        return new ProxyUser(new LanternGameProfile(uniqueId, optName.orElse("UNKNOWN")));
    }

    private Collection<GameProfile> getAllProfiles() {
        final ImmutableSet.Builder<GameProfile> profiles = ImmutableSet.builder();
        profiles.addAll(this.server.getOnlinePlayers().stream()
                .map(Player::getProfile).collect(Collectors.toSet()));
        profiles.addAll(this.banService.get().getBans().stream()
                .filter(entry -> entry instanceof BanEntry.Profile)
                .map(entry -> ((BanEntry.Profile) entry).getProfile())
                .collect(Collectors.toSet()));
        profiles.addAll(this.whitelistService.get().getWhitelistedProfiles());
        return profiles.build();
    }

    @Override
    public Optional<User> get(UUID uniqueId) {
        return Optional.ofNullable(getUser(uniqueId));
    }

    @Override
    public Optional<User> get(String lastKnownName) {
        checkNotNull(lastKnownName, "lastKnownName");
        checkArgument(lastKnownName.length() >= 3 && lastKnownName.length() <= 16,
                "Invalid username %s", lastKnownName);
        return Optional.ofNullable(this.profileManager.getCache().getByName(lastKnownName)
                .map(p -> getUser(p.getUniqueId())).orElse(null));
    }

    @Override
    public Optional<User> get(GameProfile profile) {
        return Optional.ofNullable(getUser(checkNotNull(profile, "profile").getUniqueId()));
    }

    @Override
    public User getOrCreate(GameProfile profile) {
        final Optional<User> user = get(profile);
        if (user.isPresent()) {
            return user.get();
        }
        try {
            return this.userCache.get(profile.getUniqueId(), () -> new ProxyUser(profile));
        } catch (ExecutionException e) {
            throw UncheckedThrowables.throwUnchecked(e);
        }
    }

    @Override
    public Collection<GameProfile> getAll() {
        return getAllProfiles();
    }

    @Override
    public boolean delete(GameProfile profile) {
        // Ops Config
        this.opsConfig.removeEntry(profile.getUniqueId());
        // Whitelist Service
        this.whitelistService.get().removeProfile(profile);
        // Ban Service
        final BanService banService = this.banService.get();
        banService.getBanFor(profile).ifPresent(banService::removeBan);
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
        }).collect(ImmutableList.toImmutableList());
    }

    @Override
    public void close() {
        // Cleanup and save all the active user data
        this.userCache.invalidateAll();
    }
}
