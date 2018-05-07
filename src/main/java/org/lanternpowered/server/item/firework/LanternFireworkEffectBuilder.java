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
package org.lanternpowered.server.item.firework;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.util.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanternFireworkEffectBuilder implements FireworkEffect.Builder {

    private boolean trail;
    private boolean flicker;
    private List<Color> colors;
    private List<Color> fades;
    private FireworkShape shape;

    public LanternFireworkEffectBuilder() {
        reset();
    }

    @Override
    public LanternFireworkEffectBuilder trail(boolean trail) {
        this.trail = trail;
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder flicker(boolean flicker) {
        this.flicker = flicker;
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder color(Color color) {
        checkNotNull(color);
        this.colors.add(color);
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder colors(Color... colors) {
        checkNotNull(colors);
        Collections.addAll(this.colors, colors);
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder colors(Iterable<Color> colors) {
        checkNotNull(colors);
        for (Color color : colors) {
            this.colors.add(color);
        }
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder fade(Color color) {
        checkNotNull(color);
        this.fades.add(color);
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder fades(Color... colors) {
        checkNotNull(colors);
        Collections.addAll(this.fades, colors);
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder fades(Iterable<Color> colors) {
        checkNotNull(colors);
        for (Color color : colors) {
            this.fades.add(color);
        }
        return this;
    }

    @Override
    public LanternFireworkEffectBuilder shape(FireworkShape shape) {
        this.shape = checkNotNull(shape);
        return this;
    }

    @Override
    public FireworkEffect build() {
        return new LanternFireworkEffect(this.flicker, this.trail, this.colors, this.fades, this.shape);
    }

    @Override
    public FireworkEffect.Builder from(FireworkEffect value) {
        return trail(value.hasTrail())
                .colors(value.getColors())
                .fades(value.getFadeColors())
                .shape(value.getShape())
                .flicker(value.flickers());
    }

    @Override
    public LanternFireworkEffectBuilder reset() {
        this.trail = false;
        this.flicker = false;
        this.colors = new ArrayList<>();
        this.fades = new ArrayList<>();
        this.shape = FireworkShapes.BALL;
        return this;
    }
}
