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
package org.lanternpowered.test;

import static org.spongepowered.api.util.SpongeApiTranslationHelper.t;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.CoalType;
import org.spongepowered.api.data.type.TreeTypes;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.GuiIdProperty;
import org.spongepowered.api.item.inventory.property.GuiIds;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Plugin(id = "inventory_test")
public class TestInventoryPlugin {

    private InventoryArchetype myArchetype;

    @Listener
    public void onInit(GameInitializationEvent event) {
        this.myArchetype = InventoryArchetype.builder()
                .with(InventoryArchetypes.MENU_ROW)
                //.with(InventoryArchetypes.MENU_ROW)
                .title(Text.of("My Fancy Title"))
                .property(InventoryDimension.builder().value(new Vector2i(9, 1)).build())
                .property(GuiIdProperty.builder().value(GuiIds.CHEST).build())
                .build("inventory_test:test", "Test Inventory");
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .executor((src, args) -> {
                    if (!(src instanceof Player)) {
                        throw new CommandException(t("Only players may use this command."));
                    }
                    final Inventory inventory = Inventory.builder().of(this.myArchetype)
                            .withCarrier((Carrier) src)
                            .build(this);
                    System.out.println(inventory.getClass().getName());
                    final ItemStack itemStack = ItemStack.of(ItemTypes.LOG, 64);
                    itemStack.offer(Keys.TREE_TYPE, TreeTypes.JUNGLE);
                    inventory.offer(itemStack);
                    ((Player) src).openInventory(inventory);
                    return CommandResult.success();
                })
                .build(), "test-a-inv");
        Keys.COAL_TYPE.registerEvent(ItemStack.class, event1 -> {
            final DataTransactionResult result = event1.getEndResult();
            final List<ImmutableValue<?>> newSuccessfulData = new ArrayList<>(result.getSuccessfulData());
            Iterator<ImmutableValue<?>> it = newSuccessfulData.iterator();
            while (it.hasNext()) {
                final ImmutableValue<?> value = it.next();
                if (value.getKey() == Keys.COAL_TYPE) {
                    System.out.println("Changed coal type to: " + ((CoalType) value.get()).getId() + ", but this not allowed");
                    it.remove();
                    break;
                }
            }
            final List<ImmutableValue<?>> newReplacedData = new ArrayList<>(result.getReplacedData());
            it = newSuccessfulData.iterator();
            while (it.hasNext()) {
                final ImmutableValue<?> value = it.next();
                if (value.getKey() == Keys.COAL_TYPE) {
                    it.remove();
                    break;
                }
            }
            event1.proposeChanges(DataTransactionResult.builder()
                    .result(result.getType())
                    .reject(result.getRejectedData())
                    .replace(newReplacedData)
                    .success(newSuccessfulData)
                    .build());
        });
    }
}
