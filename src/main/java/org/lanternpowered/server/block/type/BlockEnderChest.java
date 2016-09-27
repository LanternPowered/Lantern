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
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.PropertyProviders;
import org.lanternpowered.server.item.ItemInteractionResult;
import org.lanternpowered.server.item.ItemInteractionType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;

import java.util.function.Function;

import javax.annotation.Nullable;

public class BlockEnderChest extends LanternBlockType {

    public BlockEnderChest(String pluginId, String identifier,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder) {
        super(pluginId, identifier, itemTypeBuilder);
        this.modifyPropertyProviders(builder -> {
            builder.add(PropertyProviders.hardness(22.5));
            builder.add(PropertyProviders.blastResistance(1000.0));
        });
    }

    @Override
    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, World world, ItemInteractionType interactionType,
            ItemStack itemStack, Vector3i position, Direction blockFace, Vector3d cursorOffset) {
        if (player != null) {
            player.openInventory(player.getEnderChestInventory(), Cause.source(player).build());
        }
        return ItemInteractionResult.pass();
    }
}
