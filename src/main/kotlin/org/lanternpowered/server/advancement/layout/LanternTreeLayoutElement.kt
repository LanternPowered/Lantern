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
package org.lanternpowered.server.advancement.layout

import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.advancement.Advancement
import org.spongepowered.api.advancement.TreeLayoutElement
import org.spongepowered.math.vector.Vector2d

class LanternTreeLayoutElement(private val advancement: Advancement) : TreeLayoutElement {

    private var position = Vector2d.ZERO

    override fun getAdvancement(): Advancement = this.advancement
    override fun getPosition(): Vector2d = this.position

    override fun setPosition(x: Double, y: Double) {
        this.position = Vector2d(x, y)
    }

    override fun setPosition(position: Vector2d) {
        this.position = position
    }

    override fun toString(): String = ToStringHelper(this)
            .add("advancement", this.advancement.key)
            .add("position", this.position)
            .toString()

}
