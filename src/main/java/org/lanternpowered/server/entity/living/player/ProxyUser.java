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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;
import org.lanternpowered.server.data.MutableForwardingDataHolder;
import org.lanternpowered.server.data.io.UserIO;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.permission.AbstractProxySubject;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.math.vector.Vector3d;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ProxyUser extends AbstractProxySubject implements IUser, MutableForwardingDataHolder {

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
    public Mutable getDelegateDataHolder() {
        return resolveUser();
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
