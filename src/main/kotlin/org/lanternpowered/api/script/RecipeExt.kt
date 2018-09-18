/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.api.script

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe

fun shapedRecipe(fn: ShapedCraftingRecipe.Builder.() -> Unit): ShapedCraftingRecipe.Builder = ShapedCraftingRecipe.builder().apply(fn)

fun ShapedCraftingRecipe.Builder.result(result: ItemStackSnapshot): ShapedCraftingRecipe.Builder
        = (this as ShapedCraftingRecipe.Builder.ResultStep).result(result)

fun ShapedCraftingRecipe.Builder.result(result: ItemStack): ShapedCraftingRecipe.Builder
        = (this as ShapedCraftingRecipe.Builder.ResultStep).result(result)

fun ShapedCraftingRecipe.Builder.aisle(vararg aisle: String): ShapedCraftingRecipe.Builder
        = (this as ShapedCraftingRecipe.Builder.AisleStep).aisle(*aisle)

fun ShapedCraftingRecipe.Builder.ingredients(vararg pairs: Pair<Char, Ingredient>): ShapedCraftingRecipe.Builder
        = (this as ShapedCraftingRecipe.Builder.AisleStep).where(mapOf(*pairs))

fun ShapedCraftingRecipe.Builder.ingredient(key: Char, ingredient: Ingredient): ShapedCraftingRecipe.Builder
        = (this as ShapedCraftingRecipe.Builder.AisleStep).where(key, ingredient)

fun ShapedCraftingRecipe.Builder.group(group: String?): ShapedCraftingRecipe.Builder
        = (this as ShapedCraftingRecipe.Builder.EndStep).group(group)
