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

import org.lanternpowered.api.catalog.CatalogKeys.lantern
import org.lanternpowered.api.data.valueKeyOf
import org.lanternpowered.api.util.ranges.rangeTo
import org.lanternpowered.server.data.type.LanternBedPart
import org.lanternpowered.server.data.type.LanternDoorHalf
import org.lanternpowered.server.data.type.RedstoneConnectionType
import org.lanternpowered.server.entity.Pose
import org.lanternpowered.server.extra.accessory.Accessory
import org.lanternpowered.server.inventory.InventorySnapshot
import org.lanternpowered.server.item.recipe.RecipeBookState
import org.spongepowered.api.advancement.AdvancementTree
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.InstrumentType
import org.spongepowered.api.data.type.SkinPart
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.type.CarriedInventory

object LanternKeys {

    @JvmField val FAILED_DATA_MANIPULATORS: Key<ListValue<DataView>> = valueKeyOf(lantern("failed_data_manipulators"))
    @JvmField val FAILED_DATA_VALUES: Key<Value<DataView>> = valueKeyOf(lantern("failed_data_values"))

    /**
     * Represents the cooldown before something can pass through a portal.
     */
    @JvmField val PORTAL_COOLDOWN_TICKS: Key<Value<Int>> = valueKeyOf(lantern("portal_cooldown_ticks"))

    /**
     * Represents the score of something, primarily used for the score on a player death screen.
     */
    @JvmField val SCORE: Key<Value<Int>> = valueKeyOf(lantern("score"))

    /**
     * Whether something can pick up loot.
     */
    @JvmField val CAN_PICK_UP_LOOT: Key<Value<Boolean>> = valueKeyOf(lantern("can_pick_up_loot"))

    /**
     * Whether a entity is a baby.
     */
    @JvmField val IS_BABY: Key<Value<Boolean>> = valueKeyOf(lantern("is_baby"))

    /**
     * Whether something is holding its hands up, usually related to zombies.
     */
    @JvmField val ARE_HANDS_UP: Key<Value<Boolean>> = valueKeyOf(lantern("are_hands_up"))

    /**
     * How many arrows are stuck a entity's body.
     */
    @JvmField val ARROWS_IN_ENTITY: Key<BoundedValue<Int>> = valueKeyOf(lantern("arrows_in_entity")) { range(0..Int.MAX_VALUE) }

    /**
     * Whether something is converting, usually a zombie villager converting to a villager.
     */
    @JvmField val IS_CONVERTING: Key<Value<Boolean>> = valueKeyOf(lantern("is_converting"))

    /**
     * A set with all the parts of a skin that should be displayed.
     */
    @JvmField val DISPLAYED_SKIN_PARTS: Key<SetValue<SkinPart>> = valueKeyOf(lantern("displayed_skin_parts"))

    /**
     * A factor which alters how much something (e.g. entity) is affected by gravity.
     */
    @JvmField val GRAVITY_FACTOR: Key<Value<Double>> = valueKeyOf(lantern("gravity_factor"))

    /**
     * Represents the part of a door.
     */
    @JvmField val DOOR_HALF: Key<Value<LanternDoorHalf>> = valueKeyOf(lantern("door_half"))

    /**
     * Represents the part of a bed.
     */
    @JvmField val BED_PART: Key<Value<LanternBedPart>> = valueKeyOf(lantern("bed_part"))

    /**
     * Whether something is enabled.
     */
    @JvmField val ENABLED: Key<Value<Boolean>> = valueKeyOf(lantern("enabled"))

    /**
     * The inventory snapshot of something.
     */
    @JvmField val INVENTORY_SNAPSHOT: Key<Value<InventorySnapshot>> = valueKeyOf(lantern("inventory_snapshot"))

    /**
     * Whether something is triggered.
     */
    @JvmField val TRIGGERED: Key<Value<Boolean>> = valueKeyOf(lantern("triggered"))

    /**
     * Whether something is unstable.
     */
    @JvmField val UNSTABLE: Key<Value<Boolean>> = valueKeyOf(lantern("unstable"))

    @JvmField val ITEM_INVENTORY: Key<Value<CarriedInventory<out Carrier>>> = valueKeyOf(lantern("item_inventory"))

    /**
     * Whether a iron golem is holding a poppy.
     */
    @JvmField val HOLDS_POPPY: Key<Value<Boolean>> = valueKeyOf(lantern("holds_poppy"))

    /**
     * Whether a snowman has a pumpkin head.
     */
    @JvmField val HAS_PUMPKIN_HEAD: Key<Value<Boolean>> = valueKeyOf(lantern("has_pumpkin_head"))

    /**
     * Whether something is hanging.
     */
    @JvmField val IS_HANGING: Key<Value<Boolean>> = valueKeyOf(lantern("is_hanging"))

    /**
     * Whether the elytra speed boost is enabled.
     */
    @JvmField val ELYTRA_SPEED_BOOST: Key<Value<Boolean>> = valueKeyOf(lantern("elytra_speed_boost"))

    /**
     * The elytra glide speed.
     */
    @JvmField val ELYTRA_GLIDE_SPEED: Key<Value<Double>> = valueKeyOf(lantern("elytra_glide_speed"))

    /**
     * Whether super steve is enabled.
     */
    @JvmField val SUPER_STEVE: Key<Value<Boolean>> = valueKeyOf(lantern("super_steve"))

    /**
     * Whether a entity can wall jump.
     */
    @JvmField val CAN_WALL_JUMP: Key<Value<Boolean>> = valueKeyOf(lantern("can_wall_jump"))

    /**
     * Whether a entity can use dual wielding with multiple weapons.
     */
    @JvmField val CAN_DUAL_WIELD: Key<Value<Boolean>> = valueKeyOf(lantern("can_dual_wield"))

    /**
     * Whether an item is dual wieldable.
     */
    @JvmField val IS_DUAL_WIELDABLE: Key<Value<Boolean>> = valueKeyOf(lantern("is_dual_wieldable"))

    /**
     * The hand of a entity that is currently active with an interaction.
     */
    @JvmField val ACTIVE_HAND = valueKeyOf<Value<HandType>>(lantern("active_hand"))

    /**
     * The maximum exhaustion of a entity.
     */
    @JvmField val MAX_EXHAUSTION = valueKeyOf<BoundedValue<Double>>(lantern("max_exhaustion")) { range(0..Double.MAX_VALUE) }

    /**
     * The maximum food level of a entity.
     */
    @JvmField val MAX_FOOD_LEVEL = valueKeyOf<BoundedValue<Int>>(lantern("max_food_level")) { range(0..Int.MAX_VALUE) }

    /**
     * The crafting recipe book state.
     */
    @JvmField val CRAFTING_RECIPE_BOOK_STATE = valueKeyOf<Value<RecipeBookState>>(lantern("crafting_recipe_book_state"))

    /**
     * The smelting recipe book state.
     */
    @JvmField val SMELTING_RECIPE_BOOK_STATE: Key<Value<RecipeBookState>> = valueKeyOf(lantern("smelting_recipe_book_state"))

    @JvmField val ACCESSORIES: Key<ListValue<Accessory>> = valueKeyOf(lantern("accessories"))

    @JvmField val OPEN_ADVANCEMENT_TREE: Key<Value<AdvancementTree>> = valueKeyOf(lantern("open_advancement_tree"))

    @JvmField val ARE_PLAYING: Key<Value<Boolean>> = valueKeyOf(lantern("are_playing"))

    @JvmField val HAS_MUSIC_DISC: Key<Value<Boolean>> = valueKeyOf(lantern("has_music_disc"))

    @JvmField val FIELD_OF_VIEW_MODIFIER: Key<Value<Double>> = valueKeyOf(lantern("field_of_view_modifier"))

    @JvmField val HAS_CHEST: Key<Value<Boolean>> = valueKeyOf(lantern("has_chest"))

    @JvmField val FINE_ROTATION: Key<BoundedValue<Int>> = valueKeyOf(lantern("fine_rotation")) { range(0..Integer.MAX_VALUE) }

    @JvmField val WATERLOGGED: Key<Value<Boolean>> = valueKeyOf(lantern("waterlogged"))

    @JvmField val DUMMY: Key<Value<Boolean>> = valueKeyOf(lantern("dummy"))

    @JvmField val INSTRUMENT_TYPE: Key<Value<InstrumentType>> = valueKeyOf(lantern("instrument_type"))

    @JvmField val REDSTONE_NORTH_CONNECTION: Key<Value<RedstoneConnectionType>> = valueKeyOf(lantern("redstone_north_connection"))
    @JvmField val REDSTONE_SOUTH_CONNECTION: Key<Value<RedstoneConnectionType>> = valueKeyOf(lantern("redstone_south_connection"))
    @JvmField val REDSTONE_EAST_CONNECTION: Key<Value<RedstoneConnectionType>> = valueKeyOf(lantern("redstone_east_connection"))
    @JvmField val REDSTONE_WEST_CONNECTION: Key<Value<RedstoneConnectionType>> = valueKeyOf(lantern("redstone_west_connection"))

    @JvmField val CONNECTED_NORTH: Key<Value<Boolean>> = valueKeyOf(lantern("connected_north"))
    @JvmField val CONNECTED_SOUTH: Key<Value<Boolean>> = valueKeyOf(lantern("connected_south"))
    @JvmField val CONNECTED_EAST: Key<Value<Boolean>> = valueKeyOf(lantern("connected_east"))
    @JvmField val CONNECTED_WEST: Key<Value<Boolean>> = valueKeyOf(lantern("connected_west"))

    @JvmField val POSE: Key<Value<Pose>> = valueKeyOf(lantern("pose"))
}
