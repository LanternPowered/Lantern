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

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public abstract class MessagePlayOutBossBar implements Message {

    public static final class Add extends MessagePlayOutBossBar {

        private final Text title;
        private final BossBarColor color;
        private final BossBarOverlay overlay;
        private final float health;
        private final boolean darkenSky;
        private final boolean endMusic;

        public Add(UUID uniqueId, Text title, BossBarColor color, BossBarOverlay overlay, float health,
                boolean darkenSky, boolean endMusic) {
            super(uniqueId);
            this.title = title;
            this.color = color;
            this.overlay = overlay;
            this.health = health;
            this.darkenSky = darkenSky;
            this.endMusic = endMusic;
        }

        public Text getTitle() {
            return this.title;
        }

        public BossBarColor getColor() {
            return this.color;
        }

        public BossBarOverlay getOverlay() {
            return this.overlay;
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

    public static final class UpdatePercent extends MessagePlayOutBossBar {

        private final float percent;

        public UpdatePercent(UUID uniqueId, float percent) {
            super(uniqueId);
            this.percent = percent;
        }

        public float getPercent() {
            return this.percent;
        }
    }

    public static final class UpdateTitle extends MessagePlayOutBossBar {

        private final Text title;

        public UpdateTitle(UUID uniqueId, Text title) {
            super(uniqueId);
            this.title = title;
        }

        public Text getTitle() {
            return this.title;
        }
    }

    public static final class UpdateStyle extends MessagePlayOutBossBar {

        private final BossBarColor color;
        private final BossBarOverlay overlay;

        public UpdateStyle(UUID uniqueId, BossBarColor color, BossBarOverlay overlay) {
            super(uniqueId);
            this.color = color;
            this.overlay = overlay;
        }

        public BossBarColor getColor() {
            return this.color;
        }

        public BossBarOverlay getOverlay() {
            return this.overlay;
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
}
