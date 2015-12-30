/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.world;

import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.weather.Weather;

@NonnullByDefault
public final class LanternWeather extends LanternPluginCatalogType implements Weather {

    private final float darkness;
    private final float rainStrength;
    private final float lightningRate;
    private final float thunderRate;

    /**
     * Creates a new weather instance.
     * 
     * <p>The rates of the thunder and lightning are the chance that they occur
     * every tick, but this is called for all the loaded chunks across the world.</p>
     * 
     * @param pluginId the plugin id
     * @param name the name
     * @param rainStrength the rain strength
     * @param darkness the darkness
     * @param lightningRate the lightning strike rate
     * @param thunderRate the thunder (sound) rate
     */
    public LanternWeather(String pluginId, String name, float rainStrength, float darkness,
            float lightningRate, float thunderRate) {
        super(pluginId, name);

        this.lightningRate = lightningRate;
        this.rainStrength = rainStrength;
        this.thunderRate = thunderRate;
        this.darkness = darkness;
    }

    public float getLightningRate() {
        return this.lightningRate;
    }

    public float getThunderRate() {
        return this.thunderRate;
    }

    public float getDarkness() {
        return this.darkness;
    }

    public float getRainStrength() {
        return this.rainStrength;
    }

}
