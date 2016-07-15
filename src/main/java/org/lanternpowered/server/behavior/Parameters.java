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
package org.lanternpowered.server.behavior;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class Parameters {

    /**
     * The cause name of the {@link ItemStack} that was used to
     * place a block of a specific type.
     */
    public static final Parameter<ItemStack> USED_ITEM_STACK = Parameter.of(ItemStack.class, "UsedItemStack");

    public static final Parameter<BlockState> USED_BLOCK_STATE = Parameter.of(BlockState.class, "UsedBlockState");

    /**
     * The exact point where for example a {@link Player} interacted
     * to place the block.
     */
    public static final Parameter<Location<World>> INTERACTION_LOCATION = Parameter.of(new TypeToken<Location<World>>() {}, "InteractionLoc");

    public static final Parameter<Direction> INTERACTION_FACE = Parameter.of(Direction.class, "InteractionFace");

    public static final Parameter<HandType> INTERACTION_HAND = Parameter.of(HandType.class, "InteractionHand");

    public static final Parameter<Location<World>> BLOCK_LOCATION = Parameter.of(new TypeToken<Location<World>>() {}, "BlockLoc");

    public static final Parameter<BlockType> BLOCK_TYPE = Parameter.of(BlockType.class, "BlockType");

    public static final Parameter<ItemType> ITEM_TYPE = Parameter.of(ItemType.class, "ItemType");

    // TODO: Move this???
    public static final Parameter<ItemStack> RESULT_ITEM_STACK = Parameter.of(ItemStack.class, "ResultItemStack");

    public static final Parameter<Player> PLAYER = Parameter.of(Player.class, "Player");

    private Parameters() {
    }
}
