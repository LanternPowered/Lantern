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
package org.lanternpowered.server.statistic;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.statistic.StatisticFormat;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.text.translation.Translation;

public class LanternStatisticGroup extends PluginCatalogType.Base.Translatable implements StatisticGroup {

    private final StatisticFormat defaultStatisticFormat;

    public LanternStatisticGroup(String pluginId, String name, String translation, StatisticFormat defaultStatisticFormat) {
        super(pluginId, name, translation);
        this.defaultStatisticFormat = defaultStatisticFormat;
    }

    public LanternStatisticGroup(String pluginId, String name, Translation translation, StatisticFormat defaultStatisticFormat) {
        super(pluginId, name, translation);
        this.defaultStatisticFormat = defaultStatisticFormat;
    }

    public LanternStatisticGroup(String pluginId, String id, String name, String translation, StatisticFormat defaultStatisticFormat) {
        super(pluginId, id, name, translation);
        this.defaultStatisticFormat = defaultStatisticFormat;
    }

    public LanternStatisticGroup(String pluginId, String id, String name, Translation translation, StatisticFormat defaultStatisticFormat) {
        super(pluginId, id, name, translation);
        this.defaultStatisticFormat = defaultStatisticFormat;
    }

    @Override
    public StatisticFormat getDefaultStatisticFormat() {
        return this.defaultStatisticFormat;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("defaultStatisticFormat", this.defaultStatisticFormat.getId());
    }
}
