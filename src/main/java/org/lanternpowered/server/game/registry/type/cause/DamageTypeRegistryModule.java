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

import org.lanternpowered.server.cause.entity.damage.LanternDamageType;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

public class DamageTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<DamageType> {

    public DamageTypeRegistryModule() {
        super(DamageTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternDamageType("minecraft", "attack"));
        this.register(new LanternDamageType("minecraft", "contact"));
        this.register(new LanternDamageType("minecraft", "custom"));
        this.register(new LanternDamageType("minecraft", "drown"));
        this.register(new LanternDamageType("minecraft", "explosive"));
        this.register(new LanternDamageType("minecraft", "fall"));
        this.register(new LanternDamageType("minecraft", "fire"));
        this.register(new LanternDamageType("minecraft", "generic"));
        this.register(new LanternDamageType("minecraft", "hunger"));
        this.register(new LanternDamageType("minecraft", "magic"));
        this.register(new LanternDamageType("minecraft", "magma"));
        this.register(new LanternDamageType("minecraft", "projectile"));
        this.register(new LanternDamageType("minecraft", "suffocate"));
        this.register(new LanternDamageType("minecraft", "sweeping_attack"));
        this.register(new LanternDamageType("minecraft", "void"));
    }
}
