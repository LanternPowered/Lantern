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
package org.lanternpowered.server.data.key

import org.lanternpowered.api.data.type.TopHat
import org.lanternpowered.api.data.valueKeyOf
import org.lanternpowered.api.key.lanternKey
import org.lanternpowered.server.block.property.FlammableInfo
import org.lanternpowered.server.data.type.LanternBedPart
import org.lanternpowered.server.data.type.LanternDoorHalf
import org.lanternpowered.server.data.type.LanternWireAttachmentType
import org.lanternpowered.server.entity.Pose
import org.lanternpowered.server.inventory.InventorySnapshot
import org.lanternpowered.server.item.recipe.RecipeBookState
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.block.BlockSoundGroup
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.InstrumentType
import org.spongepowered.api.data.type.SkinPart
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.type.CarriedInventory
import kotlin.time.Duration

object LanternKeys {

    @JvmField val FAILED_DATA_MANIPULATORS: Key<ListValue<DataView>> = valueKeyOf(lanternKey("failed_data_manipulators"))
    @JvmField val FAILED_DATA_VALUES: Key<Value<DataView>> = valueKeyOf(lanternKey("failed_data_values"))

    /**
     * Represents the cooldown before something can pass through a portal.
     */
    @JvmField val PORTAL_COOLDOWN_TICKS: Key<Value<Int>> = valueKeyOf(lanternKey("portal_cooldown_ticks"))

    /**
     * Represents the score of something, primarily used for the score on a player death screen.
     */
    @JvmField val SCORE: Key<Value<Int>> = valueKeyOf(lanternKey("score"))

    /**
     * Whether something can pick up loot.
     */
    @JvmField val CAN_PICK_UP_LOOT: Key<Value<Boolean>> = valueKeyOf(lanternKey("can_pick_up_loot"))

    /**
     * Whether a entity is a baby.
     */
    @JvmField val IS_BABY: Key<Value<Boolean>> = valueKeyOf(lanternKey("is_baby"))

    /**
     * Whether something is holding its hands up, usually related to zombies.
     */
    @JvmField val ARE_HANDS_UP: Key<Value<Boolean>> = valueKeyOf(lanternKey("are_hands_up"))

    /**
     * How many arrows are stuck a entity's body.
     */
    @JvmField val ARROWS_IN_ENTITY: Key<Value<Int>> = valueKeyOf(lanternKey("arrows_in_entity"))

    /**
     * Whether something is converting, usually a zombie villager converting to a villager.
     */
    @JvmField val IS_CONVERTING: Key<Value<Boolean>> = valueKeyOf(lanternKey("is_converting"))

    /**
     * A set with all the parts of a skin that should be displayed.
     */
    @JvmField val DISPLAYED_SKIN_PARTS: Key<SetValue<SkinPart>> = valueKeyOf(lanternKey("displayed_skin_parts"))

    /**
     * A factor which alters how much something (e.g. entity) is affected by gravity.
     */
    @JvmField val GRAVITY_FACTOR: Key<Value<Double>> = valueKeyOf(lanternKey("gravity_factor"))

    /**
     * Represents the part of a door.
     */
    @JvmField val DOOR_HALF: Key<Value<LanternDoorHalf>> = valueKeyOf(lanternKey("door_half"))

    /**
     * Represents the part of a bed.
     */
    @JvmField val BED_PART: Key<Value<LanternBedPart>> = valueKeyOf(lanternKey("bed_part"))

    /**
     * Whether something is enabled.
     */
    @JvmField val ENABLED: Key<Value<Boolean>> = valueKeyOf(lanternKey("enabled"))

    /**
     * The inventory snapshot of something.
     */
    @JvmField val INVENTORY_SNAPSHOT: Key<Value<InventorySnapshot>> = valueKeyOf(lanternKey("inventory_snapshot"))

    /**
     * Whether something is triggered.
     */
    @JvmField val TRIGGERED: Key<Value<Boolean>> = valueKeyOf(lanternKey("triggered"))

    /**
     * Whether something is unstable.
     */
    @JvmField val UNSTABLE: Key<Value<Boolean>> = valueKeyOf(lanternKey("unstable"))

    @JvmField val ITEM_INVENTORY: Key<Value<CarriedInventory<out Carrier>>> = valueKeyOf(lanternKey("item_inventory"))

    /**
     * Whether a iron golem is holding a poppy.
     */
    @JvmField val HOLDS_POPPY: Key<Value<Boolean>> = valueKeyOf(lanternKey("holds_poppy"))

    /**
     * Whether a snowman has a pumpkin head.
     */
    @JvmField val HAS_PUMPKIN_HEAD: Key<Value<Boolean>> = valueKeyOf(lanternKey("has_pumpkin_head"))

    /**
     * Whether something is hanging.
     */
    @JvmField val IS_HANGING: Key<Value<Boolean>> = valueKeyOf(lanternKey("is_hanging"))

    /**
     * Whether the elytra speed boost is enabled.
     */
    @JvmField val ELYTRA_SPEED_BOOST: Key<Value<Boolean>> = valueKeyOf(lanternKey("elytra_speed_boost"))

    /**
     * The elytra glide speed.
     */
    @JvmField val ELYTRA_GLIDE_SPEED: Key<Value<Double>> = valueKeyOf(lanternKey("elytra_glide_speed"))

    /**
     * Whether super steve is enabled.
     */
    @JvmField val SUPER_STEVE: Key<Value<Boolean>> = valueKeyOf(lanternKey("super_steve"))

    /**
     * Whether a entity can wall jump.
     */
    @JvmField val CAN_WALL_JUMP: Key<Value<Boolean>> = valueKeyOf(lanternKey("can_wall_jump"))

    /**
     * Whether a entity can use dual wielding with multiple weapons.
     */
    @JvmField val CAN_DUAL_WIELD: Key<Value<Boolean>> = valueKeyOf(lanternKey("can_dual_wield"))

    /**
     * Whether an item is dual wieldable.
     */
    @JvmField val IS_DUAL_WIELDABLE: Key<Value<Boolean>> = valueKeyOf(lanternKey("is_dual_wieldable"))

    /**
     * The hand of a entity that is currently active with an interaction.
     */
    @JvmField val ACTIVE_HAND = valueKeyOf<Value<HandType>>(lanternKey("active_hand"))

    /**
     * The maximum exhaustion of a entity.
     */
    @JvmField val MAX_EXHAUSTION = valueKeyOf<Value<Double>>(lanternKey("max_exhaustion"))

    /**
     * The maximum food level of a entity.
     */
    @JvmField val MAX_FOOD_LEVEL = valueKeyOf<Value<Int>>(lanternKey("max_food_level"))

    /**
     * The crafting recipe book state.
     */
    @JvmField val CRAFTING_RECIPE_BOOK_STATE = valueKeyOf<Value<RecipeBookState>>(lanternKey("crafting_recipe_book_state"))

    /**
     * The furnace recipe book state.
     */
    @JvmField val FURNACE_RECIPE_BOOK_STATE: Key<Value<RecipeBookState>> = valueKeyOf(lanternKey("furnace_recipe_book_state"))

    /**
     * The blast furnace recipe book state.
     */
    @JvmField val BLAST_FURNACE_RECIPE_BOOK_STATE: Key<Value<RecipeBookState>> = valueKeyOf(lanternKey("blast_furnace_recipe_book_state"))

    /**
     * The smoker recipe book state.
     */
    @JvmField val SMOKER_RECIPE_BOOK_STATE: Key<Value<RecipeBookState>> = valueKeyOf(lanternKey("smoker_recipe_book_state"))

    @JvmField val TOP_HAT: Key<Value<TopHat>> = valueKeyOf(lanternKey("top_hat"))

    @JvmField val OPEN_ADVANCEMENT_TREE: Key<Value<AdvancementTree>> = valueKeyOf(lanternKey("open_advancement_tree"))

    @JvmField val ARE_PLAYING: Key<Value<Boolean>> = valueKeyOf(lanternKey("are_playing"))

    @JvmField val HAS_MUSIC_DISC: Key<Value<Boolean>> = valueKeyOf(lanternKey("has_music_disc"))

    @JvmField val FIELD_OF_VIEW_MODIFIER: Key<Value<Double>> = valueKeyOf(lanternKey("field_of_view_modifier"))

    @JvmField val HAS_CHEST: Key<Value<Boolean>> = valueKeyOf(lanternKey("has_chest"))

    @JvmField val FINE_ROTATION: Key<Value<Int>> = valueKeyOf(lanternKey("fine_rotation"))

    @JvmField val WATERLOGGED: Key<Value<Boolean>> = valueKeyOf(lanternKey("waterlogged"))

    @JvmField val DUMMY: Key<Value<Boolean>> = valueKeyOf(lanternKey("dummy"))

    @JvmField val INSTRUMENT_TYPE: Key<Value<InstrumentType>> = valueKeyOf(lanternKey("instrument_type"))

    @JvmField val REDSTONE_NORTH_CONNECTION: Key<Value<LanternWireAttachmentType>> = valueKeyOf(lanternKey("redstone_north_connection"))
    @JvmField val REDSTONE_SOUTH_CONNECTION: Key<Value<LanternWireAttachmentType>> = valueKeyOf(lanternKey("redstone_south_connection"))
    @JvmField val REDSTONE_EAST_CONNECTION: Key<Value<LanternWireAttachmentType>> = valueKeyOf(lanternKey("redstone_east_connection"))
    @JvmField val REDSTONE_WEST_CONNECTION: Key<Value<LanternWireAttachmentType>> = valueKeyOf(lanternKey("redstone_west_connection"))

    @JvmField val CONNECTED_NORTH: Key<Value<Boolean>> = valueKeyOf(lanternKey("connected_north"))
    @JvmField val CONNECTED_SOUTH: Key<Value<Boolean>> = valueKeyOf(lanternKey("connected_south"))
    @JvmField val CONNECTED_EAST: Key<Value<Boolean>> = valueKeyOf(lanternKey("connected_east"))
    @JvmField val CONNECTED_WEST: Key<Value<Boolean>> = valueKeyOf(lanternKey("connected_west"))

    @JvmField val POSE: Key<Value<Pose>> = valueKeyOf(lanternKey("pose"))

    @JvmField val PICKUP_DELAY: Key<Value<Duration>> = valueKeyOf(lanternKey("pickup_delay"))
    @JvmField val DESPAWN_DELAY: Key<Value<Duration>> = valueKeyOf(lanternKey("despawn_delay"))

    @JvmField val BLOCK_SOUND_GROUP: Key<Value<BlockSoundGroup>> = valueKeyOf(lanternKey("block_sound_group"))
    @JvmField val FLAMMABLE_INFO: Key<Value<FlammableInfo>> = valueKeyOf(lanternKey("flammable_info"))
}
