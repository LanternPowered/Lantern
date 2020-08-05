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
package org.lanternpowered.server.user

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.world.Location
import org.lanternpowered.server.data.MutableForwardingDataHolder
import org.lanternpowered.server.data.io.UserIO
import org.lanternpowered.server.entity.player.AbstractPlayer
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.entity.player.OfflinePlayer
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.inventory.user.LanternUserInventory
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.entity.UserInventory
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectProxy
import org.spongepowered.math.vector.Vector3d
import java.io.IOException
import java.util.Optional
import java.util.UUID

class LanternUser(private var profile: GameProfile) : SubjectProxy, MutableForwardingDataHolder, User {

    private val uniqueId: UUID = this.profile.uniqueId

    private var player: AbstractPlayer? = null
    private var inventory: UserInventory? = null

    override fun getUniqueId(): UUID = this.uniqueId
    override fun getProfile(): GameProfile = this.profile
    override fun getIdentifier(): String = this.uniqueId.toString()
    override fun getName(): String = this.profile.name.get()

    /**
     * Sets the internal [AbstractPlayer] of this user.
     *
     * @param player The internal player
     */
    private fun setInternalPlayer0(player: AbstractPlayer?) {
        if (this.player != null) {
            try {
                UserIO.save(Lantern.getGame().savesDirectory, this.player)
            } catch (e: IOException) {
                Lantern.getLogger().warn("An error occurred while saving the player data for $profile", e)
            }
        }
        this.player = player
        this.inventory = null
        if (player != null) {
            // Update the game profile, in case anything changed
            this.profile = player.getProfile()
            check(this.uniqueId == this.profile.uniqueId)
            try {
                UserIO.load(Lantern.getGame().savesDirectory, this.player)
            } catch (e: IOException) {
                Lantern.getLogger().warn("An error occurred while loading the player data for $profile", e)
            }
        }
    }

    var internalUser: AbstractPlayer?
        get() = this.player
        set(value) = setInternalPlayer0(value)

    private fun resolvePlayer(): AbstractPlayer {
        val user = this.player
        if (user != null)
            return user
        val offlineUser = OfflinePlayer(this.profile)
        this.setInternalPlayer0(offlineUser)
        return offlineUser
    }

    override val delegateDataHolder: DataHolder.Mutable
        get() = this.resolvePlayer()

    override fun getSubject(): Subject = this.resolvePlayer()

    override fun getInventory(): UserInventory {
        var userInventory = this.inventory
        if (userInventory != null)
            return userInventory
        val playerInventory = this.resolvePlayer().inventory
        userInventory = LanternUserInventory(playerInventory, this)
        this.inventory = userInventory
        return userInventory
    }

    override fun canEquip(type: EquipmentType): Boolean = this.resolvePlayer().canEquip(type)
    override fun canEquip(type: EquipmentType, equipment: ItemStack): Boolean = this.resolvePlayer().canEquip(type, equipment)
    override fun getEquipped(type: EquipmentType): Optional<ItemStack> = this.resolvePlayer().getEquipped(type)
    override fun equip(type: EquipmentType, equipment: ItemStack): Boolean = this.resolvePlayer().equip(type, equipment)
    override fun isOnline(): Boolean = this.resolvePlayer() is LanternPlayer
    override fun getPlayer(): Optional<Player> = (this.resolvePlayer() as? Player).asOptional()
    override fun getPosition(): Vector3d = this.resolvePlayer().position
    override fun getWorldKey(): NamespacedKey = this.resolvePlayer().worldKey
    override fun setLocation(world: NamespacedKey, position: Vector3d): Boolean = this.resolvePlayer().setLocation(Location.of(worldKey, position))
    override fun setRotation(rotation: Vector3d) { this.resolvePlayer().rotation = rotation }
    override fun getRotation(): Vector3d = this.resolvePlayer().rotation
    override fun getEnderChestInventory(): Inventory = this.resolvePlayer().enderChestInventory
    override fun getHead(): ItemStack = this.resolvePlayer().head
    override fun setHead(head: ItemStack) { this.resolvePlayer().head = head }
    override fun getChest(): ItemStack = this.resolvePlayer().chest
    override fun setChest(chestplate: ItemStack) { this.resolvePlayer().chest = chestplate }
    override fun getLegs(): ItemStack = this.resolvePlayer().legs
    override fun setLegs(legs: ItemStack) { this.resolvePlayer().legs = legs }
    override fun getFeet(): ItemStack = this.resolvePlayer().feet
    override fun setFeet(feet: ItemStack) { this.resolvePlayer().feet = feet }
    override fun getItemInHand(handType: HandType): ItemStack = this.resolvePlayer().getItemInHand(handType)
    override fun setItemInHand(hand: HandType, itemInHand: ItemStack) { this.resolvePlayer().setItemInHand(hand, itemInHand) }
}
