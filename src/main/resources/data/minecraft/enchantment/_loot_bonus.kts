import org.lanternpowered.api.item.enchantment.EnchantmentTypes
import org.lanternpowered.api.script.enchantment

// Script files prefixed with _ can be extended

// This a basic (parent) script for all the
// loot bonus enchantments.

enchantment {
    enchantabilityRange {
        val min = 15 + (it - 1) * 9
        val max = min + 50
        min..max
    }
    maxLevel(3)
    compatibilityTester { it != EnchantmentTypes.SILK_TOUCH }
}
