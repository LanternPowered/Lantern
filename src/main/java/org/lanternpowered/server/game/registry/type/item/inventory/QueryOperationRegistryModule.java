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

import com.google.common.collect.Iterables;
import org.lanternpowered.server.data.property.PropertyHelper;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.lanternpowered.server.inventory.query.LanternQueryOperationType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.ArmorSlotType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.query.QueryOperationType;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class QueryOperationRegistryModule extends PluginCatalogRegistryModule<QueryOperationType> {

    public QueryOperationRegistryModule() {
        super(QueryOperationTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternQueryOperationType<ItemStack>("sponge", "item_stack_exact",
                (arg, inventory) -> inventory instanceof Slot && inventory.contains(arg)));
        register(new LanternQueryOperationType<ItemStack>("sponge", "item_stack_ignore_quantity",
                (arg, inventory) -> inventory instanceof Slot && inventory.containsAny(arg)));
        register(new LanternQueryOperationType<ItemType>("sponge", "item_type",
                (arg, inventory) -> inventory instanceof Slot && inventory.contains(arg)));
        register(new LanternQueryOperationType<Predicate<ItemStack>>("sponge", "item_stack_custom",
                (arg, inventory) -> inventory instanceof Slot && arg.test(inventory.peek().orElse(ItemStack.empty()))));
        register(new LanternQueryOperationType<Class<? extends Inventory>>("sponge", "inventory_type", Class::isInstance));
        register(new LanternQueryOperationType<Translation>("sponge", "inventory_translation",
                (arg, inventory) -> inventory.getName().equals(arg)));
        register(new LanternQueryOperationType<InventoryProperty<?,?>>("sponge", "inventory_property",
                (arg, inventory) -> {
                    // Equipment slot types are a special case, because
                    // they can be grouped
                    if (arg instanceof EquipmentSlotType) {
                        for (EquipmentSlotType property : Iterables.concat(
                                inventory.getProperties(EquipmentSlotType.class),
                                inventory.getProperties(ArmorSlotType.class))) {
                            if (((LanternEquipmentType) ((EquipmentSlotType) arg).getValue()).isChild(property.getValue())) {
                                return true;
                            }
                        }
                        return false;
                    }
                    final Optional<InventoryProperty<?,?>> optProperty = inventory.getProperty(
                            inventory, (Class) arg.getClass(), arg.getKey());
                    if (optProperty.isPresent()) {
                        final InventoryProperty<?,?> prop = optProperty.get();
                        if (PropertyHelper.matches(arg, prop)) {
                            return true;
                        }
                    }
                    return false;
                }));
    }
}
