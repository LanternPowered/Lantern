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
package org.lanternpowered.server.entity.living.player;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.data.io.store.entity.UserStore;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.AbstractArmorEquipable;
import org.lanternpowered.server.entity.LanternLiving;
import org.lanternpowered.server.entity.Pose;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.entity.living.player.tab.GlobalTabList;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.vanilla.AbstractUserInventory;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.lanternpowered.server.item.recipe.RecipeBookState;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.statistic.StatisticMap;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractUser extends LanternLiving implements IUser, AbstractArmorEquipable {

    public static final double DEFAULT_EXHAUSTION = 0.0;
    public static final double DEFAULT_SATURATION = 5.0;

    // The proxy user
    private final ProxyUser user;

    // The statistics of this player
    private final StatisticMap statisticMap = new StatisticMap();

    // The ender chest inventory of this player.
    private final ChestInventory enderChestInventory;

    /**
     * This field is for internal use only, it is used while finding a proper
     * world to spawn the player in. Used at {@link NetworkSession#initPlayer()} and
     * {@link UserStore}. Will also be used by {@link OfflineUser}s.
     */
    @Nullable private LanternWorldProperties userWorld;

    AbstractUser(ProxyUser user) {
        super(user.getUniqueId());
        this.user = user;
        offer(Keys.DISPLAY_NAME, Text.of(this.user.getName()));
        this.enderChestInventory = VanillaInventoryArchetypes.ENDER_CHEST.builder()
                .withCarrier(this).build(Lantern.getMinecraftPlugin());
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        final LocalKeyRegistry<AbstractUser> c = getKeyRegistry().forHolder(AbstractUser.class);
        c.register(LanternKeys.ACCESSORIES, new ArrayList<>());
        c.register(LanternKeys.MAX_EXHAUSTION).minimum(0.0).maximum(Double.MAX_VALUE);
        c.register(Keys.EXHAUSTION, DEFAULT_EXHAUSTION).minimum(0.0).maximum(LanternKeys.MAX_EXHAUSTION);
        c.register(LanternKeys.MAX_FOOD_LEVEL, 20).minimum(0).maximum(Integer.MAX_VALUE);
        c.register(Keys.FOOD_LEVEL, 20).minimum(0).maximum(LanternKeys.MAX_FOOD_LEVEL);
        c.register(Keys.SATURATION, DEFAULT_SATURATION).minimum(0.0)
                .maximum(user -> user.get(Keys.FOOD_LEVEL).orElse(20).doubleValue());
        c.register(Keys.LAST_DATE_PLAYED);
        c.register(Keys.FIRST_DATE_PLAYED);
        c.register(Keys.WALKING_SPEED, 0.1);
        c.register(LanternKeys.FIELD_OF_VIEW_MODIFIER, 1.0);
        c.register(Keys.IS_FLYING, false);
        c.register(Keys.IS_SPRINTING, false);
        c.register(Keys.FLYING_SPEED, 0.1);
        c.register(Keys.CAN_FLY, false);
        c.register(Keys.RESPAWN_LOCATIONS, new HashMap<>());
        c.register(Keys.GAME_MODE, GameModes.NOT_SET).addChangeListener(
                (user, oldElement, newElement) -> {
                    ((LanternGameMode) newElement).getAbilityApplier().invoke(this);
                    // This MUST be updated, unless you want strange behavior on the client,
                    // the client has 3 different concepts of 'isCreative', and each combination
                    // gives a different outcome...
                    // For example:
                    // - Disable noClip and glow in spectator, but you can place blocks
                    // - NoClip in creative, but you cannot change your hotbar, or drop items
                    // Not really worth the trouble right now
                    // TODO: Differentiate the 'global tab list entry' and the entry to update
                    // TODO: these kind of settings to avoid possible 'strange' behavior.
                    GlobalTabList.getInstance().get(getProfile()).ifPresent(e -> e.setGameMode(newElement));
                });
        c.register(Keys.DOMINANT_HAND, HandPreferences.RIGHT);
        c.register(Keys.IS_ELYTRA_FLYING, false);
        c.register(LanternKeys.ELYTRA_GLIDE_SPEED, 0.1);
        c.register(LanternKeys.ELYTRA_SPEED_BOOST, false);
        c.register(LanternKeys.SUPER_STEVE, false);
        c.register(LanternKeys.CAN_WALL_JUMP, false);
        c.register(LanternKeys.CAN_DUAL_WIELD, false);
        c.register(LanternKeys.SCORE, 0);
        c.register(LanternKeys.ACTIVE_HAND);
        c.register(LanternKeys.SMELTING_RECIPE_BOOK_STATE, RecipeBookState.DEFAULT);
        c.register(LanternKeys.CRAFTING_RECIPE_BOOK_STATE, RecipeBookState.DEFAULT);
        c.register(LanternKeys.OPEN_ADVANCEMENT_TREE);
        c.register(LanternKeys.DISPLAYED_SKIN_PARTS, new HashSet<>());
        c.register(LanternKeys.POSE, Pose.STANDING);
        c.registerProvider(Keys.STATISTICS, (builder, key) -> {
            builder.offerFast((user, map) -> {
                user.statisticMap.setStatisticValues(map);
                return true;
            });
            builder.get(user -> this.statisticMap.getStatisticValues());
        });
    }

    @Nullable
    public LanternWorldProperties getUserWorld() {
        return this.userWorld;
    }

    public void setUserWorld(@Nullable LanternWorldProperties userWorld) {
        this.userWorld = userWorld;
    }

    /**
     * Sets the {@link LanternWorld} without triggering
     * any changes for this player.
     *
     * @param world The world
     */
    public void setRawWorld(@Nullable LanternWorld world) {
        super.setWorld(world);
    }

    /**
     * Gets the {@link ChestInventory}.
     *
     * @return The ender chest inventory
     */
    public ChestInventory getEnderChestInventory() {
        return this.enderChestInventory;
    }

    /**
     * Gets the {@link StatisticMap}.
     *
     * @return The statistic map
     */
    public StatisticMap getStatisticMap() {
        return this.statisticMap;
    }

    /**
     * Gets the internal {@link ProxyUser} instance of this {@link AbstractUser}.
     *
     * @return The proxy user
     */
    public ProxyUser getProxyUser() {
        return this.user;
    }

    @Override
    public Optional<UUID> getWorldUniqueId() {
        @Nullable final World<?> world = getWorld();
        if (world != null)
            return Optional.of(world.getProperties().getUniqueId());
        if (this.userWorld != null)
            return Optional.of(this.userWorld.getUniqueId());
        return Optional.empty();
    }

    @Override
    public boolean setLocation(Vector3d position, UUID worldUniqueId) {
        checkNotNull(position, "position");
        checkNotNull(worldUniqueId, "worldUniqueId");
        final WorldProperties world = Lantern.getServer().getWorldManager().getWorldProperties(worldUniqueId)
                .orElseThrow(() -> new IllegalStateException("Cannot find World with the given UUID: " + worldUniqueId));
        this.userWorld = (LanternWorldProperties) world;
        setRawPosition(position);
        return true;
    }

    @Override
    public abstract AbstractUserInventory<? extends User> getInventory();

    // User methods

    @Override
    public GameProfile getProfile() {
        return this.user.getProfile();
    }

    @Override
    public Set<Context> getActiveContexts() {
        return this.user.getActiveContexts();
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return this.user.getContainingCollection();
    }

    @Override
    public SubjectReference asSubjectReference() {
        return this.user.asSubjectReference();
    }

    @Override
    public boolean isSubjectDataPersisted() {
        return this.user.isSubjectDataPersisted();
    }

    @Override
    public SubjectData getSubjectData() {
        return this.user.getSubjectData();
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return this.user.getTransientSubjectData();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return this.user.getPermissionValue(contexts, permission);
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
        return this.user.isChildOf(contexts, parent);
    }

    @Override
    public List<SubjectReference> getParents(Set<Context> contexts) {
        return this.user.getParents(contexts);
    }

    @Override
    public Optional<String> getOption(Set<Context> contexts, String key) {
        return this.user.getOption(contexts, key);
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return this.user.hasPermission(contexts, permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.user.hasPermission(permission);
    }

    @Override
    public boolean isChildOf(SubjectReference parent) {
        return this.user.isChildOf(parent);
    }

    @Override
    public List<SubjectReference> getParents() {
        return this.user.getParents();
    }

    @Override
    public Optional<String> getFriendlyIdentifier() {
        return this.user.getFriendlyIdentifier();
    }
}
