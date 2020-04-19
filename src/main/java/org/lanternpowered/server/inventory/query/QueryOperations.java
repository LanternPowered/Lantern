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
package org.lanternpowered.server.inventory.query;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationType;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class QueryOperations {

    /**
     * The same as {@link QueryOperationTypes#ITEM_STACK_CUSTOM}.
     */
    public static final QueryOperationType<Predicate<ItemStack>> ITEM_STACK_PREDICATE =
            DummyObjectProvider.createFor(QueryOperationType.class, "ITEM_STACK_PREDICATE");

    /**
     * Similar to {@link #ITEM_STACK_PREDICATE}, but doesn't copy
     * the {@link ItemStack} before testing. It's not allowed to modify the provided
     * stack.
     */
    public static final QueryOperationType<Predicate<ItemStack>> UNSAFE_ITEM_STACK_PREDICATE =
            DummyObjectProvider.createFor(QueryOperationType.class, "ITEM_STACK_PREDICATE");
}
