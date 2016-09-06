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
package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LanternProfession extends PluginCatalogType.Base implements Profession {

    private final int internalId;
    private final Set<Career> careers = new HashSet<>();
    private final Set<Career> unmodifiableCareers = Collections.unmodifiableSet(this.careers);

    public LanternProfession(String pluginId, String name, int internalId) {
        super(pluginId, name);
        this.internalId = internalId;
    }

    public LanternProfession(String pluginId, String id, String name, int internalId) {
        super(pluginId, id, name);
        this.internalId = internalId;
    }

    void addCareer(Career career) {
        this.careers.add(career);
    }

    @Override
    public Collection<Career> getCareers() {
        return this.unmodifiableCareers;
    }

    public int getInternalId() {
        return this.internalId;
    }
}
