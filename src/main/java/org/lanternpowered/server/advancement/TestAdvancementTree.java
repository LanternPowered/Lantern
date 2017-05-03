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
package org.lanternpowered.server.advancement;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;

public final class TestAdvancementTree {

    public static final AdvancementTree A;
    public static final AdvancementTree B;

    public static final Advancement DIG_DIRT;
    public static final AdvancementCriterion[] DIG_DIRT_CRITERIA;

    static {
        A = AdvancementTree.builder()
                .title(Text.of("Rule!"))
                .description(Text.of("Build a kingdom."))
                .icon(ItemTypes.GOLDEN_APPLE)
                .build("test", "king_of_the_hill");
        final Advancement becomeKing = Advancement.builder()
                .icon(ItemTypes.DIAMOND)
                .title(Text.of("Become King"))
                .description(Text.of("Claim a piece of land."))
                .build("test", "become_king");
        final Advancement prepareCake = Advancement.builder()
                .parent(becomeKing)
                .icon(ItemTypes.CAKE)
                .title(Text.of("Cake"))
                .description(Text.of("Prepare cake for your people."))
                .build("test", "cake");
        A.addAdvancement(2, -1, prepareCake);
        A.addAdvancement(2, 1, becomeKing);
        B = AdvancementTree.builder()
                .title(Text.of("Digger"))
                .description(Text.of("Mine, dig, destroy... everything!"))
                .icon(ItemTypes.IRON_PICKAXE)
                .background("minecraft:textures/blocks/dirt.png")
                .build("test", "digger");
        DIG_DIRT_CRITERIA = new AdvancementCriterion[5];
        for (int i = 0; i < DIG_DIRT_CRITERIA.length; i++) {
            DIG_DIRT_CRITERIA[i] = new AdvancementCriterion();
        }
        DIG_DIRT = Advancement.builder()
                .parent(becomeKing)
                .icon(ItemTypes.DIRT)
                .title(Text.of("Dig Dirt"))
                .description(Text.of("Dig 5 dirt blocks."))
                .criteria(DIG_DIRT_CRITERIA)
                .build("test", "dig_dirt");
        final Advancement dirtLover = Advancement.builder()
                .parent(DIG_DIRT)
                .icon(ItemTypes.DIRT)
                .frameType(FrameTypes.GOAL)
                .title(Text.of("Dirt Lover"))
                .description(Text.of("Dig a more then 1000 dirt blocks."))
                .build("test", "dirt_lover");
        final Advancement wanderingChallenge = Advancement.builder()
                .parent(becomeKing)
                .icon(ItemTypes.BREAD)
                .frameType(FrameTypes.CHALLENGE)
                .title(Text.of("Is it edible?"))
                .description(Text.of("Attempt to cook dirt."))
                .build("test", "cooking_dirt");
        B.addAdvancement(1, 1, DIG_DIRT);
        B.addAdvancement(2, 1, dirtLover);
        B.addAdvancement(2, 2, wanderingChallenge);
    }
}
