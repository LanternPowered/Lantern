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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.objects.LocalizedText;

import java.util.UUID;

public abstract class MessagePlayOutBossBar implements Message {

    public static final class Add extends MessagePlayOutBossBar {

        private final LocalizedText title;
        private final Color color;
        private final Division division;
        private final float health;
        private final boolean darkenSky;
        private final boolean endMusic;

        public Add(UUID uniqueId, LocalizedText title, Color color, Division division, float health,
                boolean darkenSky, boolean endMusic) {
            super(uniqueId);
            this.title = title;
            this.color = color;
            this.division = division;
            this.health = health;
            this.darkenSky = darkenSky;
            this.endMusic = endMusic;
        }


        public LocalizedText getTitle() {
            return this.title;
        }

        public Color getColor() {
            return this.color;
        }

        public Division getDivision() {
            return this.division;
        }

        public boolean isDarkenSky() {
            return this.darkenSky;
        }

        public boolean isEndMusic() {
            return this.endMusic;
        }

        public float getHealth() {
            return this.health;
        }
    }

    public static final class Remove extends MessagePlayOutBossBar {

        public Remove(UUID uniqueId) {
            super(uniqueId);
        }
    }

    public static final class UpdateHealth extends MessagePlayOutBossBar {

        private final float health;

        public UpdateHealth(UUID uniqueId, float health) {
            super(uniqueId);
            this.health = health;
        }

        public float getHealth() {
            return this.health;
        }
    }

    public static final class UpdateTitle extends MessagePlayOutBossBar {

        private final LocalizedText title;

        public UpdateTitle(UUID uniqueId, LocalizedText title) {
            super(uniqueId);
            this.title = title;
        }

        public LocalizedText getTitle() {
            return this.title;
        }
    }

    public static final class UpdateStyle extends MessagePlayOutBossBar {

        private final Color color;
        private final Division division;

        public UpdateStyle(UUID uniqueId, Color color, Division division) {
            super(uniqueId);
            this.color = color;
            this.division = division;
        }

        public Color getColor() {
            return this.color;
        }

        public Division getDivision() {
            return this.division;
        }
    }

    public static final class UpdateMisc extends MessagePlayOutBossBar {

        private final boolean darkenSky;
        private final boolean endMusic;

        public UpdateMisc(UUID uniqueId, boolean darkenSky, boolean endMusic) {
            super(uniqueId);
            this.darkenSky = darkenSky;
            this.endMusic = endMusic;
        }

        public boolean isDarkenSky() {
            return this.darkenSky;
        }

        public boolean isEndMusic() {
            return this.endMusic;
        }
    }

    private final UUID uniqueId;

    MessagePlayOutBossBar(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public enum Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE,
    }

    public enum Division {
        NONE,
        SIX,
        TEN,
        TWELVE,
        TWENTY,
    }
}
