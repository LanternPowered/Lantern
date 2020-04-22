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
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.Ban.Builder;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.ban.BanTypes;

import java.net.InetAddress;
import java.time.Instant;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class LanternBanBuilder implements Ban.Builder {

    @Nullable private BanType banType;
    @Nullable private GameProfile gameProfile;
    @Nullable private InetAddress address;
    @Nullable private Text reason;
    @Nullable private Text source;
    private Instant startDate;
    @Nullable private Instant expirationDate;

    public LanternBanBuilder() {
        this.reset();
    }

    @Override
    public LanternBanBuilder reset() {
        this.banType = null;
        this.gameProfile = null;
        this.address = null;
        this.startDate = Instant.now();
        this.expirationDate = null;
        this.source = null;
        this.reason = null;
        return this;
    }

    @Override
    public LanternBanBuilder profile(GameProfile gameProfile) {
        checkNotNull(gameProfile, "gameProfile");
        checkState(this.banType == BanTypes.PROFILE, "Cannot set a GameProfile if the BanType is not BanTypes.PROFILE!");
        this.gameProfile = gameProfile;
        return this;
    }

    @Override
    public LanternBanBuilder address(InetAddress address) {
        checkNotNull(address, "address");
        checkState(this.banType == BanTypes.IP, "Cannot set an InetAddress if the BanType is not BanTypes.IP!");
        this.address = address;
        return this;
    }

    @Override
    public LanternBanBuilder type(BanType type) {
        this.banType = checkNotNull(type, "type");
        return this;
    }

    @Override
    public LanternBanBuilder reason(@Nullable Text reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public LanternBanBuilder startDate(Instant date) {
        this.startDate = checkNotNull(date, "date");
        return this;
    }

    @Override
    public LanternBanBuilder expirationDate(@Nullable Instant date) {
        this.expirationDate = date;
        return this;
    }

    @Override
    public LanternBanBuilder source(@Nullable CommandSource source) {
        this.source = source == null ? null : Text.of(source.getName());
        return this;
    }

    @Override
    public LanternBanBuilder source(@Nullable Text source) {
        this.source = source;
        return this;
    }

    @Override
    public BanEntry build() {
        checkState(this.banType != null, "banType is not set");
        if (this.banType == BanTypes.IP.get()) {
            checkState(this.address != null, "address is not set");
            return new BanEntry.Ip(this.address, this.startDate, this.expirationDate, this.source, this.reason);
        } else {
            checkState(this.gameProfile != null, "gameProfile is not set");
            return new BanEntry.Profile((LanternGameProfile) this.gameProfile, this.startDate, this.expirationDate, this.source, this.reason);
        }
    }

    @Override
    public Builder from(Ban value) {
        this.reset();
        this.source = value.getBanSource().orElse(null);
        this.banType = value.getType();
        this.reason = value.getReason().orElse(null);
        this.startDate = value.getCreationDate();
        this.expirationDate = value.getExpirationDate().orElse(null);
        if (this.banType == BanTypes.IP.get()) {
            this.address = ((Ban.Ip) value).getAddress();
        } else {
            this.gameProfile = ((Ban.Profile) value).getProfile();
        }
        return this;
    }
}
