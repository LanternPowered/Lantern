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
package org.lanternpowered.server.entity;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.AdditionalContainerHolder;
import org.lanternpowered.server.data.IImmutableDataHolder;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.IStorePropertyHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Location;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

// TODO
public class LanternEntitySnapshot implements EntitySnapshot, IImmutableDataHolder<EntitySnapshot>,
        IStorePropertyHolder, AdditionalContainerHolder<ImmutableDataManipulator<?,?>> {

    @Override
    public AdditionalContainerCollection<ImmutableDataManipulator<?, ?>> getAdditionalContainers() {
        return null;
    }

    @Override
    public UUID getWorldUniqueId() {
        return null;
    }

    @Override
    public Vector3i getPosition() {
        return null;
    }

    @Override
    public Optional<Location> getLocation() {
        return null;
    }

    @Override
    public EntitySnapshot withLocation(Location location) {
        return null;
    }

    @Override
    public Optional<UUID> getUniqueId() {
        return null;
    }

    @Override
    public Optional<Transform> getTransform() {
        return null;
    }

    @Override
    public EntityType getType() {
        return null;
    }

    @Override
    public Optional<Entity> restore() {
        return null;
    }

    @Override
    public EntityArchetype createArchetype() {
        return null;
    }

    @Override
    public <E> Optional<EntitySnapshot> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return null;
    }

    @Override
    public <E> Optional<EntitySnapshot> with(Key<? extends BaseValue<E>> key, E value) {
        return null;
    }

    @Override
    public Optional<EntitySnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        return null;
    }

    @Override
    public Optional<EntitySnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        return null;
    }

    @Override
    public Optional<EntitySnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return null;
    }

    @Override
    public EntitySnapshot merge(EntitySnapshot that) {
        return null;
    }

    @Override
    public EntitySnapshot merge(EntitySnapshot that, MergeFunction function) {
        return null;
    }

    @Override
    public ValueCollection getValueCollection() {
        return null;
    }
}
