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

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.Date;
import java.util.Optional;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public abstract class BanEntry implements Ban {

    @Setting(value = "start-date")
    private Date startDate;

    @Nullable
    @Setting(value = "expiration-date")
    private Date expirationDate;

    @Setting(value = "reason")
    private String reason;

    private volatile Text.Literal textReason;

    protected BanEntry() {
    }

    protected BanEntry(Text.Literal reason, Date startDate, @Nullable Date expirationDate) {
        this.reason = Texts.toPlain(reason);
        this.textReason = reason;
        this.expirationDate = expirationDate;
        this.startDate = startDate;
    }

    @Override
    public BanType getType() {
        return this instanceof Ban.Ip ? BanType.IP_BAN : BanType.USER_BAN;
    }

    @Override
    public Text.Literal getReason() {
        if (this.textReason == null) {
            this.textReason = Texts.of(this.reason);
        }
        return this.textReason;
    }

    @Override
    public Date getStartDate() {
        return this.startDate;
    }

    @Override
    public Optional<Date> getExpirationDate() {
        return Optional.ofNullable(this.expirationDate);
    }

    @Override
    public boolean isIndefinite() {
        return this.expirationDate == null;
    }

    @ConfigSerializable
    public static class Ip extends BanEntry implements Ban.Ip {

        @Setting(value = "ip")
        private InetAddress ip;

        private final WeakReference<CommandSource> commandSource;

        // It is actually used...
        @SuppressWarnings("unused")
        private Ip() {
            this.commandSource = null;
        }

        public Ip(InetAddress ipAddress, Text.Literal reason, Date startDate, @Nullable Date expirationDate,
                @Nullable CommandSource commandSource) {
            super(reason, startDate, expirationDate);
            this.commandSource = new WeakReference<>(commandSource);
        }

        @Override
        public Optional<CommandSource> getSource() {
            return Optional.ofNullable(this.commandSource == null ? null : this.commandSource.get());
        }

        @Override
        public InetAddress getAddress() {
            return this.ip;
        }
    }

    @ConfigSerializable
    public static class User extends BanEntry implements Ban.User {

        @Setting(value = "profile")
        private LanternGameProfile profile;

        // It is actually used...
        @SuppressWarnings("unused")
        private User() {
        }

        public User(org.spongepowered.api.entity.living.player.User user, Text.Literal reason, Date startDate,
                @Nullable Date expirationDate, @Nullable CommandSource commandSource) {
            super(reason, startDate, expirationDate);
            this.profile = (LanternGameProfile) user.getProfile();
        }

        public LanternGameProfile getProfile() {
            return this.profile;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public Optional<CommandSource> getSource() {
            return (Optional) LanternGame.get().getServer().getPlayer(this.profile.getUniqueId());
        }

        @Override
        public org.spongepowered.api.entity.living.player.User getUser() {
            return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getOrCreate(this.profile);
        }
    }
}
