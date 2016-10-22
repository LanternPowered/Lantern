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
package org.lanternpowered.server.item;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.data.AbstractDataHolder;
import org.lanternpowered.server.data.value.AbstractValueContainer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class LanternItemType extends PluginCatalogType.Base implements ItemType {

    private final Map<Class<?>, Property<?,?>> properties = new HashMap<>();
    private Translation translation;
    private int maxStackQuantity = 64;

    public LanternItemType(String pluginId, String identifier) {
        this(pluginId, identifier, identifier);
    }

    public LanternItemType(String pluginId, String identifier, String translationKey) {
        super(pluginId, identifier);
        this.translation = Lantern.getRegistry().getTranslationManager().get(
                "item." + translationKey + ".name");
    }

    public LanternItemType(String pluginId, String identifier, Translation translation) {
        super(pluginId, identifier);
        this.translation = checkNotNull(translation, "translation");
    }

    public void registerKeysFor(AbstractValueContainer valueContainer) {

    }

    @Override
    public int getMaxStackQuantity() {
        return this.maxStackQuantity;
    }

    /**
     * Sets the max stack quantity of this item type.
     *
     * @param maxStackQuantity The max stack quantity
     */
    protected void setMaxStackQuantity(int maxStackQuantity) {
        checkArgument(maxStackQuantity > 0, "maxStackQuantity must be greater then 0");
        this.maxStackQuantity = maxStackQuantity;
    }

    /**
     * Sets the translation of this item type.
     *
     * @param translation The translation
     */
    protected void setTranslation(Translation translation) {
        this.translation = checkNotNull(translation, "translation");
    }

    @Override
    public Translation getTranslation() {
        return this.translation;
    }

    /**
     * Gets the {@link Translation} for the specified {@link ItemStack}.
     *
     * @param itemStack The item stack
     * @return The translation
     */
    public Translation getTranslationFor(ItemStack itemStack) {
        return this.translation;
    }

    /**
     * Registers a {@link Property} for this item type.
     *
     * @param property The property
     */
    protected void registerProperty(Property<?, ?> property) {
        checkNotNull(property, "property");
        final List<Class<?>> types = TypeToken.of(property.getClass()).getSubtype(Property.class).getTypes().stream()
                .map(type -> type.getRawType())
                .filter(type -> !type.isInterface() && !Modifier.isAbstract(type.getModifiers()))
                .collect(Collectors.toList());
        for (Class<?> type : types) {
            this.properties.put(type, property);
        }
    }

    @Override
    public Optional<BlockType> getBlock() {
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Property<?, ?>> Optional<T> getDefaultProperty(Class<T> propertyClass) {
        return Optional.ofNullable((T) this.properties.get(checkNotNull(propertyClass, "propertyClass")));
    }

    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> clickedLocation, Direction blockFace) {
        return ItemInteractionResult.pass();
    }

}
