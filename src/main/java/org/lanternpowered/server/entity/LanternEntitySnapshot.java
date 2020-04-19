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
package org.lanternpowered.server.entity;

import org.lanternpowered.server.data.LocalImmutableDataHolder;
import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.data.property.PropertyHolderBase;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.MergeFunction;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.world.Location;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;
import java.util.UUID;

// TODO
public class LanternEntitySnapshot implements EntitySnapshot, LocalImmutableDataHolder<EntitySnapshot>, PropertyHolderBase {

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
    public <E> Optional<EntitySnapshot> with(Key<? extends Value<E>> key, E value) {
        return null;
    }

    @Override
    public EntitySnapshot merge(EntitySnapshot that, MergeFunction function) {
        return null;
    }

    @Override
    public LocalKeyRegistry getKeyRegistry() {
        return null;
    }
}
