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
package org.lanternpowered.api.script;

import org.lanternpowered.api.asset.Asset;

import java.util.Optional;

public interface Script<T> {

    /**
     * Gets the compiled script instance.
     *
     * @return The script instance
     */
    T get();

    /**
     * Gets the {@link Asset} this script was constructed from, if present.
     *
     * @return The asset
     */
    Optional<Asset> getAsset();
}
