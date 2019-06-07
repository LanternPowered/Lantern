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
