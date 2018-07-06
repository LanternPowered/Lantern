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
package org.lanternpowered.server.effect.firework

import org.lanternpowered.api.effect.firework.FireworkEffect
import org.lanternpowered.api.effect.firework.FireworkEffectBuilder
import org.lanternpowered.api.effect.firework.FireworkShape
import org.lanternpowered.api.effect.firework.FireworkShapes
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.Color
import java.util.ArrayList

class LanternFireworkEffectBuilder : FireworkEffectBuilder {

    private var trail: Boolean = false
    private var flicker: Boolean = false
    private val colors: MutableList<Color> = ArrayList()
    private val fades: MutableList<Color> = ArrayList()
    private var shape: FireworkShape? = null

    init {
        reset()
    }

    override fun trail(trail: Boolean): FireworkEffectBuilder = apply { this.trail = trail }
    override fun flicker(flicker: Boolean): FireworkEffectBuilder = apply { this.flicker = flicker }
    override fun shape(shape: FireworkShape): FireworkEffectBuilder = apply { this.shape = shape }
    override fun color(color: Color): FireworkEffectBuilder = apply { this.colors.add(color) }
    override fun colors(vararg colors: Color): FireworkEffectBuilder = apply { this.colors.addAll(colors) }
    override fun colors(colors: Iterable<Color>): FireworkEffectBuilder = apply { this.colors.addAll(colors) }
    override fun fade(color: Color): FireworkEffectBuilder = apply { this.fades.add(color) }
    override fun fades(vararg colors: Color): FireworkEffectBuilder = apply { this.fades.addAll(colors) }
    override fun fades(colors: Iterable<Color>): FireworkEffectBuilder = apply { this.fades.addAll(colors) }

    override fun build(): FireworkEffect {
        return LanternFireworkEffect(this.flicker, this.trail, this.colors.toImmutableList(), this.fades.toImmutableList(), this.shape!!)
    }

    override fun from(value: FireworkEffect): FireworkEffectBuilder = apply {
        trail(value.trail)
        colors(value.colors)
        fades(value.fadeColors)
        shape(value.shape)
        flicker(value.flickers)
    }

    override fun reset(): FireworkEffectBuilder = apply {
        this.trail = false
        this.flicker = false
        this.colors.clear()
        this.fades.clear()
        this.shape = FireworkShapes.BALL
    }
}
