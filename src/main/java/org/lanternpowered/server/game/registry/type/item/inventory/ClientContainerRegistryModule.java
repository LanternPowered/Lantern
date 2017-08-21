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

import com.flowpowered.math.vector.Vector2i;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.lanternpowered.server.inventory.InventoryPropertyHolder;
import org.lanternpowered.server.inventory.client.AnvilClientContainer;
import org.lanternpowered.server.inventory.client.BeaconClientContainer;
import org.lanternpowered.server.inventory.client.BrewingStandClientContainer;
import org.lanternpowered.server.inventory.client.ChestClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainerProvider;
import org.lanternpowered.server.inventory.client.ClientContainerType;
import org.lanternpowered.server.inventory.client.CraftingTableClientContainer;
import org.lanternpowered.server.inventory.client.DispenserClientContainer;
import org.lanternpowered.server.inventory.client.EnchantmentTableClientContainer;
import org.lanternpowered.server.inventory.client.FurnaceClientContainer;
import org.lanternpowered.server.inventory.client.HopperClientContainer;
import org.lanternpowered.server.inventory.client.ShulkerBoxClientContainer;
import org.lanternpowered.server.inventory.client.TradingClientContainer;
import org.spongepowered.api.item.inventory.property.GuiId;
import org.spongepowered.api.item.inventory.property.GuiIds;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

import java.util.function.BiFunction;

public class ClientContainerRegistryModule extends PluginCatalogRegistryModule<GuiId> {

    private static final ClientContainerRegistryModule instance = new ClientContainerRegistryModule();

    public static ClientContainerRegistryModule get() {
        return instance;
    }

    private ClientContainerRegistryModule() {
        super(GuiIds.class);
    }

    @Override
    public void registerDefaults() {
        register("minecraft", "chest", t("container.chest"), (text, propertyHolder) -> {
            // A row amount should be provided as a property
            final Vector2i dimension = propertyHolder.getProperty(InventoryDimension.class).map(InventoryDimension::getValue).orElse(null);
            final int rows = dimension == null ? 1 : dimension.getY();
            return new ChestClientContainer(text, rows);
        });
        register("minecraft", "furnace", t("container.furnace"), (text, propertyHolder) -> new FurnaceClientContainer(text));
        register("minecraft", "dispenser", t("container.dispenser"), (text, propertyHolder) -> new DispenserClientContainer(text));
        register("minecraft", "crafting_table", t("container.crafting"), (text, propertyHolder) -> new CraftingTableClientContainer(text));
        register("minecraft", "brewing_stand", t("container.brewing"), (text, propertyHolder) -> new BrewingStandClientContainer(text));
        register("minecraft", "hopper", t("container.hopper"), (text, propertyHolder) -> new HopperClientContainer(text));
        register("minecraft", "beacon", t("container.beacon"), (text, propertyHolder) -> new BeaconClientContainer(text));
        register("minecraft", "enchanting_table", t("container.enchant"), (text, propertyHolder) -> new EnchantmentTableClientContainer(text));
        register("minecraft", "anvil", t("container.repair"), (text, propertyHolder) -> new AnvilClientContainer(text));
        register("minecraft", "villager", t("container.trading"), (text, propertyHolder) -> new TradingClientContainer(text));
        // register("minecraft", "horse", t("container.horse"), (text, propertyHolder) -> new EntityEquipmentClientContainer(text)); TODO
        register("minecraft", "shulker_box", t("container.shulkerBox"), (text, propertyHolder) -> new ShulkerBoxClientContainer(text));
    }

    private void register(String plugin, String id, Text defaultTitle, BiFunction<Text, InventoryPropertyHolder, ClientContainer> supplier) {
        register(new ClientContainerType(plugin, id, new SimpleProvider(defaultTitle, supplier)));
    }

    private final class SimpleProvider implements ClientContainerProvider {

        private final Text defaultTitle;
        private final BiFunction<Text, InventoryPropertyHolder, ClientContainer> supplier;

        private SimpleProvider(Text defaultTitle,
                BiFunction<Text, InventoryPropertyHolder, ClientContainer> supplier) {
            this.defaultTitle = defaultTitle;
            this.supplier = supplier;
        }

        @Override
        public ClientContainer apply(InventoryPropertyHolder propertyHolder) {
            Text title = propertyHolder.getProperty(InventoryTitle.class).map(InventoryTitle::getValue).orElse(null);
            if (title == null) {
                title = this.defaultTitle;
            }
            return this.supplier.apply(title, propertyHolder);
        }
    }
}
