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
package org.lanternpowered.api.asset;

import org.spongepowered.api.plugin.Plugin;

import java.util.regex.Pattern;

public interface Asset extends org.spongepowered.api.asset.Asset {

    /**
     * The id pattern of an {@link Asset}.
     */
    Pattern ID_PATTERN = Pattern.compile(Plugin.ID_PATTERN + ":[a-z0-9\\-/._]+");

    /**
     * Gets the id that represents the resource.
     *
     * <p>In the vanilla resource system this would
     * return 'plugin:path/to/resource.json'.
     * For example: 'minecraft:lang/en_us.properties'
     * <p/>
     *
     * @return The resource key
     */
    String getId();
}
