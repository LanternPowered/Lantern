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

import org.lanternpowered.server.data.type.LanternCareer;
import org.lanternpowered.server.data.type.LanternProfession;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.type.Professions;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RegistrationDependency(ProfessionRegistryModule.class)
public class CareerRegistryModule extends PluginCatalogRegistryModule<Career> {

    private static final Method ADD_CAREER;

    static {
        try {
            ADD_CAREER = LanternProfession.class.getDeclaredMethod("addCareer", Career.class);
            ADD_CAREER.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public CareerRegistryModule() {
        super(Careers.class);
    }

    @Override
    protected void register(Career catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        try {
            ADD_CAREER.invoke(catalogType.getProfession(), catalogType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void registerDefaults() {
        register(new LanternCareer("minecraft", "farmer", Professions.FARMER));
        register(new LanternCareer("minecraft", "fisherman", Professions.FARMER));
        register(new LanternCareer("minecraft", "shepherd", Professions.FARMER));
        register(new LanternCareer("minecraft", "fletcher", Professions.FARMER));
        register(new LanternCareer("minecraft", "librarian", Professions.LIBRARIAN));
        register(new LanternCareer("minecraft", "cartographer", Professions.LIBRARIAN));
        register(new LanternCareer("minecraft", "cleric", Professions.PRIEST));
        register(new LanternCareer("minecraft", "armor", Professions.BLACKSMITH));
        register(new LanternCareer("minecraft", "weapon", Professions.BLACKSMITH));
        register(new LanternCareer("minecraft", "butcher", Professions.BUTCHER));
        register(new LanternCareer("minecraft", "leather", Professions.BUTCHER));
        // TODO: Use field when available
        register(new LanternCareer("minecraft", "nitwit",
                Lantern.getRegistry().getType(Profession.class, "minecraft:nitwit").get()));
    }
}
