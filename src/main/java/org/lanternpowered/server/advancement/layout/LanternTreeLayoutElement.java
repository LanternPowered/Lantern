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
package org.lanternpowered.server.advancement.layout;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.TreeLayoutElement;
import org.spongepowered.math.vector.Vector2d;

public class LanternTreeLayoutElement implements TreeLayoutElement {

    private final Advancement advancement;
    private Vector2d position = Vector2d.ZERO;

    public LanternTreeLayoutElement(Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public Advancement getAdvancement() {
        return this.advancement;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(double x, double y) {
        this.position = new Vector2d(x, y);
    }

    @Override
    public void setPosition(Vector2d position) {
        checkNotNull(position, "position");
        this.position = position;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("advancement", this.advancement.getKey())
                .add("position", this.position)
                .toString();
    }
}
