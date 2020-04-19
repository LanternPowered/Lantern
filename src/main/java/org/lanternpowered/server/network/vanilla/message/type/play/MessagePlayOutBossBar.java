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
        private final boolean createFog;

        public Add(UUID uniqueId, Text title, BossBarColor color, BossBarOverlay overlay, float health,
                boolean darkenSky, boolean endMusic, boolean createFog) {
            super(uniqueId);
            this.title = title;
            this.color = color;
            this.overlay = overlay;
            this.health = health;
            this.darkenSky = darkenSky;
            this.endMusic = endMusic;
            this.createFog = createFog;
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

        public boolean shouldCreateFog() {
            return this.createFog;
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
        private final boolean createFog;

        public UpdateMisc(UUID uniqueId, boolean darkenSky, boolean endMusic, boolean createFog) {
            super(uniqueId);
            this.darkenSky = darkenSky;
            this.endMusic = endMusic;
            this.createFog = createFog;
        }

        public boolean isDarkenSky() {
            return this.darkenSky;
        }

        public boolean isEndMusic() {
            return this.endMusic;
        }

        public boolean shouldCreateFog() {
            return this.createFog;
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
