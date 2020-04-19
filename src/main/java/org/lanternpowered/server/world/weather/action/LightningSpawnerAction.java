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
package org.lanternpowered.server.world.weather.action;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.lanternpowered.api.script.ScriptContext;
import org.lanternpowered.api.script.context.Parameters;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.server.util.collect.Collections3;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.LanternChunk;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.action.LightningEvent;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.math.vector.Vector3d;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LightningSpawnerAction implements Action {

    private static final AABB MOVE_TO_ENTITY_REGION = new AABB(-1.5, -3.0, -1.5, 1.5, 256.0, 1.5);

    @Expose
    @SerializedName("attempts-per-chunk")
    private int attemptsPerChunk = 2;

    @Expose
    @SerializedName("chance")
    private float chance = 0.0000002f;

    @Override
    public void run(ScriptContext scriptContext) {
        final LanternWorld world = (LanternWorld) scriptContext.get(Parameters.WORLD).get();

        final Random random = ThreadLocalRandom.current();
        final Iterable<Chunk> chunks = world.getLoadedChunks();
        final int chance = (int) (1f / Math.max(this.chance, 0.000000000001f));

        for (Chunk chunk : chunks) {
            for (int i = 0; i < this.attemptsPerChunk; i++) {
                final LanternChunk lanternChunk = (LanternChunk) chunk;
                if (random.nextInt(chance) != 0) {
                    continue;
                }

                final int value = random.nextInt(0x10000);
                final int x = lanternChunk.getX() << 4 | value & 0xf;
                final int z = lanternChunk.getZ() << 4 | (value >> 4) & 0xf;

                Vector3d pos = new Vector3d(x, world.getHighestYAt(x, z), z);

                // Look for nearby entities to see if the lightning bolt should be moved
                final AABB moveToEntityRegion = MOVE_TO_ENTITY_REGION.offset(pos);
                final Entity targetEntity = Collections3.pickRandomElement(world.getIntersectingEntities(moveToEntityRegion));
                if (targetEntity != null) {
                    pos = targetEntity.getLocation().getPosition();
                }

                LanternWorld.handleEntitySpawning(EntityTypes.LIGHTNING, new Transform<>(world, pos), entity -> {}, constructEvent -> {
                    final LightningEvent.Pre lightningPreEvent = SpongeEventFactory.createLightningEventPre(constructEvent.getCause());
                    Sponge.getEventManager().post(lightningPreEvent);
                    if (lightningPreEvent.isCancelled()) { // Cancel entity construction if the pre lighting is cancelled
                        constructEvent.setCancelled(true);
                    }
                });
            }
        }
    }
}
