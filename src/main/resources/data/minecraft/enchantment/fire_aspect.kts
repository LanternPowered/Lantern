import org.lanternpowered.api.ext.*
import org.lanternpowered.api.script.enchantment

enchantment {
    name(translationOf("enchantment.fire"))
    maxLevel(2)
    enchantabilityRange {
        val min = 10 + (it - 1) * 20
        val max = min + 50
        min..max
    }
}
