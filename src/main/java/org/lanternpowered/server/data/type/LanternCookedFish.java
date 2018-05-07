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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.item.PropertyProviderCollection;
import org.spongepowered.api.data.type.CookedFish;
import org.spongepowered.api.data.type.Fish;

import java.util.function.Consumer;

public class LanternCookedFish extends PluginCatalogType.Base.Translatable.Internal implements CookedFish {

    private final Fish rawFish;
    private final PropertyProviderCollection propertyProviderCollection;

    public LanternCookedFish(String pluginId, String name, String translation, int internalId, Fish rawFish) {
        this(pluginId, name, translation, internalId, rawFish, builder -> {});
    }

    public LanternCookedFish(String pluginId, String name, String translation, int internalId, Fish rawFish,
            Consumer<PropertyProviderCollection.Builder> propertiesConsumer) {
        super(pluginId, name, translation, internalId);
        this.rawFish = applyRawFish(rawFish);
        final PropertyProviderCollection.Builder builder = PropertyProviderCollection.builder();
        propertiesConsumer.accept(builder);
        this.propertyProviderCollection = builder.build();
    }

    private Fish applyRawFish(Fish rawFish) {
        checkNotNull(rawFish, "rawFish");
        ((LanternFish) rawFish).setCookedFish(this);
        return rawFish;
    }

    @Override
    public Fish getRawFish() {
        return this.rawFish;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("rawFish", this.rawFish);
    }

    public PropertyProviderCollection getProperties() {
        return this.propertyProviderCollection;
    }
}
