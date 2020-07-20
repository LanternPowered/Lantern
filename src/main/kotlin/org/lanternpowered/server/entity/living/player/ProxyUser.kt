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
package org.lanternpowered.server.entity.living.player

import com.google.common.base.Objects
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.server.data.MutableForwardingDataHolder
import org.lanternpowered.server.data.io.UserIO
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.permission.AbstractProxySubject
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.entity.UserInventory
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.util.Tristate
import org.spongepowered.math.vector.Vector3d
import java.io.IOException
import java.util.Optional
import java.util.UUID

internal class ProxyUser(private var gameProfile: GameProfile) : AbstractProxySubject(), MutableForwardingDataHolder, IUser {

    private val uniqueId: UUID = gameProfile.uniqueId
    private var user: AbstractUser? = null

    init {
        resolveSubject()
    }

    /**
     * Sets the internal [AbstractUser] of this proxy user.
     *
     * @param user The user
     */
    private fun setInternalUser0(user: AbstractUser?) {
        if (this.user != null) {
            try {
                UserIO.save(Lantern.getGame().savesDirectory, this.user)
            } catch (e: IOException) {
                Lantern.getLogger().warn("An error occurred while saving the player data for {}", this.gameProfile, e)
            }
        }
        this.user = user
        if (user != null) {
            val oldProfile = this.gameProfile
            // Update the game profile, in case anything changed
            this.gameProfile = user.profile
            check(this.uniqueId == this.gameProfile.uniqueId)
            // Reinitialize the subject
            if (!Objects.equal(oldProfile.name.orElse(null), this.gameProfile.name.orElse(null))) {
                resolveSubject()
            }
            try {
                UserIO.load(Lantern.getGame().savesDirectory, this.user)
            } catch (e: IOException) {
                Lantern.getLogger().warn("An error occurred while loading the player data for {}", this.gameProfile, e)
            }
        }
    }

    var internalUser: AbstractUser?
        get() = this.user
        set(value) = setInternalUser0(value)

    private fun resolveUser(): IUser {
        val user = this.user
        if (user != null)
            return user
        val offlineUser = OfflineUser(this)
        setInternalUser0(offlineUser)
        return offlineUser
    }

    override val delegateDataHolder: DataHolder.Mutable
        get() = resolveUser()

    override val subjectCollectionIdentifier: String
        get() = PermissionService.SUBJECTS_USER

    override fun getUniqueId(): UUID = this.uniqueId
    override fun getProfile(): GameProfile = this.gameProfile
    override fun getPermissionDefault(permission: String): Tristate = Tristate.FALSE
    override fun getInventory(): UserInventory = resolveUser().inventory
    override fun canEquip(type: EquipmentType): Boolean = resolveUser().canEquip(type)
    override fun canEquip(type: EquipmentType, equipment: ItemStack): Boolean = resolveUser().canEquip(type, equipment)
    override fun getEquipped(type: EquipmentType): Optional<ItemStack> = resolveUser().getEquipped(type)
    override fun equip(type: EquipmentType, equipment: ItemStack): Boolean = resolveUser().equip(type, equipment)
    override fun isOnline(): Boolean = resolveUser().isOnline
    override fun getPlayer(): Optional<Player> = (resolveUser() as? Player).optional()
    override fun getPosition(): Vector3d = resolveUser().position
    override fun getWorldUniqueId(): Optional<UUID> = resolveUser().worldUniqueId
    override fun setLocation(position: Vector3d, world: UUID): Boolean = resolveUser().setLocation(position, world)
    override fun setRotation(rotation: Vector3d) { resolveUser().rotation = rotation }
    override fun getRotation(): Vector3d = resolveUser().rotation
    override fun getEnderChestInventory(): Inventory = resolveUser().enderChestInventory
    override fun getHelmet(): ItemStack = resolveUser().helmet
    override fun setHelmet(helmet: ItemStack) { resolveUser().helmet = helmet }
    override fun getChestplate(): ItemStack = resolveUser().chestplate
    override fun setChestplate(chestplate: ItemStack) { resolveUser().chestplate = chestplate }
    override fun getLeggings(): ItemStack = resolveUser().leggings
    override fun setLeggings(leggings: ItemStack) { resolveUser().leggings = leggings }
    override fun getBoots(): ItemStack = resolveUser().boots
    override fun setBoots(boots: ItemStack) { resolveUser().boots = boots }
    override fun getItemInHand(handType: HandType): ItemStack = resolveUser().getItemInHand(handType)
    override fun setItemInHand(hand: HandType, itemInHand: ItemStack) { resolveUser().setItemInHand(hand, itemInHand) }
}
