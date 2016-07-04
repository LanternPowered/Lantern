/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.block.type;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.data.type.LanternLogAxis;
import org.lanternpowered.server.data.type.LanternTreeType;
import org.lanternpowered.server.item.ItemInteractionType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.function.Function;

public class BlockLog extends VariantBlock<LanternTreeType> {

    @SuppressWarnings("unchecked")
    public static final EnumTrait<LanternLogAxis> AXIS = LanternEnumTrait.of("axis", (Key) Keys.LOG_AXIS, LanternLogAxis.class);

    public BlockLog(String pluginId, String identifier, @Nullable Function<BlockType, ItemType> itemTypeBuilder,
            EnumTrait<LanternTreeType> treeTrait) {
        super(pluginId, identifier, itemTypeBuilder, treeTrait, AXIS);
        this.modifyPropertyProviders(builder -> {
            builder.add(PropertyProviders.hardness(2.0));
            builder.add(PropertyProviders.blastResistance(5.0));
            builder.add(PropertyProviders.flammableInfo(5, 5));
        });
    }

    @Override
    protected String getTranslationKey(LanternTreeType element) {
        return "tile.log." + element.getTranslationKeyBase() + ".name";
    }

    @Override
    public BlockState placeBlockAt(@Nullable Player player, World world, ItemInteractionType interactionType,
            ItemStack itemStack, Vector3i clickedBlock, Direction blockFace, Vector3d cursorOffset) {
        final BlockState state = super.placeBlockAt(player, world, interactionType, itemStack,
                clickedBlock, blockFace, cursorOffset);
        return state.withTrait(AXIS, LanternLogAxis.fromDirection(blockFace)).get();
    }
}
