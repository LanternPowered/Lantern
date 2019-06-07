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
package org.lanternpowered.server.block.entity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectSerializerRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.BlockEntityArchetype;
import org.spongepowered.api.block.entity.BlockEntityType;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public class LanternBlockEntityArchetypeBuilder implements BlockEntityArchetype.Builder {

    @Nullable private LanternBlockEntityType blockEntityType;
    @Nullable private LanternBlockEntity blockEntity;
    @Nullable private List data;
    @Nullable private BlockState blockState;

    @Override
    public Optional<BlockEntityArchetype> build(DataView container) throws InvalidDataException {
        final ObjectSerializer<LanternBlockEntity> serializer = ObjectSerializerRegistry.get().get(LanternBlockEntity.class).get();
        return Optional.of(new LanternBlockEntityArchetype(serializer.deserialize(container)));
    }

    @Override
    public BlockEntityArchetype.Builder reset() {
        this.blockEntityType = null;
        this.blockEntity = null;
        this.blockState = null;
        if (this.data != null) {
            this.data.clear();
        }
        return this;
    }

    @Override
    public BlockEntityArchetype.Builder from(BlockEntityArchetype value) {
        checkNotNull(value, "value");
        return blockEntity(((LanternBlockEntityArchetype) value).blockEntity);
    }

    @Override
    public BlockEntityArchetype.Builder from(Location location) {
        return blockEntity(location.getBlockEntity().orElseThrow(
                () -> new IllegalArgumentException("There is no block entity available at the provided location: " + location)));
    }

    @Override
    public BlockEntityArchetype.Builder state(BlockState state) {
        checkNotNull(state, "state");
        this.blockState = state;
        return this;
    }

    @Override
    public BlockEntityArchetype.Builder blockEntity(BlockEntityType blockEntityType) {
        checkNotNull(blockEntityType, "blockEntityType");
        this.blockEntityType = (LanternBlockEntityType) blockEntityType;
        return this;
    }

    @Override
    public BlockEntityArchetype.Builder blockEntity(BlockEntity blockEntity) {
        checkNotNull(blockEntity, "blockEntity");
        return from(LanternBlockEntityArchetype.copy((LanternBlockEntity) blockEntity));
    }

    private BlockEntityArchetype.Builder from(LanternBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.blockEntityType = blockEntity.getType();
        this.blockState = blockEntity.getBlock();
        if (this.data != null) {
            this.data.clear();
        }
        return this;
    }

    @Override
    public BlockEntityArchetype.Builder blockEntityData(DataView dataView) {
        final ObjectSerializer<LanternBlockEntity> serializer = ObjectSerializerRegistry.get().get(LanternBlockEntity.class).get();
        final LanternBlockEntity blockEntity = serializer.deserialize(dataView);
        from(blockEntity);
        return this;
    }

    @Override
    public BlockEntityArchetype.Builder set(DataManipulator manipulator) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(manipulator);
        return this;
    }

    @Override
    public <E, V extends Value<E>> BlockEntityArchetype.Builder set(V value) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(value);
        return this;
    }

    @Override
    public <E, V extends Value<E>> BlockEntityArchetype.Builder set(Key<V> key, E value) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(new Tuple<>(key, value));
        return this;
    }

    @Override
    public BlockEntityArchetype build() {
        checkState(this.blockEntityType != null, "The block entity type must be set");
        checkState(this.blockState != null, "The block state must be set");
        final LanternBlockEntity blockEntity;
        if (this.blockEntity == null || this.blockEntity.getType() != this.blockEntityType) {
            blockEntity = this.blockEntityType.construct();
            if (this.blockEntity != null) {
                blockEntity.copyFrom(this.blockEntity);
            }
        } else {
            blockEntity = LanternBlockEntityArchetype.copy(this.blockEntity);
        }
        blockEntity.setBlock(this.blockState);
        if (this.data != null) {
            for (Object obj : this.data) {
                if (obj instanceof DataManipulator) {
                    blockEntity.offerFastNoEvents((DataManipulator) obj);
                } else if (obj instanceof Value.Mutable) {
                    blockEntity.offerFastNoEvents((Value.Mutable) obj);
                } else {
                    final Tuple<Key, Object> tuple = (Tuple<Key, Object>) obj;
                    blockEntity.offerFastNoEvents(tuple.getFirst(), tuple.getSecond());
                }
            }
        }
        return new LanternBlockEntityArchetype(blockEntity);
    }
}
