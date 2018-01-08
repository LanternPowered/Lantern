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

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.util.VecHelper;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
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
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.Functional;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.extent.Extent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class ExtentViewDownsize implements AbstractExtent {

    private final Extent extent;
    private final Vector3i blockMin;
    private final Vector3i blockMax;
    private final Vector3i blockSize;
    private final Vector3i biomeMin;
    private final Vector3i biomeMax;
    private final Vector3i biomeSize;

    public ExtentViewDownsize(Extent extent, Vector3i blockMin, Vector3i blockMax) {
        this.extent = extent;
        this.blockMin = blockMin;
        this.blockMax = blockMax;
        this.blockSize = this.blockMax.sub(this.blockMin).add(Vector3i.ONE);
        this.biomeMin = new Vector3i(blockMin.getX(), 0, blockMin.getZ());
        this.biomeMax = new Vector3i(blockMax.getX(), 1, blockMax.getZ());
        this.biomeSize = this.biomeMax.sub(this.biomeMin).add(Vector3i.ONE.mul(1, 0, 1));
    }

    @Override
    public Location<? extends Extent> getLocation(Vector3d position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return new Location<>(this, position);
    }

    @Override
    public int getHighestYAt(int x, int z) {
        checkRange(x, this.blockMin.getY(), z);
        final int y = this.extent.getHighestYAt(x, z);
        return GenericMath.clamp(y, this.blockMin.getY(), this.blockMax.getY());
    }

    @Override
    public int getPrecipitationLevelAt(int x, int z) {
        checkRange(x, this.blockMin.getY(), z);
        final int y = this.extent.getPrecipitationLevelAt(x, z);
        return GenericMath.clamp(y, this.blockMin.getY(), this.blockMax.getY());
    }

    @Override
    public Location<? extends Extent> getLocation(Vector3i position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return new Location<>(this, position);
    }

    @Override
    public UUID getUniqueId() {
        return this.extent.getUniqueId();
    }

    @Override
    public boolean isLoaded() {
        return this.extent.isLoaded();
    }

    @Override
    public Vector3i getBiomeMin() {
        return this.biomeMin;
    }

    @Override
    public Vector3i getBiomeMax() {
        return this.biomeMax;
    }

    @Override
    public Vector3i getBiomeSize() {
        return this.biomeSize;
    }

    @Override
    public boolean containsBiome(int x,  int y, int z) {
        return VecHelper.inBounds(x, y, z, this.biomeMin, this.biomeMax);
    }

    private void checkBiomeRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.biomeMin, this.biomeMax)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.biomeMin, this.biomeMax);
        }
    }

    @Override
    public boolean hitBlock(int x, int y, int z, Direction side, GameProfile profile) {
        checkRange(x, y, z);
        return this.extent.hitBlock(x, y, z, side, profile);
    }

    @Override
    public boolean interactBlock(int x, int y, int z, Direction side, GameProfile profile) {
        checkRange(x, y, z);
        return this.extent.interactBlock(x, y, z, side, profile);
    }

    @Override
    public boolean interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side, GameProfile profile) {
        checkRange(x, y, z);
        return this.extent.interactBlockWith(x, y, z, itemStack, side, profile);
    }

    @Override
    public boolean placeBlock(int x, int y, int z, BlockState block, Direction direction, GameProfile profile) {
        checkRange(x, y, z);
        return this.extent.placeBlock(x, y, z, block, direction, profile);
    }

    @Override
    public boolean digBlock(int x, int y, int z, GameProfile profile) {
        checkRange(x, y, z);
        return this.extent.digBlock(x, y, z, profile);
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack, GameProfile profile) {
        checkRange(x, y, z);
        return this.extent.digBlockWith(x, y, z, itemStack, profile);
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack, GameProfile profile) {
        checkRange(x, y, z);
        return this.extent.getBlockDigTimeWith(x, y, z, itemStack, profile);
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        checkBiomeRange(x, y, z);
        return this.extent.getBiome(x, y, z);
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        checkBiomeRange(x, y, z);
        this.extent.setBiome(x, y, z, biome);
    }

    @Override
    public Vector3i getBlockMax() {
        return this.blockMax;
    }

    @Override
    public Vector3i getBlockMin() {
        return this.blockMin;
    }

    @Override
    public Vector3i getBlockSize() {
        return this.blockSize;
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.blockMin, this.blockMax);
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return this.getBlock(x, y, z).getType();
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getBlock(x, y, z);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Direction direction, Class<T> propertyClass) {
        checkRange(x, y, z);
        return this.extent.getProperty(x, y, z, direction, propertyClass);
    }

    @Override
    public Collection<Direction> getFacesWithProperty(int x, int y, int z, Class<? extends Property<?, ?>> propertyClass) {
        checkRange(x, y, z);
        return this.extent.getFacesWithProperty(x, y, z, propertyClass);
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState block, BlockChangeFlag flag) {
        checkRange(x, y, z);
        return this.extent.setBlock(x, y, z, block, flag);
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState blockState) {
        checkRange(x, y, z);
        return this.extent.setBlock(x, y, z, blockState);
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.createSnapshot(x, y, z);
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force, BlockChangeFlag flag) {
        checkRange(x, y, z);
        return this.extent.restoreSnapshot(x, y, z, snapshot, force, flag);
    }

    @Override
    public Optional<Entity> restoreSnapshot(EntitySnapshot snapshot, Vector3d position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.restoreSnapshot(snapshot, position);
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        final Vector3d pos = entity.getLocation().getPosition();
        checkRange(pos.getX(), pos.getY(), pos.getZ());
        return this.extent.spawnEntity(entity);
    }

    @Override
    public Collection<Entity> spawnEntities(Iterable<? extends Entity> entities) {
        for (Entity entity : entities) {
            final Vector3d pos = entity.getLocation().getPosition();
            checkRange(pos.getX(), pos.getY(), pos.getZ());
        }
        return this.extent.spawnEntities(entities);
    }

    @Override
    public Set<Entity> getIntersectingEntities(AABB box, Predicate<Entity> filter) {
        checkRange(box.getMin().getX(), box.getMin().getY(), box.getMin().getZ());
        checkRange(box.getMax().getX(), box.getMax().getY(), box.getMax().getZ());
        return this.extent.getIntersectingEntities(box, filter);
    }

    @Override
    public Set<EntityHit> getIntersectingEntities(Vector3d start, Vector3d direction, double distance, Predicate<EntityHit> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        final Vector3i max = this.blockMax.add(Vector3i.ONE);
        return this.extent.getIntersectingEntities(start, direction, distance,
                Functional.predicateAnd(hit -> VecHelper.inBounds(hit.getEntity().getLocation().getPosition(), this.blockMin, max), filter));
    }

    @Override
    public Set<EntityHit> getIntersectingEntities(Vector3d start, Vector3d end, Predicate<EntityHit> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        final Vector3i max = this.blockMax.add(Vector3i.ONE);
        return this.extent.getIntersectingEntities(start, end,
                Functional.predicateAnd(hit -> VecHelper.inBounds(hit.getEntity().getLocation().getPosition(), this.blockMin, max), filter));
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getScheduledUpdates(x, y, z);
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        checkRange(x, y, z);
        return this.extent.addScheduledUpdate(x, y, z, priority, ticks);
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        checkRange(x, y, z);
        this.extent.removeScheduledUpdate(x, y, z, update);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        checkRange(x, y, z);
        return this.extent.getProperty(x, y, z, propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getProperties(x, y, z);
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        checkRange(x, y, z);
        return this.extent.get(x, y, z, key);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.get(x, y, z, manipulatorClass);
    }

    @Override
    public Set<ImmutableValue<?>> getValues(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getValues(x, y, z);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.getOrCreate(x, y, z, manipulatorClass);
    }

    @Override
    public <E> E getOrNull(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        checkRange(x, y, z);
        return this.extent.getOrNull(x, y, z, key);
    }

    @Override
    public <E> E getOrElse(int x, int y, int z, Key<? extends BaseValue<E>> key, E defaultValue) {
        checkRange(x, y, z);
        return this.extent.getOrElse(x, y, z, key, defaultValue);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        checkRange(x, y, z);
        return this.extent.getValue(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        checkRange(x, y, z);
        return this.extent.supports(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, BaseValue<?> value) {
        checkRange(x, y, z);
        return this.extent.supports(x, y, z, value);
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.supports(x, y, z, manipulatorClass);
    }

    @Override
    public boolean supports(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        checkRange(x, y, z);
        return this.extent.supports(x, y, z, manipulator);
    }

    @Override
    public Set<Key<?>> getKeys(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getKeys(x, y, z);
    }

    @Override
    public <E> DataTransactionResult transform(int x, int y, int z, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        checkRange(x, y, z);
        return this.extent.transform(x, y, z, key, function);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, BaseValue<E> value) {
        checkRange(x, y, z);
        return this.extent.offer(x, y, z, value);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        checkRange(x, y, z);
        return this.extent.offer(x, y, z, key, value);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator) {
        checkRange(x, y, z);
        return this.extent.offer(x, y, z, manipulator);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        checkRange(x, y, z);
        return this.extent.offer(x, y, z, manipulator, function);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, Iterable<DataManipulator<?, ?>> manipulators) {
        checkRange(x, y, z);
        return this.extent.offer(x, y, z, manipulators);
    }

    @Override
    public DataTransactionResult offer(Vector3i blockPosition, Iterable<DataManipulator<?, ?>> values, MergeFunction function) {
        checkRange(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        return this.extent.offer(blockPosition, values, function);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        checkRange(x, y, z);
        return this.extent.remove(x, y, z, key);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.remove(x, y, z, manipulatorClass);
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        checkRange(x, y, z);
        return this.extent.undo(x, y, z, result);
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getManipulators(x, y, z);
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        checkRange(x, y, z);
        return this.extent.validateRawData(x, y, z, container);
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        checkRange(x, y, z);
        this.extent.setRawData(x, y, z, container);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from) {
        checkRange(xTo, yTo, zTo);
        return this.extent.copyFrom(xTo, yTo, zTo, from);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom) {
        checkRange(xTo, yTo, zTo);
        checkRange(xFrom, yFrom, zFrom);
        return this.extent.copyFrom(xTo, yTo, zTo, xFrom, yFrom, zFrom);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from, MergeFunction function) {
        checkRange(xTo, yTo, zTo);
        return this.extent.copyFrom(xTo, yTo, zTo, from, function);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        checkRange(xTo, yTo, zTo);
        checkRange(xFrom, yFrom, zFrom);
        return this.extent.copyFrom(xTo, yTo, zTo, xFrom, yFrom, zFrom, function);
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        final Collection<TileEntity> tileEntities = this.extent.getTileEntities();
        for (Iterator<TileEntity> iterator = tileEntities.iterator(); iterator.hasNext(); ) {
            final TileEntity tileEntity = iterator.next();
            final Location<World> block = tileEntity.getLocation();
            if (!VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.blockMin, this.blockMax)) {
                iterator.remove();
            }
        }
        return tileEntities;
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        return this.extent.getTileEntities(Functional.predicateAnd(
                new TileEntityInBounds(this.blockMin, this.blockMax), filter));
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getTileEntity(x, y, z);
    }

    @Override
    public Entity createEntity(EntityType type, Vector3i position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(type, position);
    }

    @Override
    public Entity createEntityNaturally(EntityType type, Vector3d position) throws IllegalArgumentException, IllegalStateException {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntityNaturally(type, position);
    }

    @Override
    public Optional<Entity> getEntity(UUID uuid) {
        final Optional<Entity> optEntity = this.extent.getEntity(uuid);
        if (optEntity.isPresent()) {
            final Vector3d pos = ((LanternEntity) optEntity.get()).getPosition();
            if (VecHelper.inBounds(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), this.blockMin, this.blockMax)) {
                return optEntity;
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Entity> getEntities() {
        final Collection<Entity> entities = this.extent.getEntities();
        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
            final Entity entity = iterator.next();
            final Vector3d pos = ((LanternEntity) entity).getPosition();
            if (!VecHelper.inBounds(pos.getX(), pos.getY(), pos.getZ(), this.blockMin, this.blockMax)) {
                iterator.remove();
            }
        }
        return entities;
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        return this.extent.getEntities(Functional.predicateAnd(input -> {
            final Vector3d pos = ((LanternEntity) input).getPosition();
            return VecHelper.inBounds(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), this.blockMin, this.blockMax);
        }, filter));
    }

    @Override
    public Entity createEntity(EntityType type, Vector3d position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(type, position);
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        // TODO once entity containers are implemented
        // checkRange(position.getX(), position.getY(), position.getZ());
        return Optional.empty();
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(entityContainer, position);
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new ExtentViewDownsize(this.extent, newMin, newMax);
    }

    @Override
    public Optional<UUID> getCreator(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getCreator(x, y, z);
    }

    @Override
    public Optional<UUID> getNotifier(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getNotifier(x, y, z);
    }

    @Override
    public void setCreator(int x, int y, int z, @Nullable UUID uuid) {
        checkRange(x, y, z);
        this.extent.setCreator(x, y, z, uuid);
    }

    @Override
    public void setNotifier(int x, int y, int z, @Nullable UUID uuid) {
        checkRange(x, y, z);
        this.extent.setNotifier(x, y, z, uuid);
    }

    @Override
    public Optional<AABB> getBlockSelectionBox(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getBlockSelectionBox(x, y, z);
    }

    @Override
    public Set<AABB> getIntersectingBlockCollisionBoxes(AABB box) {
        checkRange(box.getMin().getX(), box.getMin().getY(), box.getMin().getZ());
        checkRange(box.getMax().getX(), box.getMax().getY(), box.getMax().getZ());
        return this.extent.getIntersectingBlockCollisionBoxes(box);
    }

    @Override
    public Set<AABB> getIntersectingCollisionBoxes(Entity owner, AABB box) {
        checkRange(box.getMin().getX(), box.getMin().getY(), box.getMin().getZ());
        checkRange(box.getMax().getX(), box.getMax().getY(), box.getMax().getZ());
        return this.extent.getIntersectingCollisionBoxes(owner, box);
    }

    @Override
    public ArchetypeVolume createArchetypeVolume(Vector3i min, Vector3i max, Vector3i origin) {
        checkRange(min.getX(), min.getY(), min.getZ());
        checkRange(max.getX(), max.getY(), max.getZ());
        return this.extent.createArchetypeVolume(min, max, origin);
    }

    private static class TileEntityInBounds implements Predicate<TileEntity> {

        private final Vector3i min;
        private final Vector3i max;

        private TileEntityInBounds(Vector3i min, Vector3i max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean test(TileEntity input) {
            final Location<World> block = input.getLocation();
            return VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.min, this.max);
        }
    }
}
