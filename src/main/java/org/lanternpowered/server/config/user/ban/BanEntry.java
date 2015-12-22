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
package org.lanternpowered.server.config.user.ban;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.Date;
import java.util.Optional;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.ban.BanTypes;

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
    private Text reason;

    @Nullable
    @Setting(value = "source")
    private Text source;

    private volatile WeakReference<CommandSource> commandSource;

    protected BanEntry() {
    }

    protected BanEntry(Text reason, Date startDate, @Nullable Date expirationDate,
            @Nullable Text source) {
        this.expirationDate = expirationDate;
        this.startDate = startDate;
        this.source = source;
        this.reason = reason;
    }

    @Override
    public BanType getType() {
        return this instanceof Ban.Ip ? BanTypes.IP : BanTypes.PROFILE;
    }

    @Override
    public Text getReason() {
        return this.reason;
    }

    @Override
    public Date getCreationDate() {
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
        String plainSource = Texts.legacy().to(this.source);
        if (plainSource.equals("Server")) {
            source = LanternGame.get().getServer().getConsole();
        } else {
            source = LanternGame.get().getServer().getPlayer(plainSource).orElse(null);
        }
        if (source != null) {
            this.commandSource = new WeakReference<>(source);
        }
        return Optional.ofNullable(source);
    }

    @ConfigSerializable
    public static class Ip extends BanEntry implements Ban.Ip {

        @Setting(value = "ip")
        private InetAddress ip;

        // It is actually used...
        @SuppressWarnings("unused")
        private Ip() {
        }

        public Ip(InetAddress ipAddress, Text reason, Date startDate, @Nullable Date expirationDate,
                @Nullable Text source) {
            super(reason, startDate, expirationDate, source);
        }

        @Override
        public InetAddress getAddress() {
            return this.ip;
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

        public Profile(LanternGameProfile profile, Text reason, Date startDate, @Nullable Date expirationDate,
                @Nullable Text source) {
            super(reason, startDate, expirationDate, source);
            this.profile = profile;
        }

        public LanternGameProfile getProfile() {
            return this.profile;
        }
    }
}
