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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface BehaviorContext extends CauseStack {

    interface Snapshot {
    }

    @Override
    BehaviorContext pushCause(Object obj);

    @Override
    <T> BehaviorContext addContext(EventContextKey<T> key, T value);

    /**
     * Gets the current {@link CauseStack}.
     *
     * @return The cause stack
     */
    CauseStack getCauseStack();

    /**
     * Pushes a {@link Snapshot}.
     *
     * @return The snapshot
     */
    Snapshot pushSnapshot();

    /**
     * Reverts all the changes made since the {@link Snapshot}.
     */
    void popSnapshot(Snapshot snapshot);

    /**
     * Gets all the {@link BlockSnapshot}s.
     *
     * @return The block snapshots
     */
    Collection<BlockSnapshot> getBlockSnapshots();

    void addEntity(EntitySnapshot entitySnapshot);

    void addEntity(Entity entity);

    /**
     * Adds a {@link SlotTransaction} to the context.
     *
     * @param slotTransaction The slot transaction
     */
    void addSlotChange(SlotTransaction slotTransaction);

    /**
     * Adds a {@link SlotTransaction}s to the context.
     *
     * @param slotTransactions The slot transactions
     */
    void addSlotChanges(Iterable<SlotTransaction> slotTransactions);

    /**
     * Gets a {@link ImmutableList} with all the {@link SlotTransaction}s.
     *
     * @return The slot transactions
     */
    List<SlotTransaction> getSlotChanges();

    /**
     * Adds a {@link BlockSnapshot} to the context.
     *
     * @param blockSnapshot The block snapshot
     * @param force Force the block change, this overrides the current entry at the position without an exception
     * @throws IllegalArgumentException If the location of the block change is in use and force is {@code false}
     */
    void addBlockChange(BlockSnapshot blockSnapshot, boolean force) throws IllegalArgumentException;

    /**
     * Adds a {@link BlockSnapshot} to the context.
     *
     * @param blockSnapshot The block snapshot
     * @throws IllegalArgumentException If the location of the block change is in use and force is {@code false}
     */
    default void addBlockChange(BlockSnapshot blockSnapshot) throws IllegalArgumentException {
        addBlockChange(blockSnapshot, false);
    }

    void transformBlockChanges(BiConsumer<BlockSnapshot, BlockSnapshotBuilder> snapshotTransformer);

    void transformBlockChangesWithFunction(Function<BlockSnapshot, BlockSnapshot> snapshotTransformer);

    /**
     * Populates the {@link BlockSnapshot.Builder} with information
     * that triggered the change. For example the notifier.
     *
     * @param builder The builder to populate
     */
    void populateBlockSnapshot(BlockSnapshot.Builder builder, int populationFlags);

    <B extends Behavior> BehaviorResult process(BehaviorPipeline<B> pipeline, BehaviorProcessFunction<B> function);

    final class PopulationFlags {

        public static final int CREATOR = 0x01;

        public static final int NOTIFIER = 0x02;

        public static final int CREATOR_AND_NOTIFIER = CREATOR | NOTIFIER;
    }
}
