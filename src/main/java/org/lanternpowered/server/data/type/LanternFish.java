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
package org.lanternpowered.server.data.type;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.item.PropertyProviderCollection;
import org.spongepowered.api.data.type.CookedFish;
import org.spongepowered.api.data.type.Fish;

import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class LanternFish extends PluginCatalogType.Base.Translatable.Internal implements Fish {

    @Nullable private CookedFish cookedFish;
    private final PropertyProviderCollection propertyProviderCollection;

    public LanternFish(String pluginId, String name, String translation, int internalId) {
        this(pluginId, name, translation, internalId, builder -> {});
    }

    public LanternFish(String pluginId, String name, String translation, int internalId,
            Consumer<PropertyProviderCollection.Builder> propertiesConsumer) {
        super(pluginId, name, translation, internalId);
        final PropertyProviderCollection.Builder builder = PropertyProviderCollection.builder();
        propertiesConsumer.accept(builder);
        this.propertyProviderCollection = builder.build();
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().omitNullValues().add("cookedFish", this.cookedFish == null ? null : this.cookedFish.getId());
    }

    @Override
    public Optional<CookedFish> getCookedFish() {
        return Optional.ofNullable(this.cookedFish);
    }

    public void setCookedFish(@Nullable CookedFish cookedFish) {
        this.cookedFish = cookedFish;
    }

    public PropertyProviderCollection getProperties() {
        return this.propertyProviderCollection;
    }
}
