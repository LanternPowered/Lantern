/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.boss;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class LanternBossBarBuilder implements ServerBossBar.Builder {

    private Text name;
    private float percent;
    private BossBarColor color;
    private BossBarOverlay overlay;
    private boolean darkenSky;
    private boolean playEndBossMusic;
    private boolean createFog;
    private boolean visible;

    public LanternBossBarBuilder() {
        this.reset();
    }

    @Override
    public LanternBossBarBuilder name(Text name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    @Override
    public LanternBossBarBuilder percent(float percent) {
        checkNotNull(percent >= 0f && percent <= 1f, "Percent must be between 0 and 1, but %s is not", percent);
        this.percent = percent;
        return this;
    }

    @Override
    public LanternBossBarBuilder color(BossBarColor color) {
        this.color = checkNotNull(color, "color");
        return this;
    }

    @Override
    public LanternBossBarBuilder overlay(BossBarOverlay overlay) {
        this.overlay = checkNotNull(overlay, "overlay");
        return this;
    }

    @Override
    public LanternBossBarBuilder darkenSky(boolean darkenSky) {
        this.darkenSky = darkenSky;
        return this;
    }

    @Override
    public LanternBossBarBuilder playEndBossMusic(boolean playEndBossMusic) {
        this.playEndBossMusic = playEndBossMusic;
        return this;
    }

    @Override
    public LanternBossBarBuilder createFog(boolean createFog) {
        this.createFog = createFog;
        return this;
    }

    @Override
    public LanternBossBarBuilder visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public LanternBossBar build() {
        checkArgument(this.name != null, "The name is not set");
        return new LanternBossBar(UUID.randomUUID(), this.name, this.color, this.overlay, this.percent,
                this.darkenSky, this.playEndBossMusic, this.createFog, this.visible);
    }

    @Override
    public LanternBossBarBuilder from(ServerBossBar value) {
        this.name = value.getName();
        this.percent = value.getPercent();
        this.visible = value.isVisible();
        this.color = value.getColor();
        this.overlay = value.getOverlay();
        this.createFog = value.shouldCreateFog();
        this.darkenSky = value.shouldDarkenSky();
        this.playEndBossMusic = value.shouldPlayEndBossMusic();
        return this;
    }

    @Override
    public LanternBossBarBuilder reset() {
        this.name = null;
        this.percent = 0;
        this.visible = true;
        this.color = BossBarColors.WHITE;
        this.overlay = BossBarOverlays.PROGRESS;
        this.createFog = false;
        this.darkenSky = false;
        this.playEndBossMusic = false;
        return this;
    }
}
