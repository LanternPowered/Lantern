import org.lanternpowered.api.ext.*
import org.lanternpowered.api.script.weather
import org.lanternpowered.api.world.weather.WeatherOptions

weather {
    name("Clear")

    option(WeatherOptions.DURATION) {
        random.nextDouble(300.0 .. 900.0)
    }
    option(WeatherOptions.WEIGHT, 500)
}
