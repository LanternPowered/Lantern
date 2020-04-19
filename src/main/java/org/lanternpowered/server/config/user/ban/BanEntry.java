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
package org.lanternpowered.server.config.user.ban;

import static com.google.common.base.Preconditions.checkNotNull;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.console.LanternConsole;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.ban.BanTypes;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@ConfigSerializable
public abstract class BanEntry implements Ban {

    @Setting(value = "start-date")
    private Instant startDate;

    @Nullable
    @Setting(value = "expiration-date")
    private Instant expirationDate;

    @Nullable
    @Setting(value = "reason")
    private Text reason;

    @Nullable
    @Setting(value = "source")
    private Text source;

    @Nullable private volatile WeakReference<CommandSource> commandSource;

    protected BanEntry() {
    }

    protected BanEntry(Instant startDate, @Nullable Instant expirationDate,
            @Nullable Text source, @Nullable Text reason) {
        this.startDate = checkNotNull(startDate, "startDate");
        this.expirationDate = expirationDate;
        this.source = source;
        this.reason = reason;
    }

    /**
     * Gets whether this ban entry is expired.
     *
     * @return Is expired
     */
    public boolean isExpired() {
        return this.expirationDate != null && Instant.now().compareTo(this.expirationDate) > 0;
    }

    @Override
    public BanType getType() {
        return this instanceof Ban.Ip ? BanTypes.IP : BanTypes.PROFILE;
    }

    @Override
    public Optional<Text> getReason() {
        return Optional.ofNullable(this.reason);
    }

    @Override
    public Instant getCreationDate() {
        return this.startDate;
    }

    @Override
    public Optional<Instant> getExpirationDate() {
        return Optional.ofNullable(this.expirationDate);
    }

    @Override
    public boolean isIndefinite() {
        return this.expirationDate == null;
    }

    @Override
    public Optional<Text> getBanSource() {
        return Optional.ofNullable(this.source);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Optional<CommandSource> getBanCommandSource() {
        if (this.source == null) {
            return Optional.empty();
        }
        CommandSource source;
        if (this.commandSource != null && (source = this.commandSource.get()) != null) {
            return Optional.of(source);
        }
        final String plainSource = LanternTexts.toLegacy(this.source);
        if (plainSource.equals(LanternConsole.INSTANCE.getName())) {
            source = Sponge.getServer().getConsole();
        } else {
            source = Sponge.getServer().getPlayer(plainSource).orElse(null);
        }
        if (source != null) {
            this.commandSource = new WeakReference<>(source);
        }
        return Optional.ofNullable(source);
    }

    @ConfigSerializable
    public static class Ip extends BanEntry implements Ban.Ip {

        @Setting(value = "ip")
        private InetAddress ipAddress;

        // It is actually used...
        @SuppressWarnings("unused")
        private Ip() {
        }

        public Ip(InetAddress ipAddress, Instant startDate, @Nullable Instant expirationDate,
                @Nullable Text source, @Nullable Text reason) {
            super(startDate, expirationDate, source, reason);
            this.ipAddress = checkNotNull(ipAddress, "ipAddress");
        }

        @Override
        public InetAddress getAddress() {
            return this.ipAddress;
        }
    }

    @ConfigSerializable
    public static class Profile extends BanEntry implements Ban.Profile {

        @Setting(value = "profile")
        private LanternGameProfile profile;

        // It is actually used...
        @SuppressWarnings("unused")
        private Profile() {
        }

        public Profile(LanternGameProfile profile, Instant startDate, @Nullable Instant expirationDate,
                @Nullable Text source, @Nullable Text reason) {
            super(startDate, expirationDate, source, reason);
            this.profile = checkNotNull(profile, "profile");
        }

        public LanternGameProfile getProfile() {
            return this.profile;
        }

    }

}
