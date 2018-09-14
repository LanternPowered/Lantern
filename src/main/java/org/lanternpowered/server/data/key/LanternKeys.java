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
package org.lanternpowered.server.data.key;

import static org.lanternpowered.server.data.key.LanternKeyFactory.makeListKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMutableBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeOptionalKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeValueKey;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.lanternpowered.server.effect.potion.PotionType;
import org.lanternpowered.server.extra.accessory.Accessory;
import org.lanternpowered.server.inventory.InventorySnapshot;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

public final class LanternKeys {

    // Internal key when data failed to deserialize,
    // please don't touch it!
    public static final Key<ListValue<DataView>> FAILED_DATA_MANIPULATORS =
            makeListKey(DataView.class, DataQuery.of("FailedDataManipulators"), "failed_data_manipulators");
    public static final Key<Value<DataView>> FAILED_DATA_VALUES =
            makeValueKey(DataView.class, DataQuery.of("FailedDataValues"), "failed_data_values");

    public static final Key<Value<Integer>> PORTAL_COOLDOWN_TICKS =
            makeValueKey(Integer.class, DataQuery.of("PortalCooldownTicks"), "portal_cooldown_ticks");
    public static final Key<Value<Integer>> SCORE =
            makeValueKey(Integer.class, DataQuery.of("Score"), "score");
    public static final Key<Value<Boolean>> CAN_PICK_UP_LOOT =
            makeValueKey(Boolean.class, DataQuery.of("CanPickupLoot"), "can_pickup_loot");
    public static final Key<Value<Boolean>> IS_EFFECT =
            makeValueKey(Boolean.class, DataQuery.of("IsEffect"), "is_effect");
    public static final Key<Value<Boolean>> IS_BABY =
            makeValueKey(Boolean.class, DataQuery.of("IsBaby"), "is_baby");
    public static final Key<Value<Boolean>> ARE_HANDS_UP =
            makeValueKey(Boolean.class, DataQuery.of("AreHandsUp"), "are_hands_up");
    public static final Key<Value<Integer>> ARROWS_IN_ENTITY =
            makeValueKey(Integer.class, DataQuery.of("ArrowsInEntity"), "arrows_in_entity");
    public static final Key<Value<Boolean>> IS_CONVERTING =
            makeValueKey(Boolean.class, DataQuery.of("IsConverting"), "is_converting");
    public static final Key<SetValue<SkinPart>> DISPLAYED_SKIN_PARTS =
            makeSetKey(SkinPart.class, DataQuery.of("DisplayedSkinParts"), "displayed_skin_parts");
    public static final Key<Value<Double>> GRAVITY_FACTOR =
            makeValueKey(Double.class, DataQuery.of("GravityFactor"), "gravity_factor");
    public static final Key<Value<LanternDoorHalf>> DOOR_HALF =
            makeValueKey(LanternDoorHalf.class, DataQuery.of("DoorHalf"), "door_half");
    public static final Key<Value<Boolean>> CHECK_DECAY =
            makeValueKey(Boolean.class, DataQuery.of("CheckDecay"), "check_decay");
    public static final Key<Value<LanternBedPart>> BED_PART =
            makeValueKey(LanternBedPart.class, DataQuery.of("BedPart"), "bed_part");
    public static final Key<Value<Boolean>> ENABLED =
            makeValueKey(Boolean.class, DataQuery.of("Enabled"), "enabled");
    public static final Key<Value<InventorySnapshot>> INVENTORY_SNAPSHOT =
            makeValueKey(InventorySnapshot.class, DataQuery.of("InventorySnapshot"), "inventory_snapshot");
    public static final Key<Value<Boolean>> TRIGGERED =
            makeValueKey(Boolean.class, DataQuery.of("Triggered"), "triggered");
    public static final Key<Value<PotionType>> POTION_TYPE =
            makeValueKey(PotionType.class, DataQuery.of("PotionType"), "potion_type");
    public static final Key<Value<Boolean>> EXPLODE =
            makeValueKey(Boolean.class, DataQuery.of("Explode"), "explode");
    public static final Key<Value<CarriedInventory<? extends Carrier>>> ITEM_INVENTORY =
            makeValueKey(new TypeToken<CarriedInventory<?>>() {}, DataQuery.of("ItemInventory"), "item_inventory");
    public static final Key<Value<Boolean>> HOLDS_POPPY =
            makeValueKey(Boolean.class, DataQuery.of("HoldsPoppy"), "holds_poppy");
    public static final Key<Value<Boolean>> HAS_PUMPKIN_HEAD =
            makeValueKey(Boolean.class, DataQuery.of("HasPumpkinHead"), "has_pumpkin_head");
    public static final Key<Value<Boolean>> IS_HANGING =
            makeValueKey(Boolean.class, DataQuery.of("IsHanging"), "is_hanging");
    public static final Key<Value<Boolean>> ELYTRA_SPEED_BOOST =
            makeValueKey(Boolean.class, DataQuery.of("ElytraSpeedBoost"), "elytra_speed_boost");
    public static final Key<Value<Double>> ELYTRA_GLIDE_SPEED =
            makeValueKey(Double.class, DataQuery.of("ElytraGlideSpeed"), "elytra_glide_speed");
    public static final Key<Value<Boolean>> SUPER_STEVE =
            makeValueKey(Boolean.class, DataQuery.of("SuperSteve"), "super_steve");
    public static final Key<Value<Boolean>> CAN_WALL_JUMP =
            makeValueKey(Boolean.class, DataQuery.of("CanWallJump"), "can_wall_jump");
    public static final Key<Value<Boolean>> CAN_DUAL_WIELD =
            makeValueKey(Boolean.class, DataQuery.of("CanDualWield"), "can_dual_wield");
    public static final Key<OptionalValue<HandType>> ACTIVE_HAND =
            makeOptionalKey(HandType.class, DataQuery.of("ActiveHand"), "active_hand");
    public static final Key<MutableBoundedValue<Double>> MAX_EXHAUSTION =
            makeMutableBoundedValueKey(Double.class, DataQuery.of("MaxExhaustion"), "max_exhaustion");
    public static final Key<MutableBoundedValue<Integer>> MAX_FOOD_LEVEL =
            makeMutableBoundedValueKey(Integer.class, DataQuery.of("MaxFoodLevel"), "max_food_level");
    public static final Key<Value<Boolean>> RECIPE_BOOK_GUI_OPEN =
            makeValueKey(Boolean.class, DataQuery.of("RecipeBookGUIOpen"), "recipe_book_gui_open");
    public static final Key<Value<Boolean>> RECIPE_BOOK_FILTER_ACTIVE =
            makeValueKey(Boolean.class, DataQuery.of("RecipeBookFilterActive"), "recipe_book_filter_active");
    public static final Key<ListValue<Accessory>> ACCESSORIES =
            makeListKey(Accessory.class, DataQuery.of("Accessories"), "accessories");
    public static final Key<OptionalValue<AdvancementTree>> OPEN_ADVANCEMENT_TREE =
            makeOptionalKey(AdvancementTree.class, DataQuery.of("OpenAdvancementTree"), "open_advancement_tree");
    public static final Key<Value<Boolean>> ARE_PLAYING =
            makeValueKey(Boolean.class, DataQuery.of("ArePlaying"), "are_playing");
    public static final Key<Value<Boolean>> HAS_RECORD =
            makeValueKey(Boolean.class, DataQuery.of("HasRecord"), "has_record");
    public static final Key<Value<Double>> FIELD_OF_VIEW_MODIFIER =
            makeValueKey(Double.class, DataQuery.of("FieldOfViewModifier"), "field_of_view_modifier");
    public static final Key<Value<Boolean>> HAS_CHEST =
            makeValueKey(Boolean.class, DataQuery.of("HasChest"), "has_chest");
    public static final Key<Value<Integer>> FINE_ROTATION =
            makeValueKey(Integer.class, DataQuery.of("FineRotation"), "fine_rotation");

    public static final Key<Value<Boolean>> DUMMY =
            makeValueKey(Boolean.class, DataQuery.of("Dummy"), "dummy");

    private LanternKeys() {
    }
}
