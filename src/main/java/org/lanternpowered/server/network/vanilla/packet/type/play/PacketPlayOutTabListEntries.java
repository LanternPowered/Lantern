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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.message.Packet;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class PacketPlayOutTabListEntries implements Packet {

    private final ImmutableList<Entry> entries;

    public PacketPlayOutTabListEntries(Iterable<Entry> entries) {
        this.entries = ImmutableList.copyOf(entries);
    }

    public ImmutableList<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {

        private final GameProfile gameProfile;
        @Nullable private final GameMode gameMode;
        @Nullable private final Text displayName;
        @Nullable private final Integer ping;

        Entry(GameProfile gameProfile, @Nullable GameMode gameMode, @Nullable Text displayName, @Nullable Integer ping) {
            this.displayName = displayName;
            this.gameProfile = gameProfile;
            this.gameMode = gameMode;
            this.ping = ping;
        }

        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        @Nullable
        public GameMode getGameMode() {
            return this.gameMode;
        }

        @Nullable
        public Text getDisplayName() {
            return this.displayName;
        }

        @Nullable
        public Integer getPing() {
            return this.ping;
        }

        public static final class Add extends Entry {

            public Add(GameProfile gameProfile, GameMode gameMode, @Nullable Text displayName, int ping) {
                super(gameProfile, gameMode, displayName, ping);
            }
        }

        public static final class UpdateGameMode extends Entry {

            public UpdateGameMode(GameProfile gameProfile, GameMode gameMode) {
                super(gameProfile, gameMode, null, null);
            }
        }

        public static final class UpdateLatency extends Entry {

            public UpdateLatency(GameProfile gameProfile, int ping) {
                super(gameProfile, null, null, ping);
            }
        }

        public static final class UpdateDisplayName extends Entry {

            public UpdateDisplayName(GameProfile gameProfile, @Nullable Text displayName) {
                super(gameProfile, null, displayName, null);
            }
        }

        public static final class Remove extends Entry {

            public Remove(GameProfile gameProfile) {
                super(gameProfile, null, null, null);
            }
        }
    }
}
