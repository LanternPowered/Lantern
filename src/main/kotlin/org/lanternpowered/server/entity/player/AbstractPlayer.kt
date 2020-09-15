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
package org.lanternpowered.server.entity.player

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.textOf
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.EntityCreationData
import org.lanternpowered.server.entity.LanternLiving
import org.lanternpowered.server.entity.Pose
import org.lanternpowered.server.entity.player.gamemode.LanternGameMode
import org.lanternpowered.server.entity.player.tab.GlobalTabList
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.inventory.equipment.AbstractArmorEquipable
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory
import org.lanternpowered.server.item.recipe.RecipeBookState
import org.lanternpowered.server.permission.ProxySubject
import org.lanternpowered.server.registry.type.data.SkinPartRegistry
import org.lanternpowered.server.statistic.StatisticMap
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.type.HandPreferences
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate

abstract class AbstractPlayer(private val profile: GameProfile) : LanternLiving(EntityCreationData(profile.uniqueId, EntityTypes.PLAYER.get())),
        ProxySubject, Carrier, Subject, AbstractArmorEquipable {

    companion object {

        const val DEFAULT_EXHAUSTION = 0.0
        const val DEFAULT_SATURATION = 5.0
    }

    /**
     * The statistics of this player.
     */
    val statistics = StatisticMap()

    /**
     * The ender chest inventory of this player.
     */
    val enderChestInventory: ChestInventory

    private val playerInventory: LanternPlayerInventory

    init {
        this.offer(Keys.DISPLAY_NAME, textOf(this.profile.name.get()))

        @Suppress("LeakingThis")
        this.playerInventory = VanillaInventoryArchetypes.PLAYER.builder()
                .withCarrier(this).build(Lantern.getMinecraftPlugin())
        @Suppress("LeakingThis")
        this.enderChestInventory = VanillaInventoryArchetypes.ENDER_CHEST.builder()
                .withCarrier(this).build(Lantern.getMinecraftPlugin())
        this.resolveSubject()

        keyRegistry {
            register(LanternKeys.TOP_HAT)
            registerBounded(LanternKeys.MAX_EXHAUSTION, 4.0).minimum(0.0).maximum(Double.MAX_VALUE)
            registerBounded(Keys.EXHAUSTION, DEFAULT_EXHAUSTION).minimum(0.0).maximum(LanternKeys.MAX_EXHAUSTION)
            registerBounded(LanternKeys.MAX_FOOD_LEVEL, 20).minimum(0).maximum(Int.MAX_VALUE)
            registerBounded(Keys.FOOD_LEVEL, 20).minimum(0).maximum(LanternKeys.MAX_FOOD_LEVEL)
            registerBounded(Keys.SATURATION, DEFAULT_SATURATION)
                    .minimum(0.0)
                    .maximum { this.get(Keys.FOOD_LEVEL).orElse(20).toDouble() }
            register(Keys.LAST_DATE_PLAYED)
            register(Keys.LAST_DATE_JOINED)
            register(Keys.FIRST_DATE_JOINED)
            register(Keys.WALKING_SPEED, 0.1)
            register(LanternKeys.FIELD_OF_VIEW_MODIFIER, 1.0)
            register(Keys.IS_FLYING, false)
            register(Keys.IS_SPRINTING, false)
            register(Keys.FLYING_SPEED, 0.1)
            register(Keys.CAN_FLY, false)
            register(Keys.RESPAWN_LOCATIONS, mapOf())
            register(Keys.GAME_MODE, GameModes.NOT_SET)
                    .addChangeListener { newValue, _ ->
                        (newValue as LanternGameMode).abilityApplier(this)
                        // This MUST be updated, unless you want strange behavior on the client,
                        // the client has 3 different concepts of 'isCreative', and each combination
                        // gives a different outcome...
                        // For example:
                        // - Disable noClip and glow in spectator, but you can place blocks
                        // - NoClip in creative, but you cannot change your hotbar, or drop items
                        // Not really worth the trouble right now
                        // TODO: Differentiate the 'global tab list entry' and the entry to update
                        // TODO: these kind of settings to avoid possible 'strange' behavior.
                        GlobalTabList[this.profile]?.setGameMode(newValue)
                    }
            register(Keys.DOMINANT_HAND, HandPreferences.RIGHT)
            register(Keys.IS_ELYTRA_FLYING, false)
            register(LanternKeys.ELYTRA_GLIDE_SPEED, 0.1)
            register(LanternKeys.ELYTRA_SPEED_BOOST, false)
            register(LanternKeys.SUPER_STEVE, false)
            register(LanternKeys.CAN_WALL_JUMP, false)
            register(LanternKeys.CAN_DUAL_WIELD, false)
            register(LanternKeys.SCORE, 0)
            register(LanternKeys.ACTIVE_HAND)
            register(LanternKeys.FURNACE_RECIPE_BOOK_STATE, RecipeBookState.DEFAULT)
            register(LanternKeys.CRAFTING_RECIPE_BOOK_STATE, RecipeBookState.DEFAULT)
            register(LanternKeys.SMOKER_RECIPE_BOOK_STATE, RecipeBookState.DEFAULT)
            register(LanternKeys.BLAST_FURNACE_RECIPE_BOOK_STATE, RecipeBookState.DEFAULT)
            register(LanternKeys.OPEN_ADVANCEMENT_TREE)
            register(LanternKeys.DISPLAYED_SKIN_PARTS, SkinPartRegistry.all.toSet())
            register(LanternKeys.POSE, Pose.STANDING)
            registerProvider(Keys.STATISTICS) {
                get { this.statistics.statisticValues }
                set { value -> this.statistics.statisticValues = value }
            }
        }
    }

    fun getProfile(): GameProfile = this.profile

    override fun getIdentifier(): String = this.uniqueId.toString()

    open val worldKey: NamespacedKey
        get() = this.world.key

    override fun getInventory(): LanternPlayerInventory = this.playerInventory
    override fun getEquipment(): EquipmentInventory = this.inventory.equipment

    override var internalSubject: SubjectReference? = null

    override val subjectCollectionIdentifier: String
        get() = PermissionService.SUBJECTS_USER

    override fun getPermissionDefault(permission: String): Tristate = Tristate.FALSE
}
