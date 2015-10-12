package org.lanternpowered.server.world;

import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.spongepowered.api.world.weather.Weather;

public class LanternWeather extends LanternPluginCatalogType implements Weather {

    private final float darkness;
    private final float rainStrength;
    private final float lightningRate;
    private final float thunderRate;

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
