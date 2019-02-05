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

import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Objects;
import org.lanternpowered.server.data.io.UserIO;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.permission.AbstractProxySubject;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.util.Tristate;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

public class ProxyUser extends AbstractProxySubject implements IUser {

    private final UUID uniqueId;
    private GameProfile gameProfile;
    @Nullable private AbstractUser user;

    public ProxyUser(GameProfile gameProfile) {
        this.uniqueId = gameProfile.getUniqueId();
        this.gameProfile = gameProfile;
        initializeSubject();
    }

    /**
     * Sets the internal {@link AbstractUser} of this proxy user.
     *
     * @param user The user
     */
    public void setInternalUser(@Nullable AbstractUser user) {
        if (this.user != null) {
            try {
                UserIO.save(Lantern.getGame().getSavesDirectory(), this.user);
            } catch (IOException e) {
                Lantern.getLogger().warn("An error occurred while saving the player data for {}", this.gameProfile, e);
            }
        }
        this.user = user;
        if (user != null) {
            final GameProfile oldProfile = this.gameProfile;
            // Update the game profile, in case anything changed
            this.gameProfile = user.getProfile();
            checkState(this.uniqueId.equals(this.gameProfile.getUniqueId()));
            // Reinitialize the subject
            if (!Objects.equal(oldProfile.getName().orElse(null), this.gameProfile.getName().orElse(null))) {
                initializeSubject();
            }
            try {
                UserIO.load(Lantern.getGame().getSavesDirectory(), this.user);
            } catch (IOException e) {
                Lantern.getLogger().warn("An error occurred while loading the player data for {}", this.gameProfile, e);
            }
        }
    }

    @Nullable
    public IUser getInternalUser() {
        return this.user;
    }

    private IUser resolveUser() {
        if (this.user != null) {
            return this.user;
        }
        final OfflineUser offlineUser = new OfflineUser(this);
        setInternalUser(offlineUser);
        return offlineUser;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public GameProfile getProfile() {
        return this.gameProfile;
    }

    @Override
    public String getSubjectCollectionIdentifier() {
        return PermissionService.SUBJECTS_USER;
    }

    @Override
    public Tristate getPermissionDefault(String permission) {
        return Tristate.FALSE;
    }

    @Override
    public CarriedInventory<? extends Carrier> getInventory() {
        return resolveUser().getInventory();
    }

    @Override
    public boolean canEquip(EquipmentType type) {
        return resolveUser().canEquip(type);
    }

    @Override
    public boolean canEquip(EquipmentType type, ItemStack equipment) {
        return resolveUser().canEquip(type, equipment);
    }

    @Override
    public Optional<ItemStack> getEquipped(EquipmentType type) {
        return resolveUser().getEquipped(type);
    }

    @Override
    public boolean equip(EquipmentType type, ItemStack equipment) {
        return resolveUser().equip(type, equipment);
    }

    @Override
    public boolean isOnline() {
        return resolveUser().isOnline();
    }

    @Override
    public Optional<Player> getPlayer() {
        final IUser user = resolveUser();
        if (user instanceof Player) {
            return Optional.of((Player) user);
        }
        return Optional.empty();
    }

    @Override
    public Vector3d getPosition() {
        return resolveUser().getPosition();
    }

    @Override
    public Optional<UUID> getWorldUniqueId() {
        return resolveUser().getWorldUniqueId();
    }

    @Override
    public boolean setLocation(Vector3d position, UUID world) {
        return resolveUser().setLocation(position, world);
    }

    @Override
    public void setRotation(Vector3d rotation) {
        resolveUser().setRotation(rotation);
    }

    @Override
    public Vector3d getRotation() {
        return resolveUser().getRotation();
    }

    @Override
    public Inventory getEnderChestInventory() {
        return resolveUser().getEnderChestInventory();
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        return resolveUser().getProperty(propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return resolveUser().getApplicableProperties();
    }

    @Override
    public int getContentVersion() {
        return resolveUser().getContentVersion();
    }

    @Override
    public DataContainer toContainer() {
        return resolveUser().toContainer();
    }

    @Override
    public boolean validateRawData(DataView container) {
        return resolveUser().validateRawData(container);
    }

    @Override
    public void setRawData(DataView container) throws InvalidDataException {
        resolveUser().setRawData(container);
    }

    @Override
    public <E> Optional<E> get(Key<? extends Value<E>> key) {
        return resolveUser().get(key);
    }

    @Override
    public <E, V extends Value<E>> Optional<V> getValue(Key<V> key) {
        return resolveUser().getValue(key);
    }

    @Override
    public boolean supports(Key<?> key) {
        return resolveUser().supports(key);
    }

    @Override
    public DataHolder copy() {
        return resolveUser().copy();
    }

    @Override
    public Set<Key<?>> getKeys() {
        return resolveUser().getKeys();
    }

    @Override
    public Set<Value.Immutable<?>> getValues() {
        return resolveUser().getValues();
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        return resolveUser().get(containerClass);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        return resolveUser().getOrCreate(containerClass);
    }

    @Override
    public boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
        return resolveUser().supports(holderClass);
    }

    @Override
    public <E> DataTransactionResult offer(Key<? extends Value<E>> key, E value) {
        return resolveUser().offer(key, value);
    }

    @Override
    public DataTransactionResult offer(DataManipulator<?, ?> valueContainer, MergeFunction function) {
        return resolveUser().offer(valueContainer, function);
    }

    @Override
    public DataTransactionResult remove(Class<? extends DataManipulator<?, ?>> containerClass) {
        return resolveUser().remove(containerClass);
    }

    @Override
    public DataTransactionResult remove(Key<?> key) {
        return resolveUser().remove(key);
    }

    @Override
    public DataTransactionResult undo(DataTransactionResult result) {
        return resolveUser().undo(result);
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        return resolveUser().copyFrom(that, function);
    }

    @Override
    public Collection<DataManipulator<?, ?>> getContainers() {
        return resolveUser().getContainers();
    }

    @Override
    public ItemStack getHelmet() {
        return resolveUser().getHelmet();
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        resolveUser().setHelmet(helmet);
    }

    @Override
    public ItemStack getChestplate() {
        return resolveUser().getChestplate();
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        resolveUser().setChestplate(chestplate);
    }

    @Override
    public ItemStack getLeggings() {
        return resolveUser().getLeggings();
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        resolveUser().setLeggings(leggings);
    }

    @Override
    public ItemStack getBoots() {
        return resolveUser().getBoots();
    }

    @Override
    public void setBoots(ItemStack boots) {
        resolveUser().setBoots(boots);
    }

    @Override
    public ItemStack getItemInHand(HandType handType) {
        return resolveUser().getItemInHand(handType);
    }

    @Override
    public void setItemInHand(HandType hand, ItemStack itemInHand) {
        resolveUser().setItemInHand(hand, itemInHand);
    }
}
