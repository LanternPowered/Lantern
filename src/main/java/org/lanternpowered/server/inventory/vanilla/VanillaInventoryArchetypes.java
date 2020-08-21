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
package org.lanternpowered.server.inventory.vanilla;

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.api.NamespacedKeys;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.LanternInventoryProperties;
import org.lanternpowered.server.inventory.behavior.SimpleTopBottomShiftClickBehavior;
import org.lanternpowered.server.inventory.type.LanternArmorEquipableInventory;
import org.lanternpowered.server.inventory.type.LanternChildrenInventory;
import org.lanternpowered.server.inventory.type.LanternCraftingGridInventory;
import org.lanternpowered.server.inventory.type.LanternCraftingInventory;
import org.lanternpowered.server.inventory.type.LanternGridInventory;
import org.lanternpowered.server.inventory.type.slot.LanternCraftingOutputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternEquipmentSlot;
import org.lanternpowered.server.inventory.type.slot.LanternFilteringSlot;
import org.lanternpowered.server.inventory.type.slot.LanternFuelSlot;
import org.lanternpowered.server.inventory.type.slot.LanternInputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternOutputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.lanternpowered.server.inventory.type.slot.NullSlot;
import org.lanternpowered.server.inventory.vanilla.block.ChestInventory;
import org.lanternpowered.server.inventory.vanilla.block.CraftingTableInventory;
import org.lanternpowered.server.inventory.vanilla.block.DispenserInventory;
import org.lanternpowered.server.inventory.vanilla.block.FurnaceInventory;
import org.lanternpowered.server.inventory.vanilla.block.FurnaceShiftClickBehavior;
import org.lanternpowered.server.inventory.vanilla.block.JukeboxInventory;
import org.lanternpowered.server.item.predicate.ItemPredicate;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.gui.GuiIds;

import java.util.Set;

public final class VanillaInventoryArchetypes {

    ////////////////////
    /// Default Slot ///
    ////////////////////

    public static final LanternInventoryArchetype<LanternSlot> SLOT;

    //////////////////
    /// Input Slot ///
    //////////////////

    public static final LanternInventoryArchetype<LanternInputSlot> INPUT_SLOT;

    ///////////////////
    /// Output Slot ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternOutputSlot> OUTPUT_SLOT;

    /////////////////
    /// Fuel Slot ///
    /////////////////

    public static final LanternInventoryArchetype<LanternFuelSlot> FUEL_SLOT;

    ////////////////////////////
    /// Crafting Output Slot ///
    ////////////////////////////

    public static final LanternInventoryArchetype<LanternCraftingOutputSlot> CRAFTING_OUTPUT_SLOT;

    ///////////////////
    /// Helmet Slot ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> HELMET_SLOT;

    ///////////////////////
    /// Chestplate Slot ///
    ///////////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> CHESTPLATE_SLOT;

    /////////////////////
    /// Leggings Slot ///
    /////////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> LEGGINGS_SLOT;

    //////////////////
    /// Boots Slot ///
    //////////////////

    public static final LanternInventoryArchetype<LanternEquipmentSlot> BOOTS_SLOT;

    /////////////////////
    /// Mainhand Slot ///
    /////////////////////

    public static final LanternInventoryArchetype<LanternUnrestrictedEquipmentSlot> MAIN_HAND_SLOT;

    ////////////////////
    /// Offhand Slot ///
    ////////////////////

    public static final LanternInventoryArchetype<LanternUnrestrictedEquipmentSlot> OFF_HAND_SLOT;

    /////////////
    /// Chest ///
    /////////////

    public static final LanternInventoryArchetype<ChestInventory> CHEST;

    ///////////////////
    /// Shulker Box ///
    ///////////////////

    public static final LanternInventoryArchetype<ChestInventory> SHULKER_BOX;

    ///////////////////
    /// Ender Chest ///
    ///////////////////

    public static final LanternInventoryArchetype<ChestInventory> ENDER_CHEST;

    ////////////////////
    /// Double Chest ///
    ////////////////////

    public static final LanternInventoryArchetype<ChestInventory> DOUBLE_CHEST;

    /////////////////
    /// Dispenser ///
    /////////////////

    public static final LanternInventoryArchetype<DispenserInventory> DISPENSER;

    ///////////////
    /// Jukebox ///
    ///////////////

    public static final LanternInventoryArchetype<JukeboxInventory> JUKEBOX;

    ///////////////
    /// Furnace ///
    ///////////////

    public static final LanternInventoryArchetype<FurnaceInventory> FURNACE;

    ////////////////////////
    /// Entity Equipment ///
    ////////////////////////

    public static final LanternInventoryArchetype<LanternArmorEquipableInventory> ENTITY_EQUIPMENT;

    ////////////////////////
    /// Player Main Grid ///
    ////////////////////////

    public static final LanternInventoryArchetype<LanternGridInventory> PLAYER_MAIN_GRID;

    /////////////////////
    /// Player Hotbar ///
    /////////////////////

    public static final LanternInventoryArchetype<LanternHotbarInventory> PLAYER_HOTBAR;

    ///////////////////
    /// Player Main ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternPrimaryPlayerInventory> PLAYER_MAIN;

    /////////////////////
    /// Crafting Grid ///
    /////////////////////

    public static final LanternInventoryArchetype<LanternCraftingGridInventory> CRAFTING_GRID;

    ////////////////
    /// Crafting ///
    ////////////////

    public static final LanternInventoryArchetype<LanternCraftingInventory> CRAFTING;

    //////////////////////
    /// Workbench Grid ///
    //////////////////////

    public static final LanternInventoryArchetype<LanternCraftingGridInventory> CRAFTING_TABLE_GRID;

    /////////////////
    /// Workbench ///
    /////////////////

    public static final LanternInventoryArchetype<CraftingTableInventory> CRAFTING_TABLE;

    ////////////////////
    /// Player Armor ///
    ////////////////////

    public static final LanternInventoryArchetype<LanternPlayerArmorInventory> PLAYER_ARMOR;

    //////////////
    /// User ///
    //////////////

    public static final LanternInventoryArchetype<LanternUserInventory> USER;

    //////////////
    /// Player ///
    //////////////

    public static final LanternInventoryArchetype<LanternPlayerInventory> PLAYER;

    ///////////////////////
    /// Horse Inventory ///
    ///////////////////////

    public static final LanternInventoryArchetype<LanternChildrenInventory> HORSE;

    ///////////////////
    /// Saddle Slot ///
    ///////////////////

    public static final LanternInventoryArchetype<LanternFilteringSlot> SADDLE_SLOT;

    ////////////////////////
    /// Horse Armor Slot ///
    ////////////////////////

    public static final LanternInventoryArchetype<LanternFilteringSlot> HORSE_ARMOR_SLOT;

    /////////////////
    /// Null Slot ///
    /////////////////

    public static final LanternInventoryArchetype<NullSlot> NULL_SLOT;

    /////////////////////////
    /// Donkey/Mule Chest ///
    /////////////////////////

    public static final LanternInventoryArchetype<LanternGridInventory> DONKEY_MULE_CHEST;

    static {
        ////////////////////
        /// Default Slot ///
        ////////////////////

        SLOT = AbstractSlot.builder()
                .type(LanternSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("slot"));

        //////////////////
        /// Input Slot ///
        //////////////////

        INPUT_SLOT = AbstractSlot.builder()
                .type(LanternInputSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("input_slot"));

        ///////////////////
        /// Output Slot ///
        ///////////////////

        OUTPUT_SLOT = AbstractSlot.builder()
                .type(LanternOutputSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("output_slot"));

        /////////////////
        /// Fuel Slot ///
        /////////////////

        FUEL_SLOT = AbstractSlot.builder()
                .filter(ItemPredicate.ofStackPredicate(stack ->
                        Lantern.getRegistry().getFuelRegistry().findMatching(stack.createSnapshot()).isPresent()))
                .type(LanternFuelSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("fuel_slot"));

        ////////////////////////////
        /// Crafting Output Slot ///
        ////////////////////////////

        CRAFTING_OUTPUT_SLOT = AbstractSlot.builder()
                .type(LanternCraftingOutputSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("crafting_output_slot"));

        ///////////////////
        /// Helmet Slot ///
        ///////////////////

        HELMET_SLOT = AbstractSlot.builder()
                .property(InventoryProperties.EQUIPMENT_TYPE, EquipmentTypes.HEADWEAR)
                .type(LanternEquipmentSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("helmet_slot"));

        ///////////////////////
        /// Chestplate Slot ///
        ///////////////////////

        CHESTPLATE_SLOT = AbstractSlot.builder()
                .property(InventoryProperties.EQUIPMENT_TYPE, EquipmentTypes.CHESTPLATE)
                .type(LanternEquipmentSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("chestplate_slot"));

        /////////////////////
        /// Leggings Slot ///
        /////////////////////

        LEGGINGS_SLOT = AbstractSlot.builder()
                .property(InventoryProperties.EQUIPMENT_TYPE, EquipmentTypes.LEGGINGS)
                .type(LanternEquipmentSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("leggings_slot"));

        //////////////////
        /// Boots Slot ///
        //////////////////

        BOOTS_SLOT = AbstractSlot.builder()
                .property(InventoryProperties.EQUIPMENT_TYPE, EquipmentTypes.BOOTS)
                .type(LanternEquipmentSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("boots_slot"));

        /////////////////////
        /// Mainhand Slot ///
        /////////////////////

        MAIN_HAND_SLOT = AbstractSlot.builder()
                .property(InventoryProperties.EQUIPMENT_TYPE, EquipmentTypes.MAIN_HAND)
                .type(LanternUnrestrictedEquipmentSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("main_hand_slot"));

        ////////////////////
        /// Offhand Slot ///
        ////////////////////

        OFF_HAND_SLOT = AbstractSlot.builder()
                .property(InventoryProperties.EQUIPMENT_TYPE, EquipmentTypes.OFF_HAND)
                .type(LanternUnrestrictedEquipmentSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("off_hand_slot"));

        //////////////
        /// Chests ///
        //////////////

        final AbstractGridInventory.SlotsBuilder<ChestInventory> chestBuilder = AbstractGridInventory.slotsBuilder()
                .expand(9, 3)
                .shiftClickBehavior(SimpleTopBottomShiftClickBehavior.INSTANCE)
                .type(ChestInventory.class);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                chestBuilder.slot(x, y, SLOT);
            }
        }
        CHEST = chestBuilder
                .title(tr("container.chest"))
                .property(InventoryProperties.GUI_ID, GuiIds.CHEST)
                .buildArchetype(NamespacedKeys.minecraft("chest"));
        SHULKER_BOX = chestBuilder
                .title(tr("container.shulkerBox"))
                .property(InventoryProperties.GUI_ID, GuiIds.SHULKER_BOX)
                .buildArchetype(NamespacedKeys.minecraft("shulker_box"));
        ENDER_CHEST = chestBuilder
                .title(tr("container.enderchest"))
                .property(InventoryProperties.GUI_ID, GuiIds.CHEST)
                .buildArchetype(NamespacedKeys.minecraft("ender_chest"));

        ////////////////////
        /// Double Chest ///
        ////////////////////

        DOUBLE_CHEST = AbstractGridInventory.rowsBuilder()
                .title(tr("container.chestDouble"))
                .grid(0, CHEST)
                .grid(3, CHEST)
                .shiftClickBehavior(SimpleTopBottomShiftClickBehavior.INSTANCE)
                .property(InventoryProperties.GUI_ID, GuiIds.CHEST)
                .type(ChestInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("double_chest"));

        /////////////////
        /// Dispenser ///
        /////////////////

        final AbstractGridInventory.SlotsBuilder<DispenserInventory> dispenserBuilder = AbstractGridInventory.slotsBuilder()
                .title(tr("container.dispenser"))
                .shiftClickBehavior(SimpleTopBottomShiftClickBehavior.INSTANCE)
                .property(InventoryProperties.GUI_ID, GuiIds.DISPENSER)
                .type(DispenserInventory.class)
                .expand(3, 3);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                dispenserBuilder.slot(x, y, SLOT);
            }
        }
        DISPENSER = dispenserBuilder.buildArchetype(NamespacedKeys.minecraft("dispenser"));

        ///////////////
        /// Jukebox ///
        ///////////////

        JUKEBOX = AbstractSlot.builder()
                .type(JukeboxInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("jukebox"));

        ///////////////
        /// Furnace ///
        ///////////////

        FURNACE = AbstractChildrenInventory.builder()
                .title(tr("container.furnace"))
                .addLast(INPUT_SLOT)
                .addLast(FUEL_SLOT)
                .addLast(OUTPUT_SLOT)
                .shiftClickBehavior(FurnaceShiftClickBehavior.INSTANCE)
                .property(InventoryProperties.GUI_ID, GuiIds.FURNACE)
                .type(FurnaceInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("furnace"));

        ////////////////////////
        /// Entity Equipment ///
        ////////////////////////

        ENTITY_EQUIPMENT = AbstractChildrenInventory.builder()
                .addLast(MAIN_HAND_SLOT)
                .addLast(OFF_HAND_SLOT)
                .addLast(HELMET_SLOT)
                .addLast(CHESTPLATE_SLOT)
                .addLast(LEGGINGS_SLOT)
                .addLast(BOOTS_SLOT)
                .type(LanternArmorEquipableInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("entity_equipment"));

        ////////////////////////
        /// Player Main Grid ///
        ////////////////////////

        AbstractGridInventory.SlotsBuilder<LanternGridInventory> gridBuilder = AbstractGridInventory.slotsBuilder()
                .type(LanternGridInventory.class);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                gridBuilder.slot(x, y, SLOT);
            }
        }
        PLAYER_MAIN_GRID = gridBuilder.buildArchetype(NamespacedKeys.minecraft("player_main_grid"));

        /////////////////////
        /// Player Hotbar ///
        /////////////////////

        final AbstractChildrenInventory.Builder<LanternHotbarInventory> hotbarBuilder = AbstractChildrenInventory.builder()
                .type(LanternHotbarInventory.class);
        for (int x = 0; x < 9; x++) {
            hotbarBuilder.addLast(SLOT);
        }
        PLAYER_HOTBAR = hotbarBuilder.buildArchetype(NamespacedKeys.minecraft("player_hotbar"));

        ///////////////////
        /// Player Main ///
        ///////////////////

        PLAYER_MAIN = AbstractChildrenInventory.builder()
                .addLast(PLAYER_MAIN_GRID)
                .addLast(PLAYER_HOTBAR)
                .type(LanternPrimaryPlayerInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("player_main"));

        /////////////////////
        /// Crafting Grid ///
        /////////////////////

        final AbstractGridInventory.SlotsBuilder<LanternCraftingGridInventory> craftingGridBuilder = AbstractGridInventory.slotsBuilder()
                .type(LanternCraftingGridInventory.class);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                craftingGridBuilder.slot(x, y, INPUT_SLOT);
            }
        }
        CRAFTING_GRID = craftingGridBuilder.buildArchetype(NamespacedKeys.minecraft("crafting_grid"));

        ////////////////
        /// Crafting ///
        ////////////////

        CRAFTING = AbstractChildrenInventory.builder()
                .addLast(CRAFTING_OUTPUT_SLOT)
                .addLast(CRAFTING_GRID)
                .type(LanternCraftingInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("crafting"));

        //////////////////////
        /// Workbench Grid ///
        //////////////////////

        final AbstractGridInventory.SlotsBuilder<LanternCraftingGridInventory> workbenchGridBuilder = AbstractGridInventory.slotsBuilder()
                .type(LanternCraftingGridInventory.class)
                .expand(3, 3);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                workbenchGridBuilder.slot(x, y, INPUT_SLOT);
            }
        }
        CRAFTING_TABLE_GRID = workbenchGridBuilder.buildArchetype(NamespacedKeys.minecraft("crafting_table_grid"));

        /////////////////
        /// Workbench ///
        /////////////////

        CRAFTING_TABLE = AbstractChildrenInventory.builder()
                .addLast(CRAFTING_OUTPUT_SLOT)
                .addLast(CRAFTING_TABLE_GRID)
                .property(InventoryProperties.GUI_ID, GuiIds.CRAFTING_TABLE)
                .type(CraftingTableInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("crafting_table"));

        ////////////////////
        /// Player Armor ///
        ////////////////////

        PLAYER_ARMOR = AbstractChildrenInventory.builder()
                .addLast(HELMET_SLOT)
                .addLast(CHESTPLATE_SLOT)
                .addLast(LEGGINGS_SLOT)
                .addLast(BOOTS_SLOT)
                .type(LanternPlayerArmorInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("player_armor"));

        ///////////////////////
        /// Player and User ///
        ///////////////////////

        final AbstractChildrenInventory.Builder<LanternChildrenInventory> userInventoryBuilder =
                AbstractChildrenInventory.builder()
                        .addLast(PLAYER_ARMOR)
                        .addLast(OFF_HAND_SLOT)
                        .addLast(PLAYER_MAIN);
        USER = userInventoryBuilder
                .title(tr("inventory.user.name"))
                .type(LanternUserInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("user"));
        PLAYER = userInventoryBuilder
                .title(tr("inventory.player.name"))
                .type(LanternPlayerInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("player"));

        ///////////////////
        /// Saddle Slot ///
        ///////////////////

        SADDLE_SLOT = AbstractSlot.builder()
                .type(LanternFilteringSlot.class)
                .property(LanternInventoryProperties.ITEM_FILTER,
                        ItemPredicate.ofTypePredicate(itemType -> itemType == ItemTypes.SADDLE))
                .buildArchetype(NamespacedKeys.minecraft("saddle_slot"));

        ////////////////////////
        /// Horse Armor Slot ///
        ////////////////////////

        // TODO: Use tag?
        final Set<ItemType> horseArmor = ImmutableSet.of(
                ItemTypes.DIAMOND_HORSE_ARMOR, ItemTypes.GOLDEN_HORSE_ARMOR, ItemTypes.IRON_HORSE_ARMOR);

        HORSE_ARMOR_SLOT = AbstractSlot.builder()
                .type(LanternFilteringSlot.class)
                .property(LanternInventoryProperties.ITEM_FILTER, ItemPredicate.ofTypePredicate(horseArmor::contains))
                .buildArchetype(NamespacedKeys.minecraft("horse_armor_slot"));

        /////////////////
        /// Null Slot ///
        /////////////////

        NULL_SLOT = AbstractSlot.builder()
                .type(NullSlot.class)
                .buildArchetype(NamespacedKeys.minecraft("null_slot"));

        /////////////////////////////
        /// Donkey/Mule Inventory ///
        /////////////////////////////

        gridBuilder = AbstractGridInventory.slotsBuilder()
                .type(LanternGridInventory.class);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 5; x++) {
                gridBuilder.slot(x, y, SLOT);
            }
        }
        DONKEY_MULE_CHEST = gridBuilder
                .buildArchetype(NamespacedKeys.minecraft("donkey_mule_chest"));

        ///////////////////////
        /// Horse Equipment ///
        ///////////////////////

        HORSE = AbstractChildrenInventory.builder()
                .carrierBased(new HorseCarrierBasedTransformer())
                .type(LanternChildrenInventory.class)
                .buildArchetype(NamespacedKeys.minecraft("horse"));

    }

    private VanillaInventoryArchetypes() {
    }
}
