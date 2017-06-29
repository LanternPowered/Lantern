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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class BehaviorContextImpl implements BehaviorContext {

    public static final class Snapshot implements BehaviorContext.Snapshot {

        private final Int2ObjectMap<Object> parameterValues;
        private final Map<Location<World>, BlockSnapshot> blockSnapshots;
        private final Set<BlockSnapshot> positionlessBlockSnapshots;
        private final Set<SlotTransaction> slotTransactions;
        private final Set<EntitySnapshot> entitySnapshots;
        private final Cause cause;

        Snapshot(Int2ObjectMap<Object> parameterValues, Map<Location<World>, BlockSnapshot> blockSnapshots,
                Set<BlockSnapshot> positionlessBlockSnapshots, Set<SlotTransaction> slotTransactions,
                Set<EntitySnapshot> entitySnapshots, Cause cause) {
            this.parameterValues = parameterValues;
            this.blockSnapshots = blockSnapshots;
            this.positionlessBlockSnapshots = positionlessBlockSnapshots;
            this.slotTransactions = slotTransactions;
            this.entitySnapshots = entitySnapshots;
            this.cause = cause;
        }
    }

    private Cause cause;
    private Int2ObjectMap<Object> parameterValues = new Int2ObjectOpenHashMap<>();
    private Map<Location<World>, BlockSnapshot> blockSnapshots = new HashMap<>();
    private Set<BlockSnapshot> positionlessBlockSnapshots = new HashSet<>();
    private Set<SlotTransaction> slotTransactions = new HashSet<>();
    private Set<EntitySnapshot> entitySnapshots = new HashSet<>();

    public BehaviorContextImpl(Cause cause) {
        this.cause = cause;
    }

    @Override
    public Snapshot createSnapshot() {
        return new Snapshot(new Int2ObjectOpenHashMap<>(this.parameterValues),
                ImmutableMap.copyOf(this.blockSnapshots),
                ImmutableSet.copyOf(this.positionlessBlockSnapshots),
                ImmutableSet.copyOf(this.slotTransactions),
                ImmutableSet.copyOf(this.entitySnapshots), this.cause);
    }

    @Override
    public void restoreSnapshot(BehaviorContext.Snapshot snapshot) {
        final Snapshot snapshot1 = (Snapshot) snapshot;
        this.parameterValues = new Int2ObjectOpenHashMap<>(snapshot1.parameterValues);
        this.blockSnapshots = new HashMap<>(snapshot1.blockSnapshots);
        this.positionlessBlockSnapshots = new HashSet<>(snapshot1.positionlessBlockSnapshots);
        this.slotTransactions = new HashSet<>(snapshot1.slotTransactions);
        this.entitySnapshots = new HashSet<>(snapshot1.entitySnapshots);
        this.cause = snapshot1.cause;
    }

    @Override
    public <V> Optional<V> get(Parameter<V> parameter) {
        checkNotNull(parameter, "parameter");
        return Optional.ofNullable((V) this.parameterValues.get(parameter.getIndex()));
    }

    @Override
    public <V> void set(Parameter<V> parameter, @Nullable V value) {
        checkNotNull(parameter, "parameter");
        if (value == null) {
            this.parameterValues.remove(parameter.getIndex());
        } else {
            this.parameterValues.put(parameter.getIndex(), value);
        }
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public void insertCause(String name, Object cause) {
        this.cause = Cause.builder().from(this.cause).named(name, cause).build();
    }

    @Override
    public void insertCause(NamedCause cause) {
        this.cause = Cause.builder().from(this.cause).named(cause).build();
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
            final Location<World> loc = blockSnapshot.getLocation().orElseThrow(
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
        final Map<Location<World>, BlockSnapshot> newBlockSnapshots = new HashMap<>();

        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.createPositionless();

        for (BlockSnapshot blockSnapshot : Iterables.concat(this.positionlessBlockSnapshots, this.blockSnapshots.values())) {
            builder.from(blockSnapshot);
            snapshotTransformer.accept(blockSnapshot, builder);
            final BlockSnapshot result = builder.build();
            if (((LanternBlockSnapshot) result).isPositionless()) {
                newPositionlessBlockSnapshots.add(result);
            } else {
                final Location<World> loc = blockSnapshot.getLocation().orElseThrow(
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
        final Map<Location<World>, BlockSnapshot> newBlockSnapshots = new HashMap<>();

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
                final Location<World> loc = blockSnapshot.getLocation().orElseThrow(
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
                final Optional<Object> optNotifierObj = this.cause.get(NamedCause.NOTIFIER, Object.class);
                if (optNotifierObj.isPresent()) {
                    final Object notifierObj = optNotifierObj.get();
                    if (notifierObj instanceof UUID) {
                        builder.notifier((UUID) notifierObj);
                        notifier = false; // Make sure that the notifier isn't overridden
                    } else if (notifierObj instanceof Identifiable) {
                        builder.notifier(((Identifiable) notifierObj).getUniqueId());
                        notifier = false; // Make sure that the notifier isn't overridden
                    }
                }
            }
            final Optional<Entity> optEntity = this.cause.first(Entity.class);
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
    public <B extends Behavior> boolean process(BehaviorPipeline<B> pipeline, BehaviorProcessFunction<B> function) {
        Snapshot snapshot = null;
        BehaviorResult result = null;
        final Iterator<B> it = pipeline.getBehaviors().iterator();
        while (it.hasNext()) {
            final B behavior = it.next();
            if (snapshot == null && it.hasNext()) {
                snapshot = createSnapshot();
            }
            //noinspection unchecked
            result = function.process(this, behavior);
            if (result == BehaviorResult.SUCCESS) {
                return true;
            } else if (result == BehaviorResult.PASS) {
                if (it.hasNext()) {
                    restoreSnapshot(snapshot);
                }
            } else if (result == BehaviorResult.FAIL) {
                return false;
            } else if (result == BehaviorResult.CONTINUE) {
                if (it.hasNext()) {
                    snapshot = createSnapshot();
                }
            }
        }
        return !(result == null || result == BehaviorResult.PASS);
    }

    public void accept() {
        for (Map.Entry<Location<World>, BlockSnapshot> entry : this.blockSnapshots.entrySet()) {
            entry.getValue().restore(true, BlockChangeFlag.ALL);
        }
    }
}
