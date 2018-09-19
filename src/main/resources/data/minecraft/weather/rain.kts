import org.lanternpowered.api.ext.*
import org.lanternpowered.api.script.weather
import org.lanternpowered.api.world.weather.WeatherOptions

weather {
    name("Rain")

    option(WeatherOptions.RAIN_STRENGTH, 1.0)
    option(WeatherOptions.DURATION) {
        random.nextDouble(250.0 .. 500.0)
    }
    option(WeatherOptions.WEIGHT, 300)
}
