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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.data.type.LanternCareer;
import org.lanternpowered.server.data.type.LanternProfession;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.type.Professions;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency(ProfessionRegistryModule.class)
public class CareerRegistryModule extends DefaultCatalogRegistryModule<Career> {

    public CareerRegistryModule() {
        super(Careers.class);
    }

    @Override
    protected void register(Career catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        ((LanternProfession) catalogType.getProfession()).addCareer(catalogType);
    }

    @Override
    public void registerDefaults() {
        register(new LanternCareer(CatalogKey.minecraft("farmer"), Professions.FARMER));
        register(new LanternCareer(CatalogKey.minecraft("fisherman"), Professions.FARMER));
        register(new LanternCareer(CatalogKey.minecraft("shepherd"), Professions.FARMER));
        register(new LanternCareer(CatalogKey.minecraft("fletcher"), Professions.FARMER));
        register(new LanternCareer(CatalogKey.minecraft("librarian"), Professions.LIBRARIAN));
        register(new LanternCareer(CatalogKey.minecraft("cartographer"), Professions.LIBRARIAN));
        register(new LanternCareer(CatalogKey.minecraft("cleric"), Professions.PRIEST));
        register(new LanternCareer(CatalogKey.minecraft("armor"), Professions.BLACKSMITH));
        register(new LanternCareer(CatalogKey.minecraft("weapon"), Professions.BLACKSMITH));
        register(new LanternCareer(CatalogKey.minecraft("butcher"), Professions.BUTCHER));
        register(new LanternCareer(CatalogKey.minecraft("leather"), Professions.BUTCHER));
        // TODO: Use field when available
        register(new LanternCareer(CatalogKey.minecraft("nitwit"),
                Lantern.getRegistry().getType(Profession.class, CatalogKeys.minecraft("nitwit")).get()));
    }
}
