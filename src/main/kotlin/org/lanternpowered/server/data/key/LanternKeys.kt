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

    @JvmField val FAILED_DATA_MANIPULATORS = valueKeyOf<ListValue<DataView>>(lantern("failed_data_manipulators"))
    @JvmField val FAILED_DATA_VALUES = valueKeyOf<Value<DataView>>(lantern("failed_data_values"))

    /**
     * Represents the cooldown before something can pass through a portal.
     */
    @JvmField val PORTAL_COOLDOWN_TICKS = valueKeyOf<Value<Int>>(lantern("portal_cooldown_ticks"))

    /**
     * Represents the score of something, primarily used for the score on a player death screen.
     */
    @JvmField val SCORE = valueKeyOf<Value<Int>>(lantern("score"))

    /**
     * Whether something can pick up loot.
     */
    @JvmField val CAN_PICK_UP_LOOT = valueKeyOf<Value<Boolean>>(lantern("can_pick_up_loot"))

    /**
     * Whether a entity is a baby.
     */
    @JvmField val IS_BABY = valueKeyOf<Value<Boolean>>(lantern("is_baby"))

    /**
     * Whether something is holding its hands up, usually related to zombies.
     */
    @JvmField val ARE_HANDS_UP = valueKeyOf<Value<Boolean>>(lantern("are_hands_up"))

    /**
     * How many arrows are stuck a entity's body.
     */
    @JvmField val ARROWS_IN_ENTITY = valueKeyOf<BoundedValue<Int>>(lantern("arrows_in_entity")) { range(0..Int.MAX_VALUE) }

    /**
     * Whether something is converting, usually a zombie villager converting to a villager.
     */
    @JvmField val IS_CONVERTING = valueKeyOf<Value<Boolean>>(lantern("is_converting"))

    /**
     * A set with all the parts of a skin that should be displayed.
     */
    @JvmField val DISPLAYED_SKIN_PARTS = valueKeyOf<SetValue<SkinPart>>(lantern("displayed_skin_parts"))

    /**
     * A factor which alters how much something (e.g. entity) is affected by gravity.
     */
    @JvmField val GRAVITY_FACTOR = valueKeyOf<Value<Double>>(lantern("gravity_factor"))

    /**
     * Represents the part of a door.
     */
    @JvmField val DOOR_HALF = valueKeyOf<Value<LanternDoorHalf>>(lantern("door_half"))

    /**
     * Represents the part of a bed.
     */
    @JvmField val BED_PART = valueKeyOf<Value<LanternBedPart>>(lantern("bed_part"))

    /**
     * Whether something is enabled.
     */
    @JvmField val ENABLED = valueKeyOf<Value<Boolean>>(lantern("enabled"))

    /**
     * The inventory snapshot of something.
     */
    @JvmField val INVENTORY_SNAPSHOT = valueKeyOf<Value<InventorySnapshot>>(lantern("inventory_snapshot"))

    /**
     * Whether something is triggered.
     */
    @JvmField val TRIGGERED = valueKeyOf<Value<Boolean>>(lantern("triggered"))

    /**
     * Whether something is unstable.
     */
    @JvmField val UNSTABLE = valueKeyOf<Value<Boolean>>(lantern("unstable"))

    @JvmField val ITEM_INVENTORY = valueKeyOf<Value<CarriedInventory<out Carrier>>>(lantern("item_inventory"))

    /**
     * Whether a iron golem is holding a poppy.
     */
    @JvmField val HOLDS_POPPY = valueKeyOf<Value<Boolean>>(lantern("holds_poppy"))

    /**
     * Whether a snowman has a pumpkin head.
     */
    @JvmField val HAS_PUMPKIN_HEAD = valueKeyOf<Value<Boolean>>(lantern("has_pumpkin_head"))

    /**
     * Whether something is hanging.
     */
    @JvmField val IS_HANGING = valueKeyOf<Value<Boolean>>(lantern("is_hanging"))

    /**
     * Whether the elytra speed boost is enabled.
     */
    @JvmField val ELYTRA_SPEED_BOOST = valueKeyOf<Value<Boolean>>(lantern("elytra_speed_boost"))

    /**
     * The elytra glide speed.
     */
    @JvmField val ELYTRA_GLIDE_SPEED = valueKeyOf<Value<Double>>(lantern("elytra_glide_speed"))

    /**
     * Whether super steve is enabled.
     */
    @JvmField val SUPER_STEVE = valueKeyOf<Value<Boolean>>(lantern("super_steve"))

    /**
     * Whether a entity can wall jump.
     */
    @JvmField val CAN_WALL_JUMP = valueKeyOf<Value<Boolean>>(lantern("can_wall_jump"))

    /**
     * Whether a entity can use dual wielding with multiple weapons.
     */
    @JvmField val CAN_DUAL_WIELD = valueKeyOf<Value<Boolean>>(lantern("can_dual_wield"))

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
    @JvmField val SMELTING_RECIPE_BOOK_STATE = valueKeyOf<Value<RecipeBookState>>(lantern("smelting_recipe_book_state"))

    @JvmField val ACCESSORIES = valueKeyOf<ListValue<Accessory>>(lantern("accessories"))

    @JvmField val OPEN_ADVANCEMENT_TREE = valueKeyOf<Value<AdvancementTree>>(lantern("open_advancement_tree"))

    @JvmField val ARE_PLAYING = valueKeyOf<Value<Boolean>>(lantern("are_playing"))

    @JvmField val HAS_MUSIC_DISC = valueKeyOf<Value<Boolean>>(lantern("has_music_disc"))

    @JvmField val FIELD_OF_VIEW_MODIFIER = valueKeyOf<Value<Double>>(lantern("field_of_view_modifier"))

    @JvmField val HAS_CHEST = valueKeyOf<Value<Boolean>>(lantern("has_chest"))

    @JvmField val FINE_ROTATION = valueKeyOf<BoundedValue<Int>>(lantern("fine_rotation")) { range(0..Integer.MAX_VALUE) }

    @JvmField val WATERLOGGED = valueKeyOf<Value<Boolean>>(lantern("waterlogged"))

    @JvmField val DUMMY = valueKeyOf<Value<Boolean>>(lantern("dummy"))

    @JvmField val INSTRUMENT_TYPE = valueKeyOf<Value<InstrumentType>>(lantern("instrument_type"))

    @JvmField val REDSTONE_NORTH_CONNECTION = valueKeyOf<Value<RedstoneConnectionType>>(lantern("redstone_north_connection"))
    @JvmField val REDSTONE_SOUTH_CONNECTION = valueKeyOf<Value<RedstoneConnectionType>>(lantern("redstone_south_connection"))
    @JvmField val REDSTONE_EAST_CONNECTION = valueKeyOf<Value<RedstoneConnectionType>>(lantern("redstone_east_connection"))
    @JvmField val REDSTONE_WEST_CONNECTION = valueKeyOf<Value<RedstoneConnectionType>>(lantern("redstone_west_connection"))

    @JvmField val CONNECTED_NORTH = valueKeyOf<Value<Boolean>>(lantern("connected_north"))
    @JvmField val CONNECTED_SOUTH = valueKeyOf<Value<Boolean>>(lantern("connected_south"))
    @JvmField val CONNECTED_EAST = valueKeyOf<Value<Boolean>>(lantern("connected_east"))
    @JvmField val CONNECTED_WEST = valueKeyOf<Value<Boolean>>(lantern("connected_west"))

    @JvmField val POSE = valueKeyOf<Value<Pose>>(lantern("pose"))
}
