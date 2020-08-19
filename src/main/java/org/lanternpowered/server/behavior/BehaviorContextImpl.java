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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "ConstantConditions", "SuspiciousMethodCalls"})
public class BehaviorContextImpl implements BehaviorContext {

    @Override
    public Cause getCurrentCause() {
        return this.causeStack.getCurrentCause();
    }

    @Override
    public EventContext getCurrentContext() {
        return this.causeStack.getCurrentContext();
    }

    @Override
    public Object popCause() {
        return this.causeStack.popCause();
    }

    @Override
    public void popCauses(int n) {
        this.causeStack.popCauses(n);
    }

    @Override
    public Object peekCause() {
        return this.causeStack.peekCause();
    }

    @Override
    public void popCauseFrame(StackFrame handle) {
        this.causeStack.popCauseFrame(handle);
    }

    @Override
    public <T> Optional<T> getContext(EventContextKey<T> key) {
        return this.causeStack.getContext(key);
    }

    @Override
    public <T> Optional<T> removeContext(EventContextKey<T> key) {
        return this.causeStack.removeContext(key);
    }

    @Override
    public <T> Optional<T> first(Class<T> target) {
        return this.causeStack.first(target);
    }

    @Override
    public <T> Optional<T> last(Class<T> target) {
        return this.causeStack.last(target);
    }

    @Override
    public boolean containsType(Class<?> target) {
        return this.causeStack.containsType(target);
    }

    @Override
    public boolean contains(Object object) {
        return this.causeStack.contains(object);
    }

    @Override
    public Frame pushCauseFrame() {
        return this.causeStack.pushCauseFrame();
    }

    @Override
    public BehaviorContextImpl pushCause(Object obj) {
        this.causeStack.pushCause(obj);
        return this;
    }

    @Override
    public <T> BehaviorContextImpl addContext(EventContextKey<T> key, T value) {
        this.causeStack.addContext(key, value);
        return this;
    }

    private final class Snapshot implements BehaviorContext.Snapshot {

        private final Int2ObjectMap<Object> parameterValues;
        private final Map<Location, BlockSnapshot> blockSnapshots;
        private final Set<BlockSnapshot> positionlessBlockSnapshots;
        private final Set<SlotTransaction> slotTransactions;
        private final Set<EntitySnapshot> entitySnapshots;
        private final CauseStack.Frame causeStackFrame;

        Snapshot(Int2ObjectMap<Object> parameterValues, Map<Location, BlockSnapshot> blockSnapshots,
                Set<BlockSnapshot> positionlessBlockSnapshots, Set<SlotTransaction> slotTransactions,
                Set<EntitySnapshot> entitySnapshots, CauseStack.Frame causeStackFrame) {
            this.parameterValues = parameterValues;
            this.blockSnapshots = blockSnapshots;
            this.positionlessBlockSnapshots = positionlessBlockSnapshots;
            this.slotTransactions = slotTransactions;
            this.entitySnapshots = entitySnapshots;
            this.causeStackFrame = causeStackFrame;
        }
    }

    private final CauseStack causeStack;
    private final Deque<Snapshot> snapshots = new ArrayDeque<>();

    private Int2ObjectMap<Object> parameterValues = new Int2ObjectOpenHashMap<>();
    private Map<Location, BlockSnapshot> blockSnapshots = new HashMap<>();
    private Set<BlockSnapshot> positionlessBlockSnapshots = new HashSet<>();
    private Set<SlotTransaction> slotTransactions = new HashSet<>();
    private Set<EntitySnapshot> entitySnapshots = new HashSet<>();

    public BehaviorContextImpl(CauseStack causeStack) {
        this.causeStack = causeStack;
    }

    @Override
    public Snapshot pushSnapshot() {
        final Snapshot snapshot = new Snapshot(new Int2ObjectOpenHashMap<>(this.parameterValues),
                new HashMap<>(this.blockSnapshots),
                new HashSet<>(this.positionlessBlockSnapshots),
                new HashSet<>(this.slotTransactions),
                new HashSet<>(this.entitySnapshots),
                this.causeStack.pushCauseFrame());
        this.snapshots.push(snapshot);
        return snapshot;
    }

    @Override
    public void popSnapshot(BehaviorContext.Snapshot snapshot) {
        checkNotNull(snapshot, "snapshot");
        checkState(this.snapshots.contains(snapshot), "snapshot isn't present in this context");
        Snapshot snapshot1;
        while ((snapshot1 = this.snapshots.poll()) != snapshot) {
            this.causeStack.popCauseFrame(snapshot1.causeStackFrame);
        }
        snapshot1 = (Snapshot) snapshot;

        this.parameterValues = snapshot1.parameterValues;
        this.blockSnapshots = snapshot1.blockSnapshots;
        this.positionlessBlockSnapshots = snapshot1.positionlessBlockSnapshots;
        this.slotTransactions = snapshot1.slotTransactions;
        this.entitySnapshots = snapshot1.entitySnapshots;
        this.causeStack.popCauseFrame(snapshot1.causeStackFrame);
    }

    @Override
    public CauseStack getCauseStack() {
        return this.causeStack;
    }

    @Override
    public Collection<BlockSnapshot> getBlockSnapshots() {
        return Collections.unmodifiableCollection(this.blockSnapshots.values());
    }

    @Override
    public void addEntity(EntitySnapshot entitySnapshot) {

    }

    @Override
    public void addEntity(Entity entity) {

    }

    @Override
    public void addSlotChange(SlotTransaction slotTransaction) {
        this.slotTransactions.add(checkNotNull(slotTransaction, "slotTransaction"));
    }

    @Override
    public void addSlotChanges(Iterable<SlotTransaction> slotTransactions) {
        checkNotNull(slotTransactions, "slotTransactions");
        slotTransactions.forEach(this::addSlotChange);
    }

    @Override
    public List<SlotTransaction> getSlotChanges() {
        return ImmutableList.copyOf(this.slotTransactions);
    }

    @Override
    public void addBlockChange(BlockSnapshot blockSnapshot, boolean force) throws IllegalArgumentException {
        checkNotNull(blockSnapshot, "blockSnapshot");
        if (((LanternBlockSnapshot) blockSnapshot).isPositionless()) {
            this.positionlessBlockSnapshots.add(blockSnapshot);
        } else {
            final Location loc = blockSnapshot.getLocation().orElseThrow(
                    () -> new IllegalArgumentException("Unable to retrieve the location of the block snapshot, is the world loaded?"));
            if (!force) {
                checkArgument(this.blockSnapshots.putIfAbsent(loc, blockSnapshot) == null,
                        "There is already a block snapshot present for the location: %s", loc);
            } else {
                this.blockSnapshots.put(loc, blockSnapshot);
            }
        }
    }

    @Override
    public void transformBlockChanges(BiConsumer<BlockSnapshot, BlockSnapshotBuilder> snapshotTransformer) {
        checkNotNull(snapshotTransformer, "snapshotTransformer");

        final Set<BlockSnapshot> newPositionlessBlockSnapshots = new HashSet<>();
        final Map<Location, BlockSnapshot> newBlockSnapshots = new HashMap<>();

        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.createPositionless();

        for (BlockSnapshot blockSnapshot : Iterables.concat(this.positionlessBlockSnapshots, this.blockSnapshots.values())) {
            builder.from(blockSnapshot);
            snapshotTransformer.accept(blockSnapshot, builder);
            final BlockSnapshot result = builder.build();
            if (((LanternBlockSnapshot) result).isPositionless()) {
                newPositionlessBlockSnapshots.add(result);
            } else {
                final Location loc = blockSnapshot.getLocation().orElseThrow(
                        () -> new IllegalArgumentException("Unable to retrieve the location of the block snapshot, is the world loaded?"));
                checkArgument(newBlockSnapshots.putIfAbsent(loc, result) == null,
                        "There is already a block snapshot present for the location: %s", loc);
            }
        }

        this.positionlessBlockSnapshots = newPositionlessBlockSnapshots;
        this.blockSnapshots = newBlockSnapshots;
    }

    @Override
    public void transformBlockChangesWithFunction(Function<BlockSnapshot, BlockSnapshot> snapshotTransformer) {
        checkNotNull(snapshotTransformer, "snapshotTransformer");

        final Set<BlockSnapshot> newPositionlessBlockSnapshots = new HashSet<>();
        final Map<Location, BlockSnapshot> newBlockSnapshots = new HashMap<>();

        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.createPositionless();

        for (BlockSnapshot blockSnapshot : Iterables.concat(this.positionlessBlockSnapshots, this.blockSnapshots.values())) {
            final BlockSnapshot newSnapshot = snapshotTransformer.apply(blockSnapshot);
            if (newSnapshot == null) {
                continue;
            }
            final BlockSnapshot result = builder.build();
            if (((LanternBlockSnapshot) result).isPositionless()) {
                newPositionlessBlockSnapshots.add(result);
            } else {
                final Location loc = blockSnapshot.getLocation().orElseThrow(
                        () -> new IllegalArgumentException("Unable to retrieve the location of the block snapshot, is the world loaded?"));
                checkArgument(newBlockSnapshots.putIfAbsent(loc, blockSnapshot) == null,
                        "There is already a block snapshot present for the location: %s", loc);
            }
        }

        this.positionlessBlockSnapshots = newPositionlessBlockSnapshots;
        this.blockSnapshots = newBlockSnapshots;
    }

    @Override
    public void populateBlockSnapshot(BlockSnapshot.Builder builder, int populationFlags) {
        boolean creator = (populationFlags & PopulationFlags.CREATOR) != 0;
        boolean notifier = (populationFlags & PopulationFlags.NOTIFIER) != 0;
        if (creator || notifier) {
            if (notifier) {
                final Optional<User> optNotifier = this.causeStack.getContext(EventContextKeys.NOTIFIER);
                if (optNotifier.isPresent()) {
                    final User notifierObj = optNotifier.get();
                    builder.notifier(notifierObj.getUniqueId());
                    notifier = false; // Make sure that the notifier isn't overridden
                }
            }
            final Optional<Entity> optEntity = this.causeStack.first(Entity.class);
            if (optEntity.isPresent()) {
                final UUID uuid = optEntity.get().getUniqueId();
                if (creator) {
                    builder.creator(uuid);
                }
                if (notifier) {
                    builder.notifier(uuid);
                }
            }
        }
    }

    @Override
    public <B extends Behavior> BehaviorResult process(BehaviorPipeline<B> pipeline, BehaviorProcessFunction<B> function) {
        Snapshot snapshot = null;
        BehaviorResult result = null;
        for (B behavior : pipeline.getBehaviors()) {
            if (snapshot == null) {
                snapshot = pushSnapshot();
            }
            result = function.process(this, behavior);
            if (result == BehaviorResult.SUCCESS) {
                return result;
            } else if (result == BehaviorResult.PASS) {
                popSnapshot(snapshot);
            } else if (result == BehaviorResult.FAIL) {
                return result;
            } else if (result == BehaviorResult.CONTINUE) {
                snapshot = pushSnapshot();
            }
        }
        return result == null ? BehaviorResult.FAIL : result;
    }

    public void revert() {
        Snapshot snapshot1;
        while ((snapshot1 = this.snapshots.poll()) != null) {
            this.causeStack.popCauseFrame(snapshot1.causeStackFrame);
        }
    }

    public void accept() {
        for (Map.Entry<Location, BlockSnapshot> entry : this.blockSnapshots.entrySet()) {
            entry.getValue().restore(true, BlockChangeFlags.ALL);
        }
        for (SlotTransaction slotTransaction : this.slotTransactions) {
            slotTransaction.getSlot().set(LanternItemStack.toNullable(slotTransaction.getFinal()));
        }
        revert();
    }
}
