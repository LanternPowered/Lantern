package org.lanternpowered.server.world;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;

public final class WeakWorldReference {

    private WeakReference<World> world;
    private final UUID uniqueId;

    /**
     * Creates a new weak world reference.
     * 
     * @param world the world
     */
    public WeakWorldReference(World world) {
        checkNotNull(world, "world");
        this.world = new WeakReference<World>(world);
        this.uniqueId = world.getUniqueId();
    }

    /**
     * Creates a new weak world reference with the unique id of the world.
     * 
     * @param uniqueId the unique id
     */
    public WeakWorldReference(UUID uniqueId) {
        checkNotNull(uniqueId, "uniqueId");
        this.uniqueId = uniqueId;
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
        World world = this.world.get();
        if (world != null) {
            return Optional.of(world);
        }
        world = LanternGame.get().getServer().getWorld(this.uniqueId).orNull();
        if (world != null) {
            this.world = new WeakReference<World>(world);
            return Optional.of(world);
        }
        return Optional.absent();
    }

    /**
     * Creates a copy of the weak world reference.
     * 
     * @return the copy
     */
    public WeakWorldReference copy() {
        return !this.world.isEnqueued() ? new WeakWorldReference(this.world.get()) : new WeakWorldReference(this.uniqueId);
    }
}
