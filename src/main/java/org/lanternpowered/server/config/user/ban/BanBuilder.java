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
package org.lanternpowered.server.config.user.ban;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.InetAddress;
import java.util.Date;

import javax.annotation.Nullable;

import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.ban.BanTypes;

public final class BanBuilder implements Ban.Builder {

    private BanType banType;
    private GameProfile gameProfile;
    private InetAddress address;
    private Text reason;
    @Nullable private Text source;
    private Date startDate;
    @Nullable private Date expirationDate;

    public BanBuilder() {
        this.reset();
    }

    @Override
    public BanBuilder reset() {
        this.banType = null;
        this.gameProfile = null;
        this.address = null;
        this.startDate = new Date();
        this.expirationDate = null;
        this.source = null;
        this.reason = null;
        return this;
    }

    @Override
    public BanBuilder profile(GameProfile gameProfile) {
        checkNotNull(gameProfile, "gameProfile");
        checkState(this.banType == BanTypes.PROFILE, "Cannot set a GameProfile if the BanType is not BanTypes.PROFILE!");
        this.gameProfile = gameProfile;
        return this;
    }

    @Override
    public BanBuilder address(InetAddress address) {
        checkNotNull(address, "address");
        checkState(this.banType == BanTypes.IP, "Cannot set an InetAddress if the BanType is not BanTypes.IP!");
        this.address = address;
        return this;
    }

    @Override
    public BanBuilder type(BanType type) {
        this.banType = checkNotNull(type, "type");
        return this;
    }

    @Override
    public BanBuilder reason(Text reason) {
        this.reason = checkNotNull(reason, "reason");
        return this;
    }

    @Override
    public BanBuilder startDate(Date date) {
        this.startDate = checkNotNull(date, "date");
        return this;
    }

    @Override
    public BanBuilder expirationDate(@Nullable Date date) {
        this.expirationDate = date;
        return this;
    }

    @Override
    public BanBuilder source(@Nullable CommandSource source) {
        this.source = source == null ? null : Texts.of(source.getName());
        return this;
    }

    @Override
    public BanBuilder source(@Nullable Text source) {
        this.source = source;
        return this;
    }

    @Override
    public BanEntry build() {
        checkState(this.banType != null, "banType is not set");
        checkState(this.startDate != null, "startDate is not set");
        checkState(this.reason != null, "reason is not set");
        if (this.banType == BanTypes.IP) {
            checkState(this.address != null, "address is not set");
            return new BanEntry.Ip(this.address, this.reason, this.startDate, this.expirationDate, this.source);
        } else {
            checkState(this.gameProfile != null, "gameProfile is not set");
            return new BanEntry.Profile((LanternGameProfile) this.gameProfile, this.reason, this.startDate, this.expirationDate, this.source);
        }
    }
}
