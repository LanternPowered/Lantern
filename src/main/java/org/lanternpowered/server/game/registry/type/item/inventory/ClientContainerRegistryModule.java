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

import kotlin.jvm.functions.Function1;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.client.AnvilClientContainer;
import org.lanternpowered.server.inventory.client.BeaconClientContainer;
import org.lanternpowered.server.inventory.client.BrewingStandClientContainer;
import org.lanternpowered.server.inventory.client.ChestClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainerType;
import org.lanternpowered.server.inventory.client.CraftingTableClientContainer;
import org.lanternpowered.server.inventory.client.DispenserClientContainer;
import org.lanternpowered.server.inventory.client.EnchantmentTableClientContainer;
import org.lanternpowered.server.inventory.client.EntityEquipmentClientContainer;
import org.lanternpowered.server.inventory.client.FurnaceClientContainer;
import org.lanternpowered.server.inventory.client.HopperClientContainer;
import org.lanternpowered.server.inventory.client.ShulkerBoxClientContainer;
import org.lanternpowered.server.inventory.client.TradingClientContainer;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.gui.GuiId;
import org.spongepowered.api.item.inventory.gui.GuiIds;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public class ClientContainerRegistryModule extends DefaultCatalogRegistryModule<GuiId> {

    private static final ClientContainerRegistryModule instance = new ClientContainerRegistryModule();

    public static ClientContainerRegistryModule get() {
        return instance;
    }

    private ClientContainerRegistryModule() {
        super(GuiIds.class);
    }

    @Override
    public void registerDefaults() {
        register("minecraft", "chest", inventory -> {
            final int rows = inventory.getProperty(InventoryProperties.CAPACITY)
                            .map(capacity -> (int) Math.ceil(capacity.doubleValue() / 9.0))
                            .orElse(1);
            return new ChestClientContainer(rows);
        });
        register("minecraft", "furnace", inventory -> new FurnaceClientContainer());
        register("minecraft", "dispenser", inventory -> new DispenserClientContainer());
        register("minecraft", "crafting_table", inventory -> new CraftingTableClientContainer());
        register("minecraft", "brewing_stand", inventory -> new BrewingStandClientContainer());
        register("minecraft", "hopper", inventory -> new HopperClientContainer());
        register("minecraft", "beacon", inventory -> new BeaconClientContainer());
        register("minecraft", "enchanting_table", inventory -> new EnchantmentTableClientContainer());
        register("minecraft", "anvil", inventory -> new AnvilClientContainer());
        register("minecraft", "villager", inventory -> new TradingClientContainer());
        register("minecraft", "horse", inventory -> {
            int capacity = inventory.capacity();
            capacity -= 2;
            capacity = (int) Math.ceil((float) capacity / 3);
            int entityId = -1;
            if (inventory instanceof CarriedInventory) {
                final Carrier carrier = ((CarriedInventory<Carrier>) inventory).getCarrier().orElse(null);
                if (carrier instanceof Entity) {
                    final LanternEntity entity = (LanternEntity) carrier;
                    entityId = entity.getWorld().getEntityProtocolManager().getProtocolId(entity);
                }
            }
            if (entityId == -1) {
                throw new IllegalStateException("Invalid carrier entity to create a container.");
            }
            // TODO: Dummy entity support?
            return new EntityEquipmentClientContainer(capacity, entityId);
        });
        register("minecraft", "shulker_box", inventory -> new ShulkerBoxClientContainer());
    }

    private void register(String plugin, String id, Function1<AbstractInventory, ClientContainer> supplier) {
        register(new ClientContainerType(CatalogKey.of(plugin, id), supplier));
    }
}
