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
package org.lanternpowered.server.inventory.vanilla;

import static org.lanternpowered.server.plugin.InternalPluginsInfo.Minecraft;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.CarrierBasedTransformer;
import org.lanternpowered.server.inventory.LanternInventoryArchetype;
import org.lanternpowered.server.inventory.behavior.SimpleContainerShiftClickBehavior;
import org.lanternpowered.server.inventory.filter.ItemFilter;
import org.lanternpowered.server.inventory.type.LanternArmorEquipableInventory;
import org.lanternpowered.server.inventory.type.LanternCraftingGridInventory;
import org.lanternpowered.server.inventory.type.LanternCraftingInventory;
import org.lanternpowered.server.inventory.type.LanternGridInventory;
import org.lanternpowered.server.inventory.type.LanternOrderedInventory;
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
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.AcceptsItems;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.property.GuiIdProperty;
import org.spongepowered.api.item.inventory.property.GuiIds;

import java.util.function.Supplier;

import javax.annotation.Nullable;

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

    public static final LanternInventoryArchetype<LanternMainPlayerInventory> PLAYER_MAIN;

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

    public static final LanternInventoryArchetype<LanternPlayerEquipmentInventory> PLAYER_ARMOR;

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

    public static final LanternInventoryArchetype<LanternOrderedInventory> HORSE;

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
                .buildArchetype(Minecraft.IDENTIFIER, "slot");

        //////////////////
        /// Input Slot ///
        //////////////////

        INPUT_SLOT = AbstractSlot.builder()
                .type(LanternInputSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "input_slot");

        ///////////////////
        /// Output Slot ///
        ///////////////////

        OUTPUT_SLOT = AbstractSlot.builder()
                .type(LanternOutputSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "output_slot");

        /////////////////
        /// Fuel Slot ///
        /////////////////

        FUEL_SLOT = AbstractSlot.builder()
                .filter(ItemFilter.ofStackPredicate(stack ->
                        Lantern.getRegistry().getFuelRegistry().findMatching(stack.createSnapshot()).isPresent()))
                .type(LanternFuelSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "fuel_slot");

        ////////////////////////////
        /// Crafting Output Slot ///
        ////////////////////////////

        CRAFTING_OUTPUT_SLOT = AbstractSlot.builder()
                .type(LanternCraftingOutputSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "crafting_output_slot");

        ///////////////////
        /// Helmet Slot ///
        ///////////////////

        HELMET_SLOT = AbstractSlot.builder()
                .property(EquipmentSlotType.of(EquipmentTypes.HEADWEAR))
                .type(LanternEquipmentSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "helmet_slot");

        ///////////////////////
        /// Chestplate Slot ///
        ///////////////////////

        CHESTPLATE_SLOT = AbstractSlot.builder()
                .property(EquipmentSlotType.of(EquipmentTypes.CHESTPLATE))
                .type(LanternEquipmentSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "chestplate_slot");

        /////////////////////
        /// Leggings Slot ///
        /////////////////////

        LEGGINGS_SLOT = AbstractSlot.builder()
                .property(EquipmentSlotType.of(EquipmentTypes.LEGGINGS))
                .type(LanternEquipmentSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "leggings_slot");

        //////////////////
        /// Boots Slot ///
        //////////////////

        BOOTS_SLOT = AbstractSlot.builder()
                .property(EquipmentSlotType.of(EquipmentTypes.BOOTS))
                .type(LanternEquipmentSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "boots_slot");

        /////////////////////
        /// Mainhand Slot ///
        /////////////////////

        MAIN_HAND_SLOT = AbstractSlot.builder()
                .property(EquipmentSlotType.of(EquipmentTypes.MAIN_HAND))
                .type(LanternUnrestrictedEquipmentSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "main_hand_slot");

        ////////////////////
        /// Offhand Slot ///
        ////////////////////

        OFF_HAND_SLOT = AbstractSlot.builder()
                .property(EquipmentSlotType.of(EquipmentTypes.OFF_HAND))
                .type(LanternUnrestrictedEquipmentSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "off_hand_slot");

        //////////////
        /// Chests ///
        //////////////

        final AbstractGridInventory.SlotsBuilder<ChestInventory> chestBuilder = AbstractGridInventory.slotsBuilder()
                .expand(9, 3)
                .shiftClickBehavior(SimpleContainerShiftClickBehavior.INSTANCE)
                .type(ChestInventory.class);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                chestBuilder.slot(x, y, SLOT);
            }
        }
        CHEST = chestBuilder
                .title(tr("container.chest"))
                .property(new GuiIdProperty(GuiIds.CHEST))
                .buildArchetype(Minecraft.IDENTIFIER, "chest");
        SHULKER_BOX = chestBuilder
                .title(tr("container.shulkerBox"))
                .property(new GuiIdProperty(GuiIds.SHULKER_BOX))
                .buildArchetype(Minecraft.IDENTIFIER, "shulker_box");
        ENDER_CHEST = chestBuilder
                .title(tr("container.enderchest"))
                .property(new GuiIdProperty(GuiIds.CHEST))
                .buildArchetype(Minecraft.IDENTIFIER, "ender_chest");

        ////////////////////
        /// Double Chest ///
        ////////////////////

        DOUBLE_CHEST = AbstractGridInventory.rowsBuilder()
                .title(tr("container.chestDouble"))
                .grid(0, CHEST)
                .grid(3, CHEST)
                .shiftClickBehavior(SimpleContainerShiftClickBehavior.INSTANCE)
                .property(new GuiIdProperty(GuiIds.CHEST))
                .type(ChestInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "double_chest");

        /////////////////
        /// Dispenser ///
        /////////////////

        final AbstractGridInventory.SlotsBuilder<DispenserInventory> dispenserBuilder = AbstractGridInventory.slotsBuilder()
                .title(tr("container.dispenser"))
                .shiftClickBehavior(SimpleContainerShiftClickBehavior.INSTANCE)
                .property(new GuiIdProperty(GuiIds.DISPENSER))
                .type(DispenserInventory.class)
                .expand(3, 3);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                dispenserBuilder.slot(x, y, SLOT);
            }
        }
        DISPENSER = dispenserBuilder.buildArchetype(Minecraft.IDENTIFIER, "dispenser");

        ///////////////
        /// Jukebox ///
        ///////////////

        JUKEBOX = AbstractSlot.builder()
                .type(JukeboxInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "jukebox");

        ///////////////
        /// Furnace ///
        ///////////////

        FURNACE = AbstractOrderedInventory.builder()
                .title(tr("container.furnace"))
                .addLast(INPUT_SLOT)
                .addLast(FUEL_SLOT)
                .addLast(OUTPUT_SLOT)
                .shiftClickBehavior(FurnaceShiftClickBehavior.INSTANCE)
                .property(new GuiIdProperty(GuiIds.FURNACE))
                .type(FurnaceInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "furnace");

        ////////////////////////
        /// Entity Equipment ///
        ////////////////////////

        ENTITY_EQUIPMENT = AbstractOrderedInventory.builder()
                .addLast(MAIN_HAND_SLOT)
                .addLast(OFF_HAND_SLOT)
                .addLast(HELMET_SLOT)
                .addLast(CHESTPLATE_SLOT)
                .addLast(LEGGINGS_SLOT)
                .addLast(BOOTS_SLOT)
                .type(LanternArmorEquipableInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "entity_equipment");

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
        PLAYER_MAIN_GRID = gridBuilder.buildArchetype(Minecraft.IDENTIFIER, "player_main_grid");

        /////////////////////
        /// Player Hotbar ///
        /////////////////////

        final AbstractOrderedInventory.Builder<LanternHotbarInventory> hotbarBuilder = AbstractOrderedInventory.builder()
                .type(LanternHotbarInventory.class);
        for (int x = 0; x < 9; x++) {
            hotbarBuilder.addLast(SLOT);
        }
        PLAYER_HOTBAR = hotbarBuilder.buildArchetype(Minecraft.IDENTIFIER, "player_hotbar");

        ///////////////////
        /// Player Main ///
        ///////////////////

        PLAYER_MAIN = AbstractGridInventory.rowsBuilder()
                .grid(0, PLAYER_MAIN_GRID)
                .row(3, PLAYER_HOTBAR, 1050)
                .type(LanternMainPlayerInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "player_main");

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
        CRAFTING_GRID = craftingGridBuilder.buildArchetype(Minecraft.IDENTIFIER, "crafting_grid");

        ////////////////
        /// Crafting ///
        ////////////////

        CRAFTING = AbstractOrderedInventory.builder()
                .addLast(CRAFTING_OUTPUT_SLOT)
                .addLast(CRAFTING_GRID)
                .type(LanternCraftingInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "crafting");

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
        CRAFTING_TABLE_GRID = workbenchGridBuilder.buildArchetype(Minecraft.IDENTIFIER, "crafting_table_grid");

        /////////////////
        /// Workbench ///
        /////////////////

        CRAFTING_TABLE = AbstractOrderedInventory.builder()
                .addLast(CRAFTING_OUTPUT_SLOT)
                .addLast(CRAFTING_TABLE_GRID)
                .property(new GuiIdProperty(GuiIds.CRAFTING_TABLE))
                .type(CraftingTableInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "crafting_table");

        ////////////////////
        /// Player Armor ///
        ////////////////////

        PLAYER_ARMOR = AbstractOrderedInventory.builder()
                .addLast(HELMET_SLOT)
                .addLast(CHESTPLATE_SLOT)
                .addLast(LEGGINGS_SLOT)
                .addLast(BOOTS_SLOT)
                .type(LanternPlayerEquipmentInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "player_armor");

        ///////////////////////
        /// Player and User ///
        ///////////////////////

        final AbstractOrderedInventory.Builder<LanternOrderedInventory> userInventoryBuilder =
                AbstractOrderedInventory.builder()
                        .addLast(PLAYER_ARMOR)
                        .addLast(OFF_HAND_SLOT)
                        .addLast(PLAYER_MAIN);
        USER = userInventoryBuilder
                .type(LanternUserInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "user");
        PLAYER = userInventoryBuilder
                .type(LanternPlayerInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "player");

        ///////////////////
        /// Saddle Slot ///
        ///////////////////

        SADDLE_SLOT = AbstractSlot.builder()
                .type(LanternFilteringSlot.class)
                .property(new AcceptsItems(ImmutableList.of(ItemTypes.SADDLE)))
                .buildArchetype(Minecraft.IDENTIFIER, "saddle_slot");

        ////////////////////////
        /// Horse Armor Slot ///
        ////////////////////////

        HORSE_ARMOR_SLOT = AbstractSlot.builder()
                .type(LanternFilteringSlot.class)
                .property(new AcceptsItems(ImmutableList.of(
                        ItemTypes.DIAMOND_HORSE_ARMOR, ItemTypes.GOLDEN_HORSE_ARMOR, ItemTypes.IRON_HORSE_ARMOR)))
                .buildArchetype(Minecraft.IDENTIFIER, "horse_armor_slot");

        /////////////////
        /// Null Slot ///
        /////////////////

        NULL_SLOT = AbstractSlot.builder()
                .type(NullSlot.class)
                .buildArchetype(Minecraft.IDENTIFIER, "null_slot");

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
                .buildArchetype(Minecraft.IDENTIFIER, "donkey_mule_chest");

        ///////////////////////
        /// Horse Equipment ///
        ///////////////////////

        HORSE = AbstractOrderedInventory.builder()
                .carrierBased(new HorseCarrierBasedTransformer())
                .type(LanternOrderedInventory.class)
                .buildArchetype(Minecraft.IDENTIFIER, "horse");

    }

    private VanillaInventoryArchetypes() {
    }
}
