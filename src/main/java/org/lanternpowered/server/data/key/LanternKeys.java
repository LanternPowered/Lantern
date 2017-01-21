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

import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMutableBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeOptionalKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeValueKey;

import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.lanternpowered.server.effect.potion.PotionType;
import org.lanternpowered.server.inventory.InventorySnapshot;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

public final class LanternKeys {

    public static final Key<Value<Boolean>> INVULNERABLE =
            makeValueKey(Boolean.class, DataQuery.of("Invulnerable"), "lantern:invulnerability");
    public static final Key<Value<Integer>> PORTAL_COOLDOWN_TICKS =
            makeValueKey(Integer.class, DataQuery.of("PortalCooldownTicks"), "lantern:portal_cooldown_ticks");
    public static final Key<Value<Integer>> SCORE =
            makeValueKey(Integer.class, DataQuery.of("Score"), "lantern:score");
    public static final Key<MutableBoundedValue<Double>> ABSORPTION_AMOUNT =
            makeMutableBoundedValueKey(Double.class, DataQuery.of("AbsorptionAmount"), "lantern:absorption_amount");
    public static final Key<Value<Boolean>> CAN_PICK_UP_LOOT =
            makeValueKey(Boolean.class, DataQuery.of("CanPickupLoot"), "lantern:can_pickup_loot");
    public static final Key<Value<Boolean>> IS_EFFECT =
            makeValueKey(Boolean.class, DataQuery.of("IsEffect"), "lantern:is_effect");
    public static final Key<Value<Boolean>> IS_BABY =
            makeValueKey(Boolean.class, DataQuery.of("IsBaby"), "lantern:is_baby");
    public static final Key<Value<Boolean>> ARE_HANDS_UP =
            makeValueKey(Boolean.class, DataQuery.of("AreHandsUp"), "lantern:are_hands_up");
    public static final Key<Value<Integer>> ARROWS_IN_ENTITY =
            makeValueKey(Integer.class, DataQuery.of("ArrowsInEntity"), "lantern:arrows_in_entity");
    public static final Key<Value<Boolean>> IS_CONVERTING =
            makeValueKey(Boolean.class, DataQuery.of("IsConverting"), "lantern:is_converting");
    public static final Key<SetValue<SkinPart>> DISPLAYED_SKIN_PARTS =
            makeSetKey(SkinPart.class, DataQuery.of("DisplayedSkinParts"), "lantern:displayed_skin_parts");
    public static final Key<Value<Double>> GRAVITY_FACTOR =
            makeValueKey(Double.class, DataQuery.of("GravityFactor"), "lantern:gravity_factor");
    public static final Key<Value<LanternDoorHalf>> DOOR_HALF =
            makeValueKey(LanternDoorHalf.class, DataQuery.of("DoorHalf"), "lantern:door_half");
    public static final Key<Value<Boolean>> CHECK_DECAY =
            makeValueKey(Boolean.class, DataQuery.of("CheckDecay"), "lantern:check_decay");
    public static final Key<Value<LanternBedPart>> BED_PART =
            makeValueKey(LanternBedPart.class, DataQuery.of("BedPart"), "lantern:bed_part");
    public static final Key<Value<Boolean>> ENABLED =
            makeValueKey(Boolean.class, DataQuery.of("Enabled"), "lantern:enabled");
    public static final Key<Value<InventorySnapshot>> INVENTORY_SNAPSHOT =
            makeValueKey(InventorySnapshot.class, DataQuery.of("InventorySnapshot"), "lantern:inventory_snapshot");
    public static final Key<Value<Boolean>> TRIGGERED =
            makeValueKey(Boolean.class, DataQuery.of("Triggered"), "lantern:triggered");
    public static final Key<Value<Boolean>> IS_ELYTRA_FLYING =
            makeValueKey(Boolean.class, DataQuery.of("IsElytraFlying"), "lantern:is_elytra_flying");
    public static final Key<Value<PotionType>> POTION_TYPE =
            makeValueKey(PotionType.class, DataQuery.of("PotionType"), "lantern:potion_type");
    public static final Key<Value<Boolean>> EXPLODE =
            makeValueKey(Boolean.class, DataQuery.of("Explode"), "lantern:explode");
    public static final Key<Value<CarriedInventory<? extends Carrier>>> ITEM_INVENTORY =
            makeValueKey(new TypeToken<CarriedInventory<?>>() {}, DataQuery.of("ItemInventory"), "lantern:item_inventory");
    public static final Key<Value<Boolean>> HOLDS_POPPY =
            makeValueKey(Boolean.class, DataQuery.of("HoldsPoppy"), "lantern:holds_poppy");
    public static final Key<Value<Boolean>> HAS_PUMPKIN_HEAD =
            makeValueKey(Boolean.class, DataQuery.of("HasPumpkinHead"), "lantern:has_pumpkin_head");
    public static final Key<Value<Boolean>> IS_HANGING =
            makeValueKey(Boolean.class, DataQuery.of("IsHanging"), "lantern:is_hanging");
    public static final Key<Value<Boolean>> ELYTRA_SPEED_BOOST =
            makeValueKey(Boolean.class, DataQuery.of("ElytraSpeedBoost"), "lantern:elytra_speed_boost");
    public static final Key<Value<Double>> ELYTRA_GLIDE_SPEED =
            makeValueKey(Double.class, DataQuery.of("ElytraGlideSpeed"), "lantern:elytra_glide_speed");
    public static final Key<Value<Boolean>> SUPER_STEVE =
            makeValueKey(Boolean.class, DataQuery.of("SuperSteve"), "lantern:super_steve");
    public static final Key<Value<Boolean>> CAN_WALL_JUMP =
            makeValueKey(Boolean.class, DataQuery.of("CanWallJump"), "lantern:can_wall_jump");
    public static final Key<Value<Boolean>> CAN_DUAL_WIELD =
            makeValueKey(Boolean.class, DataQuery.of("CanDualWield"), "lantern:can_dual_wield");
    public static final Key<OptionalValue<HandType>> ACTIVE_HAND =
            makeOptionalKey(HandType.class, DataQuery.of("ActiveHand"), "lantern:active_hand");
    public static final Key<MutableBoundedValue<Integer>> MAX_FOOD_LEVEL =
            makeMutableBoundedValueKey(Integer.class, DataQuery.of("MaxFoodLevel"), "lantern:max_food_level");
    public static final Key<MutableBoundedValue<Double>> MAX_SATURATION =
            makeMutableBoundedValueKey(Double.class, DataQuery.of("MaxSaturation"), "lantern:max_saturation");

    private LanternKeys() {
    }
}
