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
package org.lanternpowered.server.game.registry.type.item.inventory;

import org.lanternpowered.api.ResourceKeys;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.query.LanternQueryOperationType;
import org.lanternpowered.server.inventory.query.QueryOperations;
import org.spongepowered.api.data.property.PropertyMatcher;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.query.QueryOperationType;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.translation.Translation;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class QueryOperationRegistryModule extends DefaultCatalogRegistryModule<QueryOperationType> {

    public QueryOperationRegistryModule() {
        super(QueryOperationTypes.class, QueryOperations.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternQueryOperationType<ItemStack>(ResourceKeys.sponge("item_stack_exact"),
                (arg, inventory) -> inventory instanceof Slot && inventory.contains(arg)));
        register(new LanternQueryOperationType<ItemStack>(ResourceKeys.sponge("item_stack_ignore_quantity"),
                (arg, inventory) -> inventory instanceof Slot && inventory.containsAny(arg)));
        register(new LanternQueryOperationType<ItemType>(ResourceKeys.sponge("item_type"),
                (arg, inventory) -> inventory instanceof Slot && inventory.contains(arg)));
        register(new LanternQueryOperationType<Predicate<ItemStack>>(ResourceKeys.sponge("item_stack_custom"),
                (arg, inventory) -> inventory instanceof Slot && arg.test(inventory.peek())));
        register(new LanternQueryOperationType<Predicate<ItemStack>>(ResourceKeys.lantern("item_stack_predicate"),
                (arg, inventory) -> inventory instanceof Slot && arg.test(inventory.peek())));
        register(new LanternQueryOperationType<Predicate<ItemStack>>(ResourceKeys.lantern("unsafe_item_stack_predicate"),
                (arg, inventory) -> inventory instanceof Slot && arg.test(((AbstractSlot) inventory).getRawItemStack())));
        register(new LanternQueryOperationType<Class<? extends Inventory>>(ResourceKeys.sponge("inventory_type"),
                (arg, inventory) -> arg.isInstance(inventory)));
        register(new LanternQueryOperationType<Class<?>>(ResourceKeys.sponge("type"),
                (arg, inventory) -> arg.isInstance(inventory)));
        register(new LanternQueryOperationType<Translation>(ResourceKeys.sponge("inventory_translation"),
                (arg, inventory) -> inventory.getName().equals(arg)));
        register(new LanternQueryOperationType<PropertyMatcher<?>>(ResourceKeys.sponge("property"),
                (arg, inventory) -> arg.matchesHolder(inventory)));
    }
}
