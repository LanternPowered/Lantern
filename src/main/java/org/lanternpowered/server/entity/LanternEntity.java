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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.LocalMutableDataHolder;
import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.data.SerializableLocalMutableDataHolder;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.entity.EntityEffectCollection;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.event.LanternEventContextKeys;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.registry.type.entity.EntityTypeRegistryModule;
import org.lanternpowered.server.network.entity.EntityProtocolType;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.util.LanternTransform;
import org.lanternpowered.server.util.Quaternions;
import org.lanternpowered.server.world.LanternLocation;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BoundedValue;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.DamageFunction;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.translation.FixedTranslation;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.math.imaginary.Quaterniond;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternEntity implements Entity, SerializableLocalMutableDataHolder {

    // The unique id of this entity
    private final UUID uniqueId;

    // The entity type of this entity
    @Nullable private EntityType<?> entityType;

    // The random object of this entity
    private final Random random = new Random();

    // The raw value map
    private final LocalKeyRegistry<? extends LanternEntity> localKeyRegistry = LocalKeyRegistry.of();

    // The world this entity is located in, may be null
    private LanternWorld world;

    // The position of the entity
    private Vector3d position = Vector3d.ZERO;

    // The rotation of the entity
    private Vector3d rotation = Vector3d.ZERO;

    // The scale of the entity
    private Vector3d scale = Vector3d.ONE;

    /**
     * The {@link EntityEffectCollection} of this entity.
     */
    private EntityEffectCollection effectCollection = EntityEffectCollection.build();

    /**
     * The entity protocol type of this entity.
     */
    @Nullable private EntityProtocolType<?> entityProtocolType;

    /**
     * The state of the removal of this entity.
     */
    @Nullable private RemoveState removeState;

    private boolean onGround = true;

    @Nullable private volatile Vector3i lastChunkCoords;

    /**
     * The base of the {@link AABB} of this entity.
     */
    @Nullable private AABB boundingBoxBase;
    @Nullable private AABB boundingBox;

    @Nullable private LanternEntity vehicle;
    private final List<LanternEntity> passengers = new ArrayList<>();

    private long lastPulseTime = -1;
    private long voidDamageCounter;

    private SoundCategory soundCategory = SoundCategories.NEUTRAL.get();

    @Override
    public LocalKeyRegistry<? extends LanternEntity> getKeyRegistry() {
        return this.localKeyRegistry;
    }

    public enum RemoveState {
        /**
         * The entity was destroyed through the {@link #remove()}
         * method (or when it reached zero health). Will not be
         * respawned in any case.
         */
        DESTROYED,
        /**
         * The entity was removed due chunk unloading. It will appear
         * as "removed", but it is basicly just unloaded.
         */
        CHUNK_UNLOAD,
    }

    public LanternEntity(UUID uniqueId) {
        this.uniqueId = uniqueId;
        registerKeys();
    }

    public void registerKeys() {
        final LocalKeyRegistry<LanternEntity> c = getKeyRegistry().forHolder(LanternEntity.class);
        c.register(Keys.DISPLAY_NAME, Text.empty());
        c.register(Keys.IS_CUSTOM_NAME_VISIBLE, true);
        c.register(Keys.TAGS, new HashSet<>());
        c.register(Keys.VELOCITY, Vector3d.ZERO);
        c.register(Keys.FIRE_TICKS, 0);
        c.register(Keys.FALL_DISTANCE, 0.0);
        c.register(Keys.IS_GLOWING, false);
        c.register(Keys.IS_INVISIBLE, false);
        c.register(Keys.INVULNERABLE, false);
        c.register(Keys.IS_GRAVITY_AFFECTED, true);
        c.register(Keys.CREATOR);
        c.register(Keys.NOTIFIER);
        c.register(LanternKeys.PORTAL_COOLDOWN_TICKS, 0);
        c.registerProvider(Keys.ON_GROUND, builder -> builder.get(holder -> holder.onGround));
        // TODO: Setting base vehicle?
        c.registerProvider(Keys.BASE_VEHICLE, builder -> builder.get(LanternEntity::getBaseVehicle));
        c.registerProvider(Keys.PASSENGERS, builder -> {
            builder.get(LanternEntity::getPassengers);
            builder.removeFast(entity -> {
                entity.clearPassengers();
                return true;
            });
            // TODO: Offering
        });
        c.registerProvider(Keys.VEHICLE, builder -> {
            builder.get(holder -> holder.getVehicle().orElse(null));
            builder.offerFast((entity, vehicle) -> {
                entity.setVehicle(vehicle);
                return true;
            });
            builder.removeFast(entity -> {
                entity.setVehicle(null);
                return true;
            });
        });
    }

    /**
     * Gets the {@link Direction} that the entity is looking.
     *
     * @param division The division
     * @return The direction
     */
    public Direction getDirection(Direction.Division division) {
        return Direction.getClosest(getDirectionVector(), division);
    }

    public Vector3d getDirectionVector() {
        final Vector3d rotation = get(Keys.HEAD_ROTATION).orElse(this.rotation);
        // Invert the x direction because west and east are swapped
        return Quaternions.fromAxesAnglesDeg(rotation.mul(1, -1, 1)).getDirection();
    }

    public Vector3d getHorizontalDirectionVector() {
        final Vector3d rotation = get(Keys.HEAD_ROTATION).orElse(this.rotation);
        // Invert the x direction because west and east are swapped
        return Quaternions.fromAxesAnglesDeg(rotation.mul(0, 1, 0)).getDirection().mul(-1, 1, 1);
    }

    /**
     * Gets the {@link Direction} that the entity is looking in the horizontal plane.
     *
     * @param division The division
     * @return The direction
     */
    public Direction getHorizontalDirection(Direction.Division division) {
        return Direction.getClosest(getHorizontalDirectionVector(), division);
    }

    @Nullable
    public EntityProtocolType<?> getEntityProtocolType() {
        return this.entityProtocolType;
    }

    public void setEntityProtocolType(@Nullable EntityProtocolType<?> entityProtocolType) {
        if (entityProtocolType != null) {
            checkArgument(entityProtocolType.getEntityType().isInstance(this),
                    "The protocol type %s is not applicable to this entity.");
        }
        this.entityProtocolType = entityProtocolType;
    }

    /**
     * Gets whether this {@link Entity} is dead, should
     * only be implemented by a {@link Living}.
     *
     * @return Is dead
     */
    boolean isDead() {
        return false;
    }

    /**
     * Marks this {@link Entity} as dead, should
     * only be implemented by a {@link Living}.
     */
    void setDead(boolean dead) {
    }

    void postDestructEvent(DestructEntityEvent event) {
        if (Sponge.getEventManager().post(event)) { // If it's cancelled, don't continue
            return;
        }
        if (!event.isMessageCancelled()) {
            // TODO
        }
    }

    @Override
    public boolean isRemoved() {
        return this.removeState != null;
    }

    @Override
    public void remove() {
        if (!isRemoved()) {
            remove(RemoveState.DESTROYED);
        }
    }

    @Nullable
    public RemoveState getRemoveState() {
        return this.removeState;
    }

    public void remove(RemoveState removeState) {
        checkNotNull(removeState, "removeState");
        if (this.removeState == removeState) {
            return;
        }
        this.removeState = removeState;
        if (removeState == RemoveState.DESTROYED) {
            setVehicle(null);
            clearPassengers();

            // Call the normal destroy entity event,
            // don't do it if the entity is dead.
            if (!isDead()) {
                setDead(true);

                final CauseStack causeStack = CauseStack.current();
                // TODO: Message channel?
                final DestructEntityEvent event = SpongeEventFactory.createDestructEntityEvent(causeStack.getCurrentCause(),
                        MessageChannel.toNone(), Optional.empty(), new MessageEvent.MessageFormatter(), this, false);
                postDestructEvent(event);
            }
        }
    }

    public void resurrect() {
        checkArgument(this.removeState != RemoveState.DESTROYED, "A destroyed entity cannot be resurrected/respawned.");
        this.removeState = null;
    }

    @Nullable
    public Vector3i getLastChunkSectionCoords() {
        return this.lastChunkCoords;
    }

    public void setLastChunkCoords(@Nullable Vector3i coords) {
        this.lastChunkCoords = coords;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    /**
     * Sets the on ground state of this entity.
     *
     * @param onGround The on ground state
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public EntityEffectCollection getEffectCollection() {
        return this.effectCollection;
    }

    public void setEffectCollection(EntityEffectCollection effectCollection) {
        this.effectCollection = effectCollection;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public void setBoundingBoxBase(@Nullable AABB boundingBox) {
        this.boundingBoxBase = boundingBox;
        this.boundingBox = null;
    }

    @Override
    public Optional<AABB> getBoundingBox() {
        AABB boundingBox = this.boundingBox;
        if (boundingBox == null && this.boundingBoxBase != null) {
            boundingBox = this.boundingBoxBase.offset(this.position);
            this.boundingBox = boundingBox;
        }
        return Optional.ofNullable(boundingBox);
    }

    @Override
    public boolean validateRawData(DataView dataView) {
        return dataView.contains(DataQueries.POSITION, DataQueries.ROTATION);
    }

    @Override
    public void setRawData(DataView dataView) throws InvalidDataException {
        checkNotNull(dataView, "dataView");
        setPosition(dataView.getObject(DataQueries.POSITION, Vector3d.class).get());
        setRotation(dataView.getObject(DataQueries.ROTATION, Vector3d.class).get());
        DataHelper.deserializeRawData(dataView, this);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer dataContainer = DataContainer.createNew()
                .set(DataQueries.ENTITY_TYPE, getType())
                .set(DataQueries.POSITION, getPosition())
                .set(DataQueries.ROTATION, getRotation());
        DataHelper.serializeRawData(dataContainer, this);
        return dataContainer;
    }

    @Override
    public EntityType<?> getType() {
        EntityType<?> entityType = this.entityType;
        if (entityType == null) {
            entityType = this.entityType = EntityTypeRegistryModule.INSTANCE.getByClass(getClass()).get();
        }
        return entityType;
    }

    @Override
    public LanternWorld getWorld() {
        return this.world;
    }

    protected void setWorld(@Nullable LanternWorld world) {
        this.world = world;
    }

    protected void setRawPosition(Vector3d position) {
        this.position = checkNotNull(position, "position");
        this.boundingBox = null;
    }

    protected void setRawRotation(Vector3d rotation) {
        this.rotation = checkNotNull(rotation, "rotation");
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public void setPosition(Vector3d position) {
        setRawPosition(position);
    }

    public boolean setPositionAndWorld(World world, Vector3d position) {
        setRawPosition(position);
        setWorld((LanternWorld) world);
        // TODO: Events
        return true;
    }

    @Override
    public Location getLocation() {
        checkState(this.world != null, "This entity doesn't have a world.");
        return new LanternLocation(this.world, this.position);
    }

    @Override
    public boolean setLocation(Location location) {
        checkNotNull(location, "location");
        return setPositionAndWorld(location.getWorld(), location.getPosition());
    }

    @Override
    public Vector3d getScale() {
        return this.scale;
    }

    @Override
    public void setScale(Vector3d scale) {
        this.scale = checkNotNull(scale, "scale");
    }

    @Override
    public Vector3d getRotation() {
        return this.rotation;
    }

    @Override
    public void setRotation(Vector3d rotation) {
        setRawRotation(rotation);
    }

    @Override
    public boolean transferToWorld(World world, Vector3d position) {
        return setPositionAndWorld(checkNotNull(world, "world"), position);
    }

    @Override
    public Transform getTransform() {
        return new LanternTransform(this.position, this.rotation);
    }

    @Override
    public boolean setTransform(Transform transform) {
        checkNotNull(transform, "transform");
        setPosition(transform.getPosition());
        setRotation(transform.getRotation());
        setScale(transform.getScale());
        // TODO: Events
        return true;
    }

    @Override
    public boolean setLocationAndRotation(Location location, Vector3d rotation) {
        checkNotNull(location, "location");
        checkNotNull(rotation, "rotation");

        setWorld((LanternWorld) location.getWorld());
        setRawPosition(location.getPosition());
        setRawRotation(rotation);
        // TODO: Events
        return true;
    }

    @Override
    public boolean setLocationAndRotation(Location location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        checkNotNull(location, "location");
        checkNotNull(rotation, "rotation");
        checkNotNull(relativePositions, "relativePositions");

        final World world = location.getWorld();
        final Vector3d pos = location.getPosition();

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double pitch = rotation.getX();
        double yaw = rotation.getY();
        double roll = rotation.getZ();

        if (relativePositions.contains(RelativePositions.X)) {
            x += this.position.getX();
        }
        if (relativePositions.contains(RelativePositions.Y)) {
            y += this.position.getY();
        }
        if (relativePositions.contains(RelativePositions.Z)) {
            z += this.position.getZ();
        }
        if (relativePositions.contains(RelativePositions.PITCH)) {
            pitch += this.rotation.getX();
        }
        if (relativePositions.contains(RelativePositions.YAW)) {
            yaw += this.rotation.getY();
        }
        // TODO: No relative roll?

        setWorld((LanternWorld) world);
        setRawPosition(new Vector3d(x, y, z));
        setRawRotation(new Vector3d(pitch, yaw, roll));

        // TODO: Events
        return true;
    }

    public List<Entity> getPassengers() {
        synchronized (this.passengers) {
            return ImmutableList.copyOf(this.passengers);
        }
    }

    public boolean hasPassenger(Entity entity) {
        checkNotNull(entity, "entity");
        synchronized (this.passengers) {
            //noinspection SuspiciousMethodCalls
            return this.passengers.contains(entity);
        }
    }

    public boolean addPassenger(Entity entity) {
        checkNotNull(entity, "entity");
        final LanternEntity entity1 = (LanternEntity) entity;
        return entity1.getVehicle0() == null && entity1.setVehicle(this);
    }

    public void removePassenger(Entity entity) {
        checkNotNull(entity, "entity");
        final LanternEntity entity1 = (LanternEntity) entity;
        if (entity1.getVehicle0() != this) {
            return;
        }
        entity1.setVehicle(null);
    }

    public void clearPassengers() {
        synchronized (this.passengers) {
            for (LanternEntity passenger : new ArrayList<>(this.passengers)) {
                passenger.setVehicle(null);
            }
        }
    }

    public Optional<Entity> getVehicle() {
        synchronized (this.passengers) {
            return Optional.ofNullable(this.vehicle);
        }
    }

    public boolean setVehicle(@Nullable Entity entity) {
        synchronized (this.passengers) {
            if (this.vehicle == entity) {
                return false;
            }
            if (this.vehicle != null) {
                this.vehicle.removePassenger0(this);
            }
            this.vehicle = (LanternEntity) entity;
            if (this.vehicle != null) {
                this.vehicle.addPassenger0(this);
            }
            return true;
        }
    }

    private void removePassenger0(LanternEntity passenger) {
        synchronized (this.passengers) {
            this.passengers.remove(passenger);
        }
    }

    private void addPassenger0(LanternEntity passenger) {
        synchronized (this.passengers) {
            int index = -1;
            if (passenger instanceof LanternPlayer) {
                do {
                    index++;
                } while (index < this.passengers.size() && this.passengers.get(index) instanceof LanternPlayer);
            }
            if (index == -1) {
                this.passengers.add(passenger);
            } else {
                this.passengers.add(index, passenger);
            }
        }
    }

    @Nullable
    public LanternEntity getBaseVehicle() {
        synchronized (this.passengers) {
            LanternEntity lastEntity = this;
            while (true) {
                final LanternEntity entity = lastEntity.getVehicle0();
                if (entity == null) {
                    return lastEntity;
                }
                lastEntity = entity;
            }
        }
    }

    @Nullable
    private LanternEntity getVehicle0() {
        synchronized (this.passengers) {
            return this.vehicle;
        }
    }

    @Override
    public boolean isLoaded() {
        return this.removeState != RemoveState.CHUNK_UNLOAD;
    }

    public final void pulse() {
        final long time = LanternGame.currentTimeTicks();
        final long deltaTicks = this.lastPulseTime == -1 ? 1 : time - this.lastPulseTime;
        if (deltaTicks > 0) {
            pulse((int) deltaTicks);
            this.lastPulseTime = time;
        }
    }

    /**
     * Pulses the entity.
     *
     * @param deltaTicks The amount of ticks that passed since the last pulse
     */
    protected void pulse(int deltaTicks) {
        synchronized (this.passengers) {
            if (this.vehicle != null) {
                this.position = this.vehicle.getPosition();
            }
        }
        // Deal some void damage
        if (getPosition().getY() < -64.0) {
            this.voidDamageCounter += deltaTicks;
            while (this.voidDamageCounter >= 10) {
                damage(4.0, DamageSources.VOID);
                this.voidDamageCounter -= 10;
            }
        } else {
            this.voidDamageCounter = 0;
        }
    }

    @Override
    public EntitySnapshot createSnapshot() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public boolean damage(double damage, DamageSource damageSource) {
        checkNotNull(damageSource, "damageSource");
        final Optional<Double> optHealth = get(Keys.HEALTH);
        if (!optHealth.isPresent()) {
            // A special case, make void damage always pass through for
            // entities without health, instantly destroying them.
            if (damageSource.getType() == DamageTypes.VOID) {
                remove(RemoveState.DESTROYED);
                return true;
            }
            return false;
        }
        // Always throw the event. Plugins may want to override
        // default checking behavior.
        boolean cancelled = false;
        // Check if the damage affects creative mode, and check
        // if the player is in creative mode
        if (!damageSource.doesAffectCreative() &&
                get(Keys.GAME_MODE).orElse(null) == GameModes.CREATIVE) {
            cancelled = true;
        }
        final List<Tuple<DamageFunction, Consumer<DamageEntityEvent>>> damageFunctions = new ArrayList<>();
        // Only collect damage modifiers if the event isn't cancelled
        if (!cancelled) {
            collectDamageFunctions(damageFunctions);
        }
        double health = optHealth.get();
        // TODO: Damage modifiers, etc.
        final CauseStack causeStack = CauseStack.current();
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(damageSource);
            frame.addContext(EventContextKeys.DAMAGE_TYPE, damageSource.getType());
            final DamageEntityEvent event = SpongeEventFactory.createDamageEntityEvent(frame.getCurrentCause(),
                    damageFunctions.stream().map(Tuple::getFirst).collect(Collectors.toList()), this, damage);
            event.setCancelled(cancelled);
            Sponge.getEventManager().post(event);
            if (event.isCancelled()) {
                return false;
            }
            damageFunctions.forEach(tuple -> tuple.getSecond().accept(event));
            damage = event.getFinalDamage();
            if (damage > 0) {
                health = Math.max(optHealth.get() - damage, 0);
                offer(Keys.HEALTH, health);
            }
            final double exhaustion = damageSource.getExhaustion();
            getValue(Keys.EXHAUSTION).ifPresent(value -> offer(Keys.EXHAUSTION, Math.min(value.getMaxValue(), value.get() + exhaustion)));
            // Add some values to the context that may be useful in the handleDamage method,
            // for example the base damage value is used by the falling sound effect
            frame.addContext(LanternEventContextKeys.BASE_DAMAGE_VALUE, damage);
            frame.addContext(LanternEventContextKeys.ORIGINAL_DAMAGE_VALUE, event.getOriginalDamage());
            frame.addContext(LanternEventContextKeys.FINAL_DAMAGE_VALUE, event.getFinalDamage());
            handleDamage(causeStack, damageSource, damage, health);
        }
        return true;
    }

    protected void handleDamage(CauseStack causeStack, DamageSource damageSource, double damage, double newHealth) {
    }

    protected void collectDamageFunctions(List<Tuple<DamageFunction, Consumer<DamageEntityEvent>>> damageFunctions) {
        // Absorption health modifier
        get(Keys.ABSORPTION).filter(value -> value > 0).ifPresent(value -> {
            final DoubleUnaryOperator function = d -> -(Math.max(d - Math.max(d - value, 0), 0));
            final DamageModifier modifier = DamageModifier.builder()
                    .cause(Cause.of(EventContext.empty(), this))
                    .type(DamageModifierTypes.ABSORPTION)
                    .build();
            damageFunctions.add(new Tuple<>(new DamageFunction(modifier, function), event -> {
                final double mod = event.getDamage(modifier);
                offer(Keys.ABSORPTION, Math.max(get(Keys.ABSORPTION).get() + mod, 0));
            }));
        });
    }

    /**
     * Heals the entity for the specified amount.
     *
     * <p>Will not heal them if they are dead and will not set
     * them above their maximum health.</p>
     *
     * @param amount The amount to heal for
     * @param source The healing source
     */
    public boolean heal(double amount, HealingSource source) {
        if (isDead()) {
            return false;
        }
        final BoundedValue<Double> health = getValue(Keys.HEALTH).orElse(null);
        if (health == null || health.get() >= health.getMaxValue()) {
            return false;
        }
        final CauseStack causeStack = CauseStack.current();
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(source);
            frame.addContext(LanternEventContextKeys.HEALING_TYPE, source.getHealingType());

            final HealEntityEvent event = SpongeEventFactory.createHealEntityEvent(
                    frame.getCurrentCause(), this, new ArrayList<>(), amount);
            Sponge.getEventManager().post(event);
            if (event.isCancelled()) {
                return false;
            }
            amount = event.getFinalHealAmount();
            if (amount > 0) {
                offer(Keys.HEALTH, Math.min(health.get() + amount, health.getMaxValue()));
            }
        }
        return true;
    }

    @Override
    public Translation getTranslation() {
        final Optional<Text> displayName = this.get(Keys.DISPLAY_NAME);
        if (displayName.isPresent()) {
            return new FixedTranslation(LanternTexts.toLegacy(displayName.get()));
        }
        return this.entityType.getTranslation();
    }

    @Override
    public EntityArchetype copy() {
        return null;
    }

    @Override
    public EntityArchetype createArchetype() {
        return null;
    }

    /**
     * Triggers the {@link EntityEvent} for this entity.
     *
     * @param event The event
     */
    public void triggerEvent(EntityEvent event) {
        getWorld().getEntityProtocolManager().triggerEvent(this, event);
    }

    /**
     * Gets the {@link SoundCategory} of this entity.
     *
     * @return The sound category
     */
    public SoundCategory getSoundCategory() {
        return this.soundCategory;
    }

    /**
     * Sets the {@link SoundCategory} of this entity.
     *
     * @param soundCategory  The sound category
     */
    public void setSoundCategory(SoundCategory soundCategory) {
        this.soundCategory = soundCategory;
    }

    /**
     * Plays a sound which is caused by this {@link LanternEntity}.
     *
     * @param soundType The sound type
     * @param volume The volume
     * @param pitch The pitch value
     */
    public void playSound(SoundType soundType, double volume, double pitch) {
        playSound(soundType, Vector3d.ZERO, volume, pitch);
    }

    /**
     * Plays a sound which is caused by this {@link LanternEntity}.
     *
     * @param soundType The sound type
     * @param relativeSoundPosition The relative position to the entity to play the sound at
     * @param volume The volume
     * @param pitch The pitch value
     */
    public void playSound(SoundType soundType, Vector3d relativeSoundPosition, double volume, double pitch) {
        // Silent entities don't make any sounds
        if (get(Keys.IS_SILENT).orElse(false)) {
            return;
        }
        this.world.playSound(soundType, this.soundCategory, getPosition().add(relativeSoundPosition), volume, pitch);
    }

    /**
     * Plays a sound which is caused by this {@link LanternEntity}. Playing a sound
     * oriented around the entity will rotate the relative position based on the orientation
     * of the entity (the y axis in most cases).
     *
     * @param soundType The sound type
     * @param relativeSoundPosition The relative position to the entity to play the sound at
     * @param volume The volume
     * @param pitch The pitch value
     */
    public void playOrientedSound(SoundType soundType, Vector3d relativeSoundPosition, double volume, double pitch) {
        playSound(soundType, orientRelativePosition(relativeSoundPosition), volume, pitch);
    }

    /**
     * Orients the relative position around the entity based on it's orientation. The
     * relative position should always assume that the entity has no rotation, since
     * it will be rotated by this method.
     *
     * @param relativePosition The relative position
     * @return The oriented relative position
     */
    protected Vector3d orientRelativePosition(Vector3d relativePosition) {
        final Quaterniond rot = Quaterniond.fromAxesAnglesDeg(0, getRotation().getY(), 0);
        return rot.rotate(relativePosition);
    }
}
