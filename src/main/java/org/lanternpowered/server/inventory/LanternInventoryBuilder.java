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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.translation.Translation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class LanternInventoryBuilder implements Inventory.Builder {

    private static final Translation DEFAULT_TRANSLATION = tr("Inventory");

    @Nullable private Carrier carrier;
    private InventoryArchetype inventoryArchetype;
    private final Map<String, InventoryProperty<String, ?>> inventoryPropertiesByName = new HashMap<>();
    private final Map<Class<?>, InventoryProperty<String, ?>> inventoryProperties = new HashMap<>();
    private final Map<Class<?>, Consumer<?>> eventListeners = new HashMap<>();

    @Override
    public LanternInventoryBuilder from(Inventory value) {
        return this;
    }

    @Override
    public LanternInventoryBuilder reset() {
        this.inventoryPropertiesByName.clear();
        this.inventoryProperties.clear();
        this.eventListeners.clear();
        this.carrier = null;
        return this;
    }

    @Override
    public LanternInventoryBuilder of(InventoryArchetype archetype) {
        this.inventoryArchetype = checkNotNull(archetype, "archetype");
        return this;
    }

    @Override
    public Inventory.Builder property(InventoryProperty<?, ?> property) {
        return property((String) AbstractInventoryProperty.getDefaultKey(property.getClass()), property);
    }

    @Override
    public LanternInventoryBuilder property(@Nullable String name, InventoryProperty property) {
        checkNotNull(property, "property");
        //noinspection ConstantConditions
        if (name != null) {
            this.inventoryPropertiesByName.put(name, property);
        }
        //noinspection unchecked
        this.inventoryProperties.put(property.getClass(), property);
        return this;
    }

    @Override
    public LanternInventoryBuilder withCarrier(Carrier carrier) {
        this.carrier = checkNotNull(carrier, "carrier");
        return this;
    }

    @Override
    public <E extends InteractInventoryEvent> LanternInventoryBuilder listener(Class<E> type, Consumer<E> listener) {
        checkNotNull(type, "type");
        checkNotNull(listener, "listener");
        this.eventListeners.put(type, listener);
        return this;
    }

    @Override
    public LanternInventoryBuilder forCarrier(Carrier carrier) {
        return this;
    }

    @Override
    public LanternInventoryBuilder forCarrier(Class<? extends Carrier> carrier) {
        return this;
    }

    @Override
    public Inventory build(Object plugin) {
        //noinspection unchecked
        final Map<String, InventoryProperty<String, ?>> inventoryPropertiesByName =
                new HashMap<>(this.inventoryArchetype.getProperties());
        //noinspection unchecked
        final Map<Class<?>, InventoryProperty<String, ?>> inventoryProperties =
                new HashMap<>((Map) ((LanternInventoryArchetype) this.inventoryArchetype).getPropertiesByClass());
        inventoryPropertiesByName.putAll(this.inventoryPropertiesByName);
        inventoryProperties.putAll(this.inventoryProperties);
        final Translation title = getTitle(inventoryProperties);
        // TODO
        return null;
    }

    /**
     * Gets the title that should be used for the inventory.
     *
     * @return The title
     */
    private static Translation getTitle(Map<Class<?>, InventoryProperty<String, ?>> map) {
        //noinspection unchecked
        final InventoryTitle property = (InventoryTitle) map.get(InventoryTitle.class);
        if (property != null) {
            final Text value = property.getValue();
            checkNotNull(value);
            if (value instanceof TranslatableText) {
                final TranslatableText value1 = (TranslatableText) value;
                if (value1.getChildren().isEmpty() && value1.getArguments().isEmpty()) {
                    return value1.getTranslation();
                }
            }
            return tr(value.toPlain());
        }
        return DEFAULT_TRANSLATION;
    }
}
