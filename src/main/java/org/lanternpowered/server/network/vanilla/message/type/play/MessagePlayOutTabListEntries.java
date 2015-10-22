/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.network.vanilla.message.type.play;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGameProfile.Property;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;

import com.google.common.collect.ImmutableList;

public final class MessagePlayOutTabListEntries implements Message {

    private final ImmutableList<Entry> entries;

    public MessagePlayOutTabListEntries(Iterable<Entry> entries) {
        this.entries = ImmutableList.copyOf(entries);
    }

    public ImmutableList<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {

        private final UUID uniqueId;

        @Nullable private final String name;
        @Nullable private final Collection<Property> properties;
        @Nullable private final GameMode gameMode;
        @Nullable private final Text displayName;
        @Nullable private final Integer ping;

        Entry(UUID uniqueId, @Nullable String name, @Nullable Collection<Property> properties,
                @Nullable GameMode gameMode, @Nullable Text displayName, @Nullable Integer ping) {
            this.properties = properties == null ? null : ImmutableList.copyOf(properties);
            this.displayName = displayName;
            this.uniqueId = uniqueId;
            this.gameMode = gameMode;
            this.name = name;
            this.ping = ping;
        }

        @Nullable
        public Collection<Property> getProperties() {
            return this.properties;
        }

        @Nullable
        public UUID getUniqueId() {
            return this.uniqueId;
        }

        @Nullable
        public String getName() {
            return this.name;
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

            public Add(UUID uniqueId, String name, Collection<Property> properties, GameMode gameMode,
                    @Nullable Text displayName, Integer ping) {
                super(uniqueId, name, properties, gameMode, displayName, ping);
            }
        }

        public static final class UpdateGameMode extends Entry {

            public UpdateGameMode(UUID uniqueId, GameMode gameMode) {
                super(uniqueId, null, null, gameMode, null, null);
            }
        }

        public static final class UpdateLatency extends Entry {

            public UpdateLatency(UUID uniqueId, int ping) {
                super(uniqueId, null, null, null, null, ping);
            }
        }

        public static final class UpdateDisplayName extends Entry {

            public UpdateDisplayName(UUID uniqueId, @Nullable Text displayName) {
                super(uniqueId, null, null, null, displayName, null);
            }
        }

        public static final class Remove extends Entry {

            public Remove(UUID uniqueId) {
                super(uniqueId, null, null, null, null, null);
            }
        }
    }
}
