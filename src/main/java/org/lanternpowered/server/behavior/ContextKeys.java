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

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;
import org.spongepowered.api.world.Location;

public final class ContextKeys {

    /**
     * The cause name of the {@link ItemStack} that was used to
     * place a block of a specific type.
     */
    public static final EventContextKey<Player> PLAYER = createFor("PLAYER");

    /**
     * The cause name of the {@link ItemStack} that was used to
     * place a block of a specific type.
     */
    public static final EventContextKey<ItemStack> USED_ITEM_STACK = createFor("USED_ITEM_STACK");

    public static final EventContextKey<BlockState> USED_BLOCK_STATE = createFor("USED_BLOCK_STATE");

    /**
     * The exact point where for example a {@link Player} interacted
     * to place the block.
     */
    public static final EventContextKey<Location> INTERACTION_LOCATION = createFor("INTERACTION_LOCATION");

    public static final EventContextKey<Direction> INTERACTION_FACE = createFor("INTERACTION_FACE");

    public static final EventContextKey<HandType> INTERACTION_HAND = createFor("INTERACTION_HAND");

    public static final EventContextKey<Location> BLOCK_LOCATION = createFor("BLOCK_LOCATION");

    public static final EventContextKey<BlockType> BLOCK_TYPE = createFor("BLOCK_TYPE");

    public static final EventContextKey<BlockSnapshot> BLOCK_SNAPSHOT = createFor("BLOCK_SNAPSHOT");

    public static final EventContextKey<ItemType> ITEM_TYPE = createFor("ITEM_TYPE");

    public static final EventContextKey<Slot> USED_SLOT = createFor("USED_SLOT");

    @SuppressWarnings("unchecked")
    private static <T> EventContextKey<T> createFor(String id) {
        return DummyObjectProvider.createFor(EventContextKey.class, id);
    }

    private ContextKeys() {
    }
}
