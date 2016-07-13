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
package org.lanternpowered.server.game.registry.type.cause;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.game.registry.RegistryModuleHelper.validateIdentifier;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.cause.entity.damage.LanternDamageType;
import org.lanternpowered.server.cause.entity.dismount.LanternDismountType;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.dismount.DismountType;
import org.spongepowered.api.event.cause.entity.dismount.DismountTypes;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class DismountTypeRegistryModule implements AdditionalCatalogRegistryModule<DismountType>,
        AlternateCatalogRegistryModule<DismountType> {

    @RegisterCatalog(DismountTypes.class)
    private final Map<String, DismountType> dismountTypes = new HashMap<>();

    @Override
    public Map<String, DismountType> provideCatalogMap() {
        Map<String, DismountType> provided = new HashMap<>();
        for (Map.Entry<String, DismountType> entry : this.dismountTypes.entrySet()) {
            provided.put(entry.getKey().replace("minecraft:", ""), entry.getValue());
        }
        return provided;
    }

    @Override
    public void registerAdditionalCatalog(DismountType damageType) {
        checkNotNull(damageType, "damageType");
        final String id = damageType.getId();
        validateIdentifier(id);
        checkState(!this.dismountTypes.containsKey(id),
                "There is already a dismount type registered with the id. (" + id + ")");
        this.dismountTypes.put(id, damageType);
    }

    @Override
    public void registerDefaults() {
        this.registerAdditionalCatalog(new LanternDismountType("minecraft", "death"));
        this.registerAdditionalCatalog(new LanternDismountType("minecraft", "derail"));
        this.registerAdditionalCatalog(new LanternDismountType("minecraft", "player"));
    }

    @Override
    public Optional<DismountType> getById(String id) {
        if (checkNotNull(id).indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.dismountTypes.get(id.toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<DismountType> getAll() {
        return ImmutableSet.copyOf(this.dismountTypes.values());
    }

}
