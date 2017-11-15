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
package org.lanternpowered.server.network.vanilla.recipe;

public final class NetworkRecipeTypes {

    static final String CRAFTING_SHAPED = "crafting_shaped";
    static final String CRAFTING_SHAPELESS = "crafting_shapeless";
    static final String SMELTING = "smelting";

    // Special cases
    public static final String ARMOR_DYE = "crafting_special_armordye";
    public static final String BOOK_CLONING = "crafting_special_bookcloning";
    public static final String MAP_CLONING = "crafting_special_mapcloning";
    public static final String MAP_EXTENDING = "crafting_special_mapextending";
    public static final String FIREWORK_ROCKET = "crafting_special_firework_rocket";
    public static final String FIREWORK_STAR = "crafting_special_firework_star";
    public static final String FIREWORK_STAR_FADE = "crafting_special_firework_star_fade";
    public static final String REPAIR_ITEM = "crafting_special_repairitem";
    public static final String TIPPED_ARROW = "crafting_special_tippedarrow";
    public static final String BANNER_DUPLICATE = "crafting_special_bannerduplicate";
    public static final String BANNER_ADD_PATTERN = "crafting_special_banneraddpattern";
    public static final String SHIELD_DECORATION = "crafting_special_shielddecoration";
    public static final String SHULKER_BOX_COLORING = "crafting_special_shulkerboxcoloring";
}
