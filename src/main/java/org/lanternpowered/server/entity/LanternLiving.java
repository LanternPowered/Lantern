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

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.entity.EntityEffect;
import org.lanternpowered.server.effect.entity.EntityEffectCollection;
import org.lanternpowered.server.effect.entity.EntityEffectTypes;
import org.lanternpowered.server.effect.entity.animation.DefaultLivingDeathAnimation;
import org.lanternpowered.server.effect.entity.animation.DefaultLivingHurtAnimation;
import org.lanternpowered.server.effect.entity.sound.DefaultLivingFallSoundEffect;
import org.lanternpowered.server.effect.entity.sound.DefaultLivingSoundEffect;
import org.lanternpowered.server.effect.potion.LanternPotionEffectType;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.util.collect.Lists2;
import org.lanternpowered.server.world.EntitySpawningEntry;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.rules.RuleTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.entity.health.source.HealingSources;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.HarvestEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class LanternLiving extends LanternEntity implements Living {

    public static final EntityEffectCollection DEFAULT_EFFECT_COLLECTION = EntityEffectCollection.builder()
            .add(EntityEffectTypes.HURT, new DefaultLivingSoundEffect(EntityBodyPosition.HEAD, SoundTypes.ENTITY_GENERIC_HURT))
            .add(EntityEffectTypes.HURT, new DefaultLivingHurtAnimation())
            .add(EntityEffectTypes.DEATH, new DefaultLivingSoundEffect(EntityBodyPosition.HEAD, SoundTypes.ENTITY_GENERIC_HURT))
            .add(EntityEffectTypes.DEATH, new DefaultLivingDeathAnimation())
            .add(EntityEffectTypes.FALL, new DefaultLivingFallSoundEffect(
                    SoundTypes.ENTITY_GENERIC_SMALL_FALL,
                    SoundTypes.ENTITY_GENERIC_BIG_FALL))
            .build();

    /**
     * The amount if ticks that a {@link Living} still exists after
     * being killed before it is removed from the {@link World}.
     */
    public static final int DEFAULT_DEATH_BEFORE_REMOVAL_TICKS = 30;

    private Vector3d headRotation = Vector3d.ZERO;
    private long lastFoodTickTime = LanternGame.currentTimeTicks();
    private long lastPeacefulFoodTickTime = LanternGame.currentTimeTicks();
    private long lastPeacefulHealthTickTime = LanternGame.currentTimeTicks();

    private int removeTicks = 0;

    /**
     * Whether this {@link Living} entity is
     * dead, reached zero health.
     */
    private boolean dead;

    public LanternLiving(UUID uniqueId) {
        super(uniqueId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerKeys() {
        super.registerKeys();
        final ValueCollection c = getValueCollection();
        c.register(Keys.MAX_AIR, 300, 0, Integer.MAX_VALUE);
        c.register(Keys.REMAINING_AIR, 300, 0, Keys.MAX_AIR);
        c.register(Keys.MAX_HEALTH, 20.0, 0.0, 1024.0);
        c.register(Keys.HEALTH, 20.0, 0.0, Keys.MAX_HEALTH)
                .addListener((oldElement, newElement) -> {
                    if (newElement <= 0) {
                        handleDeath();
                    }
                });
        c.register((Key<MutableBoundedValue<Double>>) (Key) Keys.ABSORPTION, 0.0, 0.0, 1024.0);
        c.register(Keys.POTION_EFFECTS, new ArrayList<>());
    }

    protected void setRawHeadRotation(Vector3d rotation) {
        this.headRotation = checkNotNull(rotation, "rotation");
    }

    private void handleDeath() {
        // Can happen when a dead player joins, just
        // mark the player as dead since the events
        // have already been thrown.
        if (getWorld() == null) {
            setDead(true);
        }
        if (isDead()) {
            return;
        }
        setDead(true);
        final CauseStack causeStack = CauseStack.current();

        // Only players can keep their inventory
        final boolean keepsInventory = this instanceof LanternPlayer &&
                getWorld().getOrCreateRule(RuleTypes.KEEP_INVENTORY).getValue();

        // Post the entity destruction event
        final DestructEntityEvent.Death event = SpongeEventFactory.createDestructEntityEventDeath(causeStack.getCurrentCause(),
                MessageChannel.TO_NONE, Optional.empty(), new MessageEvent.MessageFormatter(), this, keepsInventory, false);
        postDestructEvent(event);

        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            // Add the destruct event to the cause, this can be used
            // to track the cause of the entity death.
            frame.pushCause(event);
            // Post the harvest event
            handleDeath(causeStack);
        }

        // Clear the inventory, if keepsInventory is false in the thrown Death event
        if (!event.getKeepInventory() && this instanceof Carrier) {
            ((Carrier) this).getInventory().clear();
        }

        final EntityEffectCollection effects = getEffectCollection();
        // Handle the death effect
        Optional<EntityEffect> effect = effects.getCombined(EntityEffectTypes.DEATH);
        if (!effect.isPresent()) {
            effect = effects.getCombined(EntityEffectTypes.HURT);
        }
        effect.ifPresent(e -> e.play(this));
    }

    protected void handleDeath(CauseStack causeStack) {
        final int exp = collectExperience(causeStack);
        // Humanoids get their own sub-interface for the event
        final HarvestEntityEvent harvestEvent;
        if (this instanceof Humanoid) {
            harvestEvent = SpongeEventFactory.createHarvestEntityEventTargetHumanoid(
                    causeStack.getCurrentCause(), exp, exp, (Humanoid) this);
        } else {
            harvestEvent = SpongeEventFactory.createHarvestEntityEventTargetLiving(
                    causeStack.getCurrentCause(), exp, exp, this);
        }
        Sponge.getEventManager().post(harvestEvent);
        // Finalize the harvest event
        finalizeHarvestEvent(causeStack, harvestEvent, new ArrayList<>());
    }

    /**
     * Finalize the {@link HarvestEntityEvent}. This will spawn all
     * the dropped {@link Item}s and {@link ExperienceOrb}s. But only
     * if the event isn't cancelled.
     *
     * @param causeStack The cause stack
     * @param event The harvest event
     */
    protected void finalizeHarvestEvent(CauseStack causeStack, HarvestEntityEvent event, List<ItemStackSnapshot> itemStackSnapshots) {
        if (event.isCancelled()) {
            return;
        }
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(event);

            final int exp = event.getExperience();
            // No experience, don't spawn any entity
            if (exp > 0) {
                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.EXPERIENCE);
                // Spawn a experience orb with the experience value
                LanternWorld.handleEntitySpawning(EntityTypes.EXPERIENCE_ORB, getTransform(),
                        entity -> entity.offer(Keys.CONTAINED_EXPERIENCE, exp));
            }

            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.DROPPED_ITEM);
            // Collect entity drops
            collectDrops(causeStack, itemStackSnapshots);
            if (!itemStackSnapshots.isEmpty()) {
                final DropItemEvent.Pre preDropEvent = SpongeEventFactory.createDropItemEventPre(
                        frame.getCurrentCause(), ImmutableList.copyOf(itemStackSnapshots), Lists2.nonNullOf(itemStackSnapshots));
                Sponge.getEventManager().post(preDropEvent);
                if (!preDropEvent.isCancelled()) {
                    final Transform<World> transform = getTransform().setPosition(
                            getBoundingBox().map(AABB::getCenter).orElse(Vector3d.ZERO));
                    final List<EntitySpawningEntry> entries = itemStackSnapshots.stream()
                            .filter(snapshot -> !snapshot.isEmpty())
                            .map(snapshot -> new EntitySpawningEntry(EntityTypes.ITEM, transform, entity -> {
                                entity.offer(Keys.REPRESENTED_ITEM, snapshot);
                                entity.offer(Keys.PICKUP_DELAY, 15);
                            }))
                            .collect(Collectors.toList());
                    LanternWorld.handleEntitySpawning(entries, SpongeEventFactory::createDropItemEventDestruct);
                }
            }
        }
    }

    /**
     * Pulses this {@link Living} and checks if the entity is still alive, a
     * dead entity will be removed after a specific amount of ticks.
     *
     * @return Whether the entity is dead
     */
    protected boolean pulseDeath(int deltaTicks) {
        if (isDead()) {
            this.removeTicks += deltaTicks;
            // Destroy the entity
            if (this.removeTicks >= DEFAULT_DEATH_BEFORE_REMOVAL_TICKS) {
                super.remove(RemoveState.DESTROYED);
            }
            return true;
        } else {
            // Reset the counter
            this.removeTicks = 0;
            return false;
        }
    }

    /**
     * Collects a experience value from this {@link Living}. This is the
     * amount of experience that will be dropped when the entity is killed.
     * <p>
     * The {@link CauseStack} may be used to retrieve contextual data how
     * the {@link Living} got killed.
     *
     * @param causeStack The cause stack
     * @return The experience value
     */
    protected int collectExperience(CauseStack causeStack) {
        return 0;
    }

    /**
     * Collects all the dropped {@link ItemStackSnapshot}s for this {@link Living}.
     *
     * @param causeStack The cause stack
     * @param itemStackSnapshots The item stack snapshots
     */
    protected void collectDrops(CauseStack causeStack, List<ItemStackSnapshot> itemStackSnapshots) {
    }

    @Override
    protected void handleDamage(CauseStack causeStack, double health) {
        super.handleDamage(causeStack, health);

        // The death animation will be played in handleDeath
        if (health > 0) {
            // Handle the hurt effect
            final EntityEffectCollection effects = getEffectCollection();
            effects.getCombinedOrEmpty(EntityEffectTypes.HURT).play(this);
        }
    }

    @Override
    public boolean isDead() {
        return this.dead;
    }

    @Override
    protected void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public Vector3d getHeadRotation() {
        return this.headRotation;
    }

    @Override
    public void setHeadRotation(Vector3d rotation) {
        setRawHeadRotation(rotation);
    }

    @Override
    public Text getTeamRepresentation() {
        return Text.of(getUniqueId().toString());
    }

    @Override
    protected void pulse(int deltaTicks) {
        if (!pulseDeath(deltaTicks)) {
            pulseLiving(deltaTicks);
        }
    }

    protected void pulseLiving(int deltaTicks) {
        super.pulse(deltaTicks);

        pulsePotions(deltaTicks);
        pulseFood();
    }

    @Override
    public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass) {
        return Optional.empty();
    }

    @Override
    public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass, Vector3d velocity) {
        return Optional.empty();
    }

    private void pulsePotions(int deltaTicks) {
        // TODO: Move potion effects to a component? + The key registration
        final List<PotionEffect> potionEffects = get(Keys.POTION_EFFECTS).get();
        if (!potionEffects.isEmpty()) {
            final PotionEffect.Builder builder = PotionEffect.builder();
            final ImmutableList.Builder<PotionEffect> newPotionEffects = ImmutableList.builder();
            for (PotionEffect potionEffect : potionEffects) {
                final boolean instant = potionEffect.getType().isInstant();
                final int duration = instant ? 1 : potionEffect.getDuration() - deltaTicks;
                if (duration > 0) {
                    final PotionEffect newPotionEffect = builder.from(potionEffect).duration(duration).build();
                    ((LanternPotionEffectType) newPotionEffect.getType()).getEffectConsumer().invoke(this, newPotionEffect);
                    if (!instant) {
                        newPotionEffects.add(newPotionEffect);
                    }
                }
                if (potionEffect.getType() == PotionEffectTypes.GLOWING) {
                    offer(Keys.GLOWING, duration > 0);
                } else if (potionEffect.getType() == PotionEffectTypes.INVISIBILITY) {
                    offer(Keys.INVISIBLE, duration > 0);
                } else if (potionEffect.getType() == PotionEffectTypes.HUNGER && supports(Keys.EXHAUSTION)) {
                    final MutableBoundedValue<Double> exhaustion = getValue(Keys.EXHAUSTION).get();
                    final double value = exhaustion.get() + (double) deltaTicks * 0.005 * (potionEffect.getAmplifier() + 1.0);
                    offer(Keys.EXHAUSTION, Math.min(value, exhaustion.getMaxValue()));
                } else if (potionEffect.getType() == PotionEffectTypes.SATURATION && supports(FoodData.class)) {
                    final int amount = potionEffect.getAmplifier() + 1;
                    final int food = Math.min(get(Keys.FOOD_LEVEL).get() + amount, get(LanternKeys.MAX_FOOD_LEVEL).get());
                    offer(Keys.FOOD_LEVEL, food);
                    offer(Keys.SATURATION, Math.min(get(Keys.SATURATION).get() + (amount * 2), food));
                }
            }
            offer(Keys.POTION_EFFECTS, newPotionEffects.build());
        }
    }

    private void pulseFood() {
        if (!supports(FoodData.class) || get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
            return;
        }
        final Difficulty difficulty = getWorld().getDifficulty();

        MutableBoundedValue<Double> exhaustion = getValue(Keys.EXHAUSTION).get();
        MutableBoundedValue<Double> saturation = getValue(Keys.SATURATION).get();
        MutableBoundedValue<Integer> foodLevel = getValue(Keys.FOOD_LEVEL).get();

        if (exhaustion.get() > 4.0) {
            if (saturation.get() > saturation.getMinValue()) {
                offer(Keys.SATURATION, Math.max(saturation.get() - 1.0, saturation.getMinValue()));
                // Get the updated saturation
                saturation = getValue(Keys.SATURATION).get();
            } else if (!difficulty.equals(Difficulties.PEACEFUL)) {
                offer(Keys.FOOD_LEVEL, Math.max(foodLevel.get() - 1, foodLevel.getMinValue()));
                // Get the updated food level
                foodLevel = getValue(Keys.FOOD_LEVEL).get();
            }
            offer(Keys.EXHAUSTION, Math.max(exhaustion.get() - 4.0, exhaustion.getMinValue()));
            exhaustion = getValue(Keys.EXHAUSTION).get();
        }

        final boolean naturalRegeneration = getWorld().getOrCreateRule(RuleTypes.NATURAL_REGENERATION).getValue();
        final long currentTickTime = LanternGame.currentTimeTicks();

        if (naturalRegeneration && saturation.get() > saturation.getMinValue() && foodLevel.get() >= foodLevel.getMaxValue()) {
            if ((currentTickTime - this.lastFoodTickTime) >= 10) {
                final double amount = Math.min(saturation.get(), 6.0);
                heal(amount / 6.0, HealingSources.FOOD);
                offer(Keys.EXHAUSTION, Math.min(exhaustion.get() + amount, exhaustion.getMaxValue()));
                this.lastFoodTickTime = currentTickTime;
            }
        } else if (naturalRegeneration && foodLevel.get() >= 18) {
            if ((currentTickTime - this.lastFoodTickTime) >= 80) {
                heal(1.0, HealingSources.FOOD);
                offer(Keys.EXHAUSTION, Math.min(6.0 + exhaustion.get(), exhaustion.getMaxValue()));
                this.lastFoodTickTime = currentTickTime;
            }
        } else if (foodLevel.get() <= foodLevel.getMinValue()) {
            if ((currentTickTime - this.lastFoodTickTime) >= 80) {
                final double health = get(Keys.HEALTH).orElse(20.0);
                if ((health > 10.0 && difficulty.equals(Difficulties.EASY))
                        || (health > 1.0 && difficulty.equals(Difficulties.NORMAL))
                        || difficulty.equals(Difficulties.HARD)) {
                    damage(1.0, DamageSources.STARVATION);
                }
                this.lastFoodTickTime = currentTickTime;
            }
        } else {
            this.lastFoodTickTime = currentTickTime;
        }

        // Peaceful restoration
        if (naturalRegeneration && difficulty.equals(Difficulties.PEACEFUL)) {
            if (currentTickTime - this.lastPeacefulHealthTickTime >= 20) {
                heal(1.0, HealingSources.MAGIC);
                this.lastPeacefulHealthTickTime = currentTickTime;
            }

            final int oldFoodLevel = get(Keys.FOOD_LEVEL).orElse(0);
            if (currentTickTime - this.lastPeacefulFoodTickTime >= 10
                    && oldFoodLevel < get(LanternKeys.MAX_FOOD_LEVEL).orElse(20)) {
                offer(Keys.FOOD_LEVEL, oldFoodLevel + 1);
                this.lastPeacefulFoodTickTime = currentTickTime;
            }
        }
    }
}
