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
package org.lanternpowered.server.block.tile;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectSerializerRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternTileEntityArchetypeBuilder implements TileEntityArchetype.Builder {

    @Nullable private LanternTileEntityType tileEntityType;
    @Nullable private LanternTileEntity tileEntity;
    @Nullable private List data;
    @Nullable private BlockState blockState;

    @Override
    public Optional<TileEntityArchetype> build(DataView container) throws InvalidDataException {
        final ObjectSerializer<LanternTileEntity> serializer = ObjectSerializerRegistry.get().get(LanternTileEntity.class).get();
        return Optional.of(new LanternTileEntityArchetype(serializer.deserialize(container)));
    }

    @Override
    public TileEntityArchetype.Builder reset() {
        this.tileEntityType = null;
        this.tileEntity = null;
        this.blockState = null;
        if (this.data != null) {
            this.data.clear();
        }
        return this;
    }

    @Override
    public TileEntityArchetype.Builder from(TileEntityArchetype value) {
        checkNotNull(value, "value");
        return tile(((LanternTileEntityArchetype) value).tileEntity);
    }

    @Override
    public TileEntityArchetype.Builder from(Location<World> location) {
        return tile(location.getTileEntity().orElseThrow(
                () -> new IllegalArgumentException("There is no tile entity available at the provided location: " + location)));
    }

    @Override
    public TileEntityArchetype.Builder state(BlockState state) {
        checkNotNull(state, "state");
        this.blockState = state;
        return this;
    }

    @Override
    public TileEntityArchetype.Builder tile(TileEntityType tileEntityType) {
        checkNotNull(tileEntityType, "tileEntityType");
        this.tileEntityType = (LanternTileEntityType) tileEntityType;
        return this;
    }

    @Override
    public TileEntityArchetype.Builder tile(TileEntity tileEntity) {
        checkNotNull(tileEntity, "tileEntity");
        return from(LanternTileEntityArchetype.copy((LanternTileEntity) tileEntity));
    }

    private TileEntityArchetype.Builder from(LanternTileEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.tileEntityType = tileEntity.getType();
        this.blockState = tileEntity.getBlock();
        if (this.data != null) {
            this.data.clear();
        }
        return this;
    }

    @Override
    public TileEntityArchetype.Builder tileData(DataView dataView) {
        final ObjectSerializer<LanternTileEntity> serializer = ObjectSerializerRegistry.get().get(LanternTileEntity.class).get();
        final LanternTileEntity tileEntity = serializer.deserialize(dataView);
        from(tileEntity);
        return this;
    }

    @Override
    public TileEntityArchetype.Builder setData(DataManipulator<?, ?> manipulator) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(manipulator);
        return this;
    }

    @Override
    public <E, V extends BaseValue<E>> TileEntityArchetype.Builder set(V value) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(value);
        return this;
    }

    @Override
    public <E, V extends BaseValue<E>> TileEntityArchetype.Builder set(Key<V> key, E value) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(new Tuple<>(key, value));
        return this;
    }

    @Override
    public TileEntityArchetype build() {
        checkState(this.tileEntityType != null, "The tile entity type must be set");
        checkState(this.blockState != null, "The block state must be set");
        final LanternTileEntity tileEntity;
        if (this.tileEntity == null || this.tileEntity.getType() != this.tileEntityType) {
            tileEntity = this.tileEntityType.construct();
            if (this.tileEntity != null) {
                tileEntity.copyFrom(this.tileEntity);
            }
        } else {
            tileEntity = LanternTileEntityArchetype.copy(this.tileEntity);
        }
        tileEntity.setBlock(this.blockState);
        if (this.data != null) {
            for (Object obj : this.data) {
                if (obj instanceof DataManipulator) {
                    tileEntity.offerFastNoEvents((DataManipulator) obj);
                } else if (obj instanceof Value) {
                    tileEntity.offerFastNoEvents((Value) obj);
                } else {
                    final Tuple<Key, Object> tuple = (Tuple<Key, Object>) obj;
                    tileEntity.offerFastNoEvents(tuple.getFirst(), tuple.getSecond());
                }
            }
        }
        return new LanternTileEntityArchetype(tileEntity);
    }
}
