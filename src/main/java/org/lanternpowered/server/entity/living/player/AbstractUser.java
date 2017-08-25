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
package org.lanternpowered.server.entity.living.player;

import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.io.store.entity.UserStore;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternHumanoid;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.entity.living.player.tab.GlobalTabList;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.vanilla.AbstractUserInventory;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.statistic.StatisticMap;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public abstract class AbstractUser extends LanternHumanoid implements IUser {

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
     * {@link UserStore}.
     */
    @Nullable private LanternWorldProperties tempWorld;

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
        final ValueCollection c = getValueCollection();
        c.register(LanternKeys.ACCESSORIES, new ArrayList<>());
        c.register(LanternKeys.MAX_EXHAUSTION, 40.0, 0.0, Double.MAX_VALUE);
        c.register(Keys.EXHAUSTION, DEFAULT_EXHAUSTION, 0.0, LanternKeys.MAX_EXHAUSTION);
        c.register(LanternKeys.MAX_FOOD_LEVEL, 20, 0, Integer.MAX_VALUE);
        c.register(Keys.FOOD_LEVEL, 20, 0, LanternKeys.MAX_FOOD_LEVEL);
        c.registerWithSuppliedMax(Keys.SATURATION, DEFAULT_SATURATION, 0.0,
                container -> container.get(Keys.FOOD_LEVEL).orElse(20).doubleValue());
        c.register(Keys.LAST_DATE_PLAYED, null);
        c.register(Keys.FIRST_DATE_PLAYED, null);
        c.registerNonRemovable(Keys.WALKING_SPEED, 0.1);
        c.registerNonRemovable(LanternKeys.FIELD_OF_VIEW_MODIFIER, 1.0);
        c.registerNonRemovable(Keys.IS_FLYING, false);
        c.registerNonRemovable(Keys.IS_SNEAKING, false);
        c.registerNonRemovable(Keys.IS_SPRINTING, false);
        c.registerNonRemovable(Keys.FLYING_SPEED, 0.1);
        c.registerNonRemovable(Keys.CAN_FLY, false);
        c.registerNonRemovable(Keys.RESPAWN_LOCATIONS, new HashMap<>());
        c.registerNonRemovable(Keys.GAME_MODE, GameModes.NOT_SET).addListener(
                (oldElement, newElement) -> {
                    ((LanternGameMode) newElement).getAbilityApplier().accept(this);
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
        c.registerNonRemovable(Keys.DOMINANT_HAND, HandPreferences.RIGHT);
        c.registerNonRemovable(LanternKeys.IS_ELYTRA_FLYING, false);
        c.registerNonRemovable(LanternKeys.ELYTRA_GLIDE_SPEED, 0.1);
        c.registerNonRemovable(LanternKeys.ELYTRA_SPEED_BOOST, false);
        c.registerNonRemovable(LanternKeys.SUPER_STEVE, false);
        c.registerNonRemovable(LanternKeys.CAN_WALL_JUMP, false);
        c.registerNonRemovable(LanternKeys.CAN_DUAL_WIELD, false);
        c.registerNonRemovable(LanternKeys.SCORE, 0);
        c.registerNonRemovable(LanternKeys.ACTIVE_HAND, Optional.empty());
        c.registerNonRemovable(LanternKeys.RECIPE_BOOK_FILTER_ACTIVE, false);
        c.registerNonRemovable(LanternKeys.RECIPE_BOOK_GUI_OPEN, false);
        c.registerProcessor(Keys.STATISTICS).add(builder -> builder
                .offerHandler((key, valueContainer, map) -> {
                    this.statisticMap.setStatisticValues(map);
                    return DataTransactionResult.successNoData();
                })
                .retrieveHandler((key, valueContainer) -> Optional.of(this.statisticMap.getStatisticValues()))
                .failAlwaysRemoveHandler());
        c.registerNonRemovable(LanternKeys.OPEN_ADVANCEMENT_TREE, Optional.empty());
    }

    @Nullable
    public LanternWorldProperties getTempWorld() {
        return this.tempWorld;
    }

    public void setTempWorld(@Nullable LanternWorldProperties tempTargetWorld) {
        this.tempWorld = tempTargetWorld;
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
