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

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.data.property.PropertyHelper;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.equipment.LanternEquipmentType;
import org.lanternpowered.server.inventory.query.LanternQueryOperationType;
import org.lanternpowered.server.inventory.query.QueryOperations;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.query.QueryOperationType;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class QueryOperationRegistryModule extends DefaultCatalogRegistryModule<QueryOperationType> {

    public QueryOperationRegistryModule() {
        super(QueryOperationTypes.class, QueryOperations.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternQueryOperationType<ItemStack>(CatalogKeys.sponge("item_stack_exact"),
                (arg, inventory) -> inventory instanceof Slot && inventory.contains(arg)));
        register(new LanternQueryOperationType<ItemStack>(CatalogKeys.sponge("item_stack_ignore_quantity"),
                (arg, inventory) -> inventory instanceof Slot && inventory.containsAny(arg)));
        register(new LanternQueryOperationType<ItemType>(CatalogKeys.sponge("item_type"),
                (arg, inventory) -> inventory instanceof Slot && inventory.contains(arg)));
        register(new LanternQueryOperationType<Predicate<ItemStack>>(CatalogKeys.sponge("item_stack_custom"),
                (arg, inventory) -> inventory instanceof Slot && arg.test(inventory.peek())));
        register(new LanternQueryOperationType<Predicate<ItemStack>>(CatalogKeys.lantern("item_stack_predicate"),
                (arg, inventory) -> inventory instanceof Slot && arg.test(inventory.peek())));
        register(new LanternQueryOperationType<Predicate<ItemStack>>(CatalogKeys.lantern("unsafe_item_stack_predicate"),
                (arg, inventory) -> inventory instanceof Slot && arg.test(((AbstractSlot) inventory).getRawItemStack())));
        register(new LanternQueryOperationType<Class<? extends Inventory>>(CatalogKeys.sponge("inventory_type"),
                (arg, inventory) -> arg.isInstance(inventory)));
        register(new LanternQueryOperationType<Class<?>>(CatalogKeys.sponge("type"),
                (arg, inventory) -> arg.isInstance(inventory)));
        register(new LanternQueryOperationType<Translation>(CatalogKeys.sponge("inventory_translation"),
                (arg, inventory) -> inventory.getName().equals(arg)));
        register(new LanternQueryOperationType<InventoryProperty<?,?>>(CatalogKeys.sponge("inventory_property"),
                (arg, inventory) -> {
                    // Equipment slot types are a special case, because
                    // they can be grouped
                    if (arg instanceof EquipmentSlotType) {
                        for (EquipmentSlotType property : inventory.getProperties(EquipmentSlotType.class)) {
                            if (((LanternEquipmentType) arg.getValue()).isChild(property.getValue())) {
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
