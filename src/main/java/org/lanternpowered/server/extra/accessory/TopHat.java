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
package org.lanternpowered.server.extra.accessory;

import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.util.annotation.CatalogedBy;

import java.util.Optional;

/**
 * Represents a {@link TopHat}.
 */
@CatalogedBy(TopHats.class)
public interface TopHat extends Accessory {

    /**
     * Gets the {@link DyeColor}, if present.
     *
     * @return The dye color
     */
    Optional<DyeColor> getDyeColor();
}
