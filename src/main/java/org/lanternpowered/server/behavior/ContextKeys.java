/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.behavior;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventContextKey;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.ServerLocation;

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
    public static final EventContextKey<ServerLocation> INTERACTION_LOCATION = createFor("INTERACTION_LOCATION");

    public static final EventContextKey<Direction> INTERACTION_FACE = createFor("INTERACTION_FACE");

    public static final EventContextKey<HandType> INTERACTION_HAND = createFor("INTERACTION_HAND");

    public static final EventContextKey<ServerLocation> BLOCK_LOCATION = createFor("BLOCK_LOCATION");

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
