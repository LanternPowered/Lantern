package org.lanternpowered.server.event;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class LanternEventHelper {

    public static Entity createDroppedItem(Location<World> location, ItemStackSnapshot snapshot) {
        final Entity entity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
        entity.offer(Keys.REPRESENTED_ITEM, snapshot);
        entity.offer(Keys.PICKUP_DELAY, 60);
        return entity;
    }

    public static void fireDropItemEventDispense(Cause cause, Consumer<List<Entity>> consumer) {
        final List<Entity> entities = new ArrayList<>();
        consumer.accept(entities);

        final SpawnEntityEvent event = SpongeEventFactory.createDropItemEventDispense(cause, entities);
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            finishSpawnEntityEvent(event);
        }
    }

    public static void finishSpawnEntityEvent(SpawnEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Cause cause = Cause.source(event).build();
        for (Entity entity : event.getEntities()) {
            entity.getWorld().spawnEntity(entity, cause);
        }
    }
}
