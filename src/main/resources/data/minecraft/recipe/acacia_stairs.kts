package data.minecraft.recipe

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.script.*
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.recipe.crafting.Ingredient

shapedRecipe {
    result(ItemStack(ItemTypes.OAK_STAIRS, 8))
    group("wooden_stairs")
    aisle(
        "#  ",
        "## ",
        "###"
    )
    ingredient('#', Ingredient.of(ItemTypes.LOG))
}
