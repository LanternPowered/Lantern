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
package org.lanternpowered.server.game.registry.type.item;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.util.ReflectionHelper;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.Optional;

@RegistrationDependency(BlockRegistryModule.class)
public final class ItemRegistryModule extends AdditionalPluginCatalogRegistryModule<ItemType> implements ItemRegistry {

    private static class Holder {

        private static final ItemRegistryModule INSTANCE = new ItemRegistryModule();
    }

    public static ItemRegistryModule get() {
        return Holder.INSTANCE;
    }

    private final Int2ObjectMap<ItemType> itemTypeByInternalId = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<ItemType> internalIdByItemType = new Object2IntOpenHashMap<>();

    private ItemRegistryModule() {
        super(ItemTypes.class);
    }

    /**
     * Registers a {@link ItemType} with the specified internal id.
     *
     * @param internalId The internal id
     * @param itemType The item type
     */
    public void register(int internalId, ItemType itemType) {
        checkState(!this.itemTypeByInternalId.containsKey(internalId), "The internal id is already used: %s", internalId);
        super.register(itemType);
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
    public void registerDefaults() {
        final LanternItemType none = new LanternItemType("minecraft", "none");
        this.register(0, none);
        try {
            ReflectionHelper.setField(ItemStackSnapshot.class.getDeclaredField("NONE"), null,
                    new LanternItemStack(none, 0).createSnapshot());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
