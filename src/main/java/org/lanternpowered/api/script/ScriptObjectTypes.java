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

import org.spongepowered.api.world.weather.Weather;

/**
 * An enumeration of all the supported object types that can
 * be registered through the {@link ScriptGameRegistry#register} methods.
 */
public final class ScriptObjectTypes {

    public static final Class<Weather> WEATHER = Weather.class;

    private ScriptObjectTypes() {
    }
}
