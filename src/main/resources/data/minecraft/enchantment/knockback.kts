import org.lanternpowered.api.ext.*
import org.lanternpowered.api.script.enchantment

enchantment {
    name(translationOf("enchantment.knockback"))
    maxLevel(2)
    enchantabilityRange {
        val min = 5 + (it - 1) * 20
        val max = min + 50
        min..max
    }
}
