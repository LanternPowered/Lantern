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
package org.lanternpowered.server.game.registry.forge;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

public final class ForgeRegistryData {

    private final String moduleId;
    private final Object2IntMap<String> mappings;

    /**
     * Constructs a new {@link ForgeRegistryData} object.
     *
     * @param moduleId The module id
     * @param mappings The mappings
     */
    public ForgeRegistryData(String moduleId, Object2IntMap<String> mappings) {
        this.moduleId = moduleId;
        this.mappings = mappings;
    }

    /**
     * Gets the module id.
     *
     * @return The module id
     */
    public String getModuleId() {
        return this.moduleId;
    }

    /**
     * Gets the mappings.
     *
     * @return The mappings
     */
    public Object2IntMap<String> getMappings() {
        return this.mappings;
    }
}
