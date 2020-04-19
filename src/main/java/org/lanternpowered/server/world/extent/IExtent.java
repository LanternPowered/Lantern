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
package org.lanternpowered.server.world.extent;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.extent.Extent;

import java.util.Collection;
import java.util.function.Predicate;

public interface IExtent extends Extent {

    /**
     * Gets the collision boxes of the block
     * at the given coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return The collision boxes, or empty if none were found
     */
    Collection<AABB> getBlockCollisionBoxes(int x, int y, int z);

    default boolean hasIntersectingEntities(AABB box) {
        return hasIntersectingEntities(box, entity -> true);
    }

    boolean hasIntersectingEntities(AABB box, Predicate<Entity> filter);
}
