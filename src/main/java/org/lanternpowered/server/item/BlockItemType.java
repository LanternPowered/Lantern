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
package org.lanternpowered.server.item;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

public class BlockItemType extends LanternItemType {

    private final BlockType blockType;

    public BlockItemType(String pluginId, String identifier, BlockType blockType) {
        super(pluginId, identifier, checkNotNull(blockType, "blockType").getTranslation());
        this.blockType = blockType;
    }

    @Override
    public Optional<BlockType> getBlock() {
        return Optional.of(this.blockType);
    }

    @Override
    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, World world, ItemInteractionType interactionType,
            ItemStack itemStack, Vector3i clickedBlock, Direction blockFace, Vector3d cursorOffset) {
        itemStack = itemStack.copy();
        itemStack.setQuantity(itemStack.getQuantity() - 1);
        world.setBlock(clickedBlock.add(blockFace.toVector3d().toInt()), this.blockType.getDefaultState());
        return ItemInteractionResult.builder()
                .type(ItemInteractionResult.Type.SUCCESS)
                .resultItem(itemStack.createSnapshot())
                .build();
    }

}
