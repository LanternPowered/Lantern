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
package org.lanternpowered.server.world.extent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ShortArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AtomicShortArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeAreaWorker;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.extent.worker.MutableBiomeAreaWorker;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface AbstractExtent extends Extent {

    @Override
    default MutableBiomeAreaWorker<? extends Extent> getBiomeWorker() {
        return new LanternMutableBiomeAreaWorker<>(this);
    }

    @Override
    default MutableBlockVolumeWorker<? extends Extent> getBlockWorker(Cause cause) {
        return new LanternMutableBlockVolumeWorker<>(this, cause);
    }

    @Override
    default <T extends Property<?, ?>> Optional<T> getProperty(Vector3i position, Direction direction, Class<T> propertyClass) {
        return this.getProperty(position.getX(), position.getY(), position.getZ(), direction, propertyClass);
    }

    @Override
    default boolean restoreSnapshot(BlockSnapshot snapshot, boolean force, BlockChangeFlag flag, Cause cause) {
        final Location<World> location = checkNotNull(snapshot, "snapshot").getLocation().orElse(null);
        checkArgument(location != null, "location is not present in snapshot");
        return this.restoreSnapshot(location.getBlockPosition(), snapshot, force, flag, cause);
    }

    @Override
    default MutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        if (!this.containsBiome(newMin.getX(), newMin.getY())) {
            throw new PositionOutOfBoundsException(newMin, this.getBiomeMin(), this.getBiomeMax());
        }
        if (!this.containsBiome(newMax.getX(), newMax.getY())) {
            throw new PositionOutOfBoundsException(newMax, this.getBiomeMin(), this.getBiomeMax());
        }
        return new MutableBiomeViewDownsize(this, newMin, newMax);
    }

    @Override
    default MutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new MutableBiomeViewTransform(this, transform);
    }

    @Override
    default UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return new UnmodifiableBiomeAreaWrapper(this);
    }

    @Override
    default MutableBiomeArea getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ObjectArrayMutableBiomeBuffer(ExtentBufferHelper.copyToObjectArray(this, this.getBiomeMin(),
                        this.getBiomeMax(), this.getBiomeSize()), this.getBiomeMin(), this.getBiomeSize());
            case THREAD_SAFE:
                return new AtomicObjectArrayMutableBiomeBuffer(ExtentBufferHelper.copyToObjectArray(this, this.getBiomeMin(),
                        this.getBiomeMax(), this.getBiomeSize()), this.getBiomeMin(), this.getBiomeSize());
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    default ImmutableBiomeArea getImmutableBiomeCopy() {
        return ShortArrayImmutableBiomeBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToArray(this, this.getBiomeMin(),
                this.getBiomeMax(), this.getBiomeSize()), this.getBiomeMin(), this.getBiomeSize());
    }

    @Override
    default MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        if (!this.containsBlock(newMin.getX(), newMin.getY(), newMin.getZ())) {
            throw new PositionOutOfBoundsException(newMin, this.getBlockMin(), this.getBlockMax());
        }
        if (!this.containsBlock(newMax.getX(), newMax.getY(), newMax.getZ())) {
            throw new PositionOutOfBoundsException(newMax, this.getBlockMin(), this.getBlockMax());
        }
        return new MutableBlockViewDownsize(this, newMin, newMax);
    }

    @Override
    default MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new MutableBlockViewTransform(this, transform);
    }

    @Override
    default UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this);
    }

    @Override
    default MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBlockBuffer(ExtentBufferHelper.copyToArray(this, this.getBlockMin(),
                        this.getBlockMax(), this.getBlockSize()), this.getBlockMin(), this.getBlockSize());
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBlockBuffer(ExtentBufferHelper.copyToArray(this, this.getBlockMin(),
                        this.getBlockMax(), this.getBlockSize()), this.getBlockMin(), this.getBlockSize());
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    default ImmutableBlockVolume getImmutableBlockCopy() {
        return ShortArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToArray(this, this.getBlockMin(),
                this.getBlockMax(), this.getBlockSize()), this.getBlockMin(), this.getBlockSize());
    }

    @Override
    default void setRawData(Vector3i position, DataView container) throws InvalidDataException {
        this.setRawData(position.getX(), position.getY(), position.getZ(), container);
    }

    @Override
    default boolean validateRawData(Vector3i position, DataView container) {
        return this.validateRawData(position.getX(), position.getY(), position.getZ(), container);
    }

    @Override
    default Collection<DataManipulator<?, ?>> getManipulators(Vector3i coordinates) {
        return this.getManipulators(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    @Override
    default <E> Optional<E> get(Vector3i coordinates, Key<? extends BaseValue<E>> key) {
        return this.get(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> get(Vector3i coordinates, Class<T> manipulatorClass) {
        return this.get(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Vector3i coordinates, Class<T> manipulatorClass) {
        return this.getOrCreate(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    default <E> E getOrNull(Vector3i coordinates, Key<? extends BaseValue<E>> key) {
        return this.getOrNull(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    default <E> E getOrElse(Vector3i coordinates, Key<? extends BaseValue<E>> key, E defaultValue) {
        return this.getOrElse(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key, defaultValue);
    }

    @Override
    default <E, V extends BaseValue<E>> Optional<V> getValue(Vector3i coordinates, Key<V> key) {
        return this.getValue(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    default boolean supports(Vector3i coordinates, Key<?> key) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    default boolean supports(Vector3i coordinates, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    default boolean supports(Vector3i coordinates, DataManipulator<?, ?> manipulator) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulator);
    }

    @Override
    default Set<Key<?>> getKeys(Vector3i coordinates) {
        return this.getKeys(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    @Override
    default Set<ImmutableValue<?>> getValues(Vector3i coordinates) {
        return this.getValues(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    @Override
    default <E> DataTransactionResult offer(Vector3i coordinates, BaseValue<E> value) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), value);
    }

    @Override
    default DataTransactionResult offer(Vector3i coordinates, DataManipulator<?, ?> manipulator) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulator);
    }

    @Override
    default DataTransactionResult offer(Vector3i coordinates, Iterable<DataManipulator<?, ?>> manipulators) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulators);
    }

    @Override
    default DataTransactionResult remove(Vector3i coordinates, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.remove(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    default DataTransactionResult remove(Vector3i coordinates, Key<?> key) {
        return this.remove(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    default DataTransactionResult undo(Vector3i coordinates, DataTransactionResult result) {
        return this.undo(coordinates.getX(), coordinates.getY(), coordinates.getZ(), result);
    }

    @Override
    default DataTransactionResult copyFrom(Vector3i coordinatesTo, Vector3i coordinatesFrom) {
        return this.copyFrom(coordinatesTo.getX(), coordinatesTo.getY(), coordinatesTo.getZ(), coordinatesFrom.getX(),
            coordinatesFrom.getY(), coordinatesFrom.getZ());
    }

    @Override
    default DataTransactionResult copyFrom(Vector3i coordinatesTo, Vector3i coordinatesFrom, MergeFunction function) {
        return this.copyFrom(coordinatesTo.getX(), coordinatesTo.getY(), coordinatesTo.getZ(), coordinatesFrom.getX(),
            coordinatesFrom.getY(), coordinatesFrom.getZ(), function);
    }

    @Override
    default DataTransactionResult copyFrom(Vector3i to, DataHolder from, MergeFunction function) {
        return this.copyFrom(to.getX(), to.getY(), to.getZ(), from, function);
    }

    @Override
    default DataTransactionResult copyFrom(Vector3i to, DataHolder from) {
        return this.copyFrom(to.getX(), to.getY(), to.getZ(), from);
    }

    @Override
    default DataTransactionResult offer(Vector3i coordinates, DataManipulator<?, ?> manipulator, MergeFunction function) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulator, function);
    }

    @Override
    default <E> DataTransactionResult transform(Vector3i coordinates, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return this.transform(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key, function);
    }

    @Override
    default <E> DataTransactionResult offer(Vector3i coordinates, Key<? extends BaseValue<E>> key, E value) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key, value);
    }

    @Override
    default boolean supports(Vector3i coordinates, BaseValue<?> value) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), value);
    }

    @Override
    default <T extends Property<?, ?>> Optional<T> getProperty(Vector3i coordinates, Class<T> propertyClass) {
        return this.getProperty(coordinates.getX(), coordinates.getY(), coordinates.getZ(), propertyClass);
    }

    @Override
    default Collection<Property<?, ?>> getProperties(Vector3i coordinates) {
        return this.getProperties(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }
}
