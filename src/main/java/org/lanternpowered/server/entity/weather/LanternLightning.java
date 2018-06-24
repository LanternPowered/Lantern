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
package org.lanternpowered.server.entity.weather;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.entity.EntityEffectCollection;
import org.lanternpowered.server.effect.entity.EntityEffectTypes;
import org.lanternpowered.server.effect.entity.sound.weather.LightningSoundEffect;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.game.registry.type.cause.DamageTypeRegistryModule;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.action.LightningEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.BlockChangeFlags;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LanternLightning extends LanternEntity implements AbstractLightning {

    public static final EntityEffectCollection DEFAULT_SOUND_COLLECTION = EntityEffectCollection.builder()
            .add(EntityEffectTypes.LIGHTNING, new LightningSoundEffect())
            .build();

    /**
     * The region that the {@link Lightning} will damage {@link Entity}s.
     */
    private static final AABB ENTITY_STRIKE_REGION = new AABB(-3, -3, -3, 3, 9, 3);

    /**
     * The amount of ticks that the lightning will be alive.
     */
    private int ticksToLive = 10;
    private boolean remove;

    public LanternLightning(UUID uniqueId) {
        super(uniqueId);
        setEntityProtocolType(EntityProtocolTypes.LIGHTNING);
        setEffectCollection(DEFAULT_SOUND_COLLECTION.copy());
        setSoundCategory(SoundCategories.WEATHER);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        getValueCollection().registerNonRemovable(LanternKeys.IS_EFFECT, false);
    }

    @Override
    public void pulse(int deltaTicks) {
        super.pulse(deltaTicks);

        this.ticksToLive -= deltaTicks;
        if (this.ticksToLive > 0) {
            return;
        }
        if (this.remove) {
            try (CauseStack.Frame frame = CauseStack.current().pushCauseFrame()) {
                // Add this entity to the cause of removal
                frame.pushCause(this);
                // Throw the expire and post event
                final Cause cause = frame.getCurrentCause();
                Sponge.getEventManager().post(SpongeEventFactory.createExpireEntityEvent(cause, this));
                Sponge.getEventManager().post(SpongeEventFactory.createLightningEventPost(cause));
                // Remove the entity
                remove();
            }
        } else {
            // Remove the entity the next pulse
            this.remove = true;

            // Play the sound effect
            getEffectCollection().getCombinedOrEmpty(EntityEffectTypes.LIGHTNING).play(this);

            try (CauseStack.Frame frame = CauseStack.current().pushCauseFrame()) {
                final LanternWorld world = getWorld();
                // Add this entity to the cause of the strike
                frame.pushCause(this);

                final List<Entity> entities;
                if (isEffect()) {
                    entities = new ArrayList<>();
                } else {
                    // Move the entity strike region to the lightning position
                    final AABB strikeRegion = ENTITY_STRIKE_REGION.offset(getPosition());
                    // Get all intersecting entities
                    entities = new ArrayList<>(world.getIntersectingEntities(strikeRegion, entity -> entity != this));
                }

                final List<Transaction<BlockSnapshot>> blockChanges = new ArrayList<>();
                // TODO: Create fire, once fire is implemented

                final LightningEvent.Strike strikeEvent = SpongeEventFactory.createLightningEventStrike(
                        frame.getCurrentCause(), entities, blockChanges);
                Sponge.getEventManager().post(strikeEvent);
                if (!strikeEvent.isCancelled()) {
                    // Construct the damage source
                    final EntityDamageSource damageSource = EntityDamageSource.builder()
                            .entity(this).type(DamageTypeRegistryModule.LIGHTNING).build();
                    // Apply all the block changes
                    for (Transaction<BlockSnapshot> transaction : blockChanges) {
                        if (transaction.isValid()) {
                            transaction.getFinal().restore(false, BlockChangeFlags.ALL);
                        }
                    }
                    // Damage all the entities within the region, or trigger other effects, like zombie pigman etc.
                    for (Entity entity : entities) {
                        entity.damage(5.0, damageSource);
                    }
                }
            }
        }
    }
}
