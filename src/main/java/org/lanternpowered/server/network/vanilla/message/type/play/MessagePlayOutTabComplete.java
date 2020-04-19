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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayOutTabComplete implements Message {

    private final List<Match> matches;
    private final int id;
    private final int start;
    private final int length;

    public MessagePlayOutTabComplete(List<Match> matches, int id, int start, int length) {
        this.matches = ImmutableList.copyOf(matches);
        this.id = id;
        this.start = start;
        this.length = length;
    }

    public List<Match> getMatches() {
        return this.matches;
    }

    public int getId() {
        return this.id;
    }

    public int getStart() {
        return this.start;
    }

    public int getLength() {
        return this.length;
    }

    public static final class Match {

        private final String value;
        @Nullable private final Text tooltip;

        public Match(String value, @Nullable Text tooltip) {
            this.value = value;
            this.tooltip = tooltip;
        }

        public String getValue() {
            return value;
        }

        public Optional<Text> getTooltip() {
            return Optional.ofNullable(this.tooltip);
        }
    }
}
