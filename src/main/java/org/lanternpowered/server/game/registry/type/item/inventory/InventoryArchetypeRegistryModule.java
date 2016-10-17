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
package org.lanternpowered.server.game.registry.type.item.inventory;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.lanternpowered.server.inventory.LanternInventoryArchetypeBuilder;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.AcceptsItems;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;

public class InventoryArchetypeRegistryModule extends PluginCatalogRegistryModule<InventoryArchetype> {

    public InventoryArchetypeRegistryModule() {
        super(InventoryArchetypes.class);
    }

    @Override
    public void registerDefaults() {
        final LanternInventoryArchetypeBuilder builder = new LanternInventoryArchetypeBuilder();
        builder.property(InventoryDimension.PROPERTY_NAM, new InventoryDimension(1, 1));

        final InventoryArchetype slotArchetype = builder.build("minecraft:slot", "Slot");
        this.register(slotArchetype);

        // Differences from the sponge impl, slot indexes are
        // assigned in the order they are added.

        builder.reset();
        for (int i = 0; i < 9; i++) {
            builder.with(new LanternInventoryArchetypeBuilder()
                    .from(slotArchetype)
                    .build("minecraft:slot_" + i, "Slot"));
        }

        final InventoryArchetype menuRowArchetype = builder.property(new InventoryDimension(9, 1))
                .build("sponge:menu_row", "Menu Row");
        this.register(menuRowArchetype);

        final InventoryArchetype menuGridArchetype = builder.reset()
                .with(menuRowArchetype)
                .with(menuRowArchetype)
                .with(menuRowArchetype)
                .property(new InventoryDimension(9, 3))
                .build("sponge:menu_grid", "Menu Grid");
        this.register(menuGridArchetype);

        final InventoryArchetype chestArchetype = builder.reset()
                .with(menuGridArchetype)
                .property(InventoryTitle.of(t("container.chest")))
                .build("minecraft:chest", "Chest");
        this.register(chestArchetype);

        final InventoryArchetype doubleChestArchetype = builder.reset()
                .with(chestArchetype)
                .with(chestArchetype)
                .property(new InventoryDimension(9, 6))
                .property(InventoryTitle.of(t("container.chestDouble")))
                .build("minecraft:double_chest", "Double Chest");
        this.register(doubleChestArchetype);

        final InventoryArchetype furnaceArchetype = builder.reset()
                .with(new LanternInventoryArchetypeBuilder()
                        .from(slotArchetype)
                        .build("minecraft:furnace_input", "Furnace Input"))
                .with(new LanternInventoryArchetypeBuilder()
                        .from(slotArchetype)
                        .property(AcceptsItems.of(/*fuelsPredicate?*/))
                        .build("minecraft:furnace_fuel", "Furnace Fuel"))
                .with(new LanternInventoryArchetypeBuilder()
                        .from(slotArchetype)
                        .property(AcceptsItems.of())
                        .build("minecraft:furnace_output", "Furnace Output"))
                .property(new InventoryTitle(t("container.furnace")))
                .property(new InventoryDimension(3, 1))
                .build("minecraft:furnace", "Furnace");
        this.register(furnaceArchetype);

        final InventoryArchetype dispenserArchetype = builder.reset()
                .with(menuGridArchetype)
                .property(new InventoryDimension(3, 3))
                .property(InventoryTitle.of(t("container.dispenser")))
                .build("minecraft:dispenser", "Dispenser");
        this.register(dispenserArchetype);

        final InventoryArchetype workbenchArchetype = builder.reset()
                .with(new LanternInventoryArchetypeBuilder()
                        .from(menuGridArchetype)
                        .property(new InventoryDimension(3, 3))
                        .build("minecraft:workbench_grid", "Workbench Grid"))
                .with(slotArchetype)
                .property(InventoryTitle.of(t("container.crafting")))
                .build("minecraft:workbench", "Workbench");
        this.register(workbenchArchetype);

        final InventoryArchetype brewingStandArchetype = builder.reset()
                .with(menuRowArchetype)
                .property(new InventoryDimension(5, 1))
                .property(InventoryTitle.of(t("container.brewing")))
                .build("minecraft:brewing_stand", "Brewing Stand");
        this.register(brewingStandArchetype);

        final InventoryArchetype hopperArchetype = builder.reset()
                .with(menuRowArchetype)
                .property(new InventoryDimension(5, 1))
                .property(InventoryTitle.of(t("container.hopper")))
                .build("minecraft:hopper", "Hopper");
        this.register(hopperArchetype);

        final InventoryArchetype beaconArchetype = builder.reset()
                .with(slotArchetype)
                .property(new InventoryDimension(1, 1))
                .property(InventoryTitle.of(t("container.beacon")))
                .build("minecraft:beacon", "Beacon");
        this.register(beaconArchetype);

        final InventoryArchetype enchantingTableArchetype = builder.reset()
                .with(slotArchetype)
                .with(slotArchetype)
                .property(new InventoryDimension(2, 1))
                .property(InventoryTitle.of(t("container.enchant")))
                .build("minecraft:enchanting_table", "Enchanting Table");
        this.register(enchantingTableArchetype);

        final InventoryArchetype anvilArchetype = builder.reset()
                .with(slotArchetype)
                .with(slotArchetype)
                .with(slotArchetype)
                .property(new InventoryDimension(3, 1))
                .property(InventoryTitle.of(t("container.repair")))
                .build("minecraft:anvil", "Anvil");
        this.register(anvilArchetype);

        final InventoryArchetype villagerArchetype = builder.reset()
                .with(slotArchetype)
                .with(slotArchetype)
                .with(slotArchetype)
                .property(new InventoryDimension(3, 1))
                .build("minecraft:villager", "Villager");
        this.register(villagerArchetype);

        final InventoryArchetype horseArchetype = builder.reset()
                .with(slotArchetype)
                .with(slotArchetype)
                .property(new InventoryDimension(2, 1))
                .build("minecraft:horse", "Horse");
        this.register(horseArchetype);
    }
}
