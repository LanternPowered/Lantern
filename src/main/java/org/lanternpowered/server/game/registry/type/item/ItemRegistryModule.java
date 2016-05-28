/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.game.registry.type.item;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.util.ReflectionHelper;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency(BlockRegistryModule.class)
public final class ItemRegistryModule implements ItemRegistry {

    private static class Holder {

        private static final ItemRegistryModule INSTANCE = new ItemRegistryModule();
    }

    public static ItemRegistryModule get() {
        return Holder.INSTANCE;
    }

    @RegisterCatalog(ItemTypes.class)
    private final Map<String, ItemType> itemTypes = new HashMap<>();

    private final TIntObjectMap<ItemType> itemTypeByInternalId = new TIntObjectHashMap<>();
    private final TObjectIntMap<ItemType> internalIdByItemType = new TObjectIntHashMap<>();

    private ItemRegistryModule() {
    }

    /**
     * Registers a {@link ItemType} with the specified internal id.
     *
     * @param internalId The internal id
     * @param itemType The item type
     */
    public void register(int internalId, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        checkArgument(!this.itemTypes.containsValue(itemType), "The item type %s is already registered", itemType.getId());
        checkArgument(!this.itemTypeByInternalId.containsKey(internalId), "The internal id %d is already in use", internalId);
        checkArgument(!this.itemTypes.containsKey(itemType.getId()), "The id %s is already in use", itemType.getId());
        this.itemTypes.put(itemType.getId(), itemType);
        this.internalIdByItemType.put(itemType, internalId);
        this.itemTypeByInternalId.put(internalId, itemType);
    }

    @Override
    public int getInternalId(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        if (this.internalIdByItemType.containsKey(itemType)) {
            return this.internalIdByItemType.get(itemType);
        }
        return -1;
    }

    @Override
    public Optional<ItemType> getTypeByInternalId(int internalId) {
        return Optional.ofNullable(this.itemTypeByInternalId.get(internalId));
    }

    @Override
    public Optional<ItemType> getById(String id) {
        return Optional.ofNullable(this.itemTypes.get(checkNotNull(id, "identifier").toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<ItemType> getAll() {
        return ImmutableSet.copyOf(this.itemTypes.values());
    }

    @Override
    public void registerDefaults() {
        final LanternItemType none = new LanternItemType("minecraft", "none", null);
        this.register(0, none);
        try {
            ReflectionHelper.setField(ItemStackSnapshot.class.getDeclaredField("NONE"), null,
                    new LanternItemStack(none, 0).createSnapshot());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
