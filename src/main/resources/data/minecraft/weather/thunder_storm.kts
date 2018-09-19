import org.lanternpowered.api.entity.weather.LightningSpawner
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.script.weather
import org.lanternpowered.api.world.weather.WeatherOptions

weather {
    name("Thunder Storm")

    option(WeatherOptions.RAIN_STRENGTH, 1.0)
    option(WeatherOptions.SKY_DARKNESS, 1.0)
    option(WeatherOptions.DURATION) {
        random.nextDouble(200.0 .. 300.0)
    }
    option(WeatherOptions.WEIGHT, 150)

    action { world ->
        // Only strike lighting if the sky darkness has transitioned above 0.8
        if (world.skyDarkness < 0.8) return@action

        // Spawn lightning in the world
        LightningSpawner.spawnLightning(world, chance = 0.000004, attemptsPerChunk = 2)
    }
}
