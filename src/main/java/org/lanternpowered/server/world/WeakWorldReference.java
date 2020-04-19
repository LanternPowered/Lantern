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
package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a weak reference to a {@link World}.
 */
public final class WeakWorldReference {

    @Nullable private WeakReference<World> world;
    private final UUID uniqueId;

    /**
     * Creates a new weak world reference.
     * 
     * @param world the world
     */
    public WeakWorldReference(World world) {
        this.world = new WeakReference<>(checkNotNull(world, "world"));
        this.uniqueId = world.getUniqueId();
    }

    /**
     * Creates a new weak world reference with the unique id of the world.
     * 
     * @param uniqueId the unique id
     */
    public WeakWorldReference(UUID uniqueId) {
        this.uniqueId = checkNotNull(uniqueId, "uniqueId");
    }

    /**
     * Gets the unique id of the world of this reference.
     * 
     * @return the unique id
     */
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    /**
     * Gets the world of this reference, this world may be
     * {@link Optional#empty()} if it couldn't be found.
     * 
     * @return the world if present, otherwise {@link Optional#empty()}
     */
    public Optional<World> getWorld() {
        World world = this.world == null ? null : this.world.get();
        if (world != null) {
            return Optional.of(world);
        }
        world = Sponge.getServer().getWorld(this.uniqueId).orElse(null);
        if (world != null) {
            this.world = new WeakReference<>(world);
            return Optional.of(world);
        }
        return Optional.empty();
    }

    public Location toLocation(Vector3i position) {
        return getWorld().map(world -> new Location(world, position)).orElseGet(() -> new Location(getUniqueId(), position));
    }

    public Location toLocation(Vector3d position) {
        return getWorld().map(world -> new Location(world, position)).orElseGet(() -> new Location(getUniqueId(), position));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("uniqueId", this.uniqueId)
                .add("name", getWorld().map(World::getName).orElse(null))
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WeakWorldReference)) {
            return false;
        }
        final WeakWorldReference other = (WeakWorldReference) obj;
        return other.uniqueId.equals(this.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.uniqueId);
    }
}
