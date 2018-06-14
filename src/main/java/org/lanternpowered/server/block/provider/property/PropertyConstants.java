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
package org.lanternpowered.server.block.provider.property;

import org.lanternpowered.server.block.property.SolidMaterialProperty;
import org.lanternpowered.server.block.property.SolidSideProperty;
import org.spongepowered.api.data.property.block.FlammableProperty;
import org.spongepowered.api.data.property.block.FullBlockSelectionBoxProperty;
import org.spongepowered.api.data.property.block.GravityAffectedProperty;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.data.property.block.StatisticsTrackedProperty;
import org.spongepowered.api.data.property.block.SurrogateBlockProperty;
import org.spongepowered.api.data.property.block.UnbreakableProperty;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class PropertyConstants {

    public static final FlammableProperty FLAMMABLE_PROPERTY_TRUE = new FlammableProperty(true);
    public static final FlammableProperty FLAMMABLE_PROPERTY_FALSE = new FlammableProperty(false);

    public static final ReplaceableProperty REPLACEABLE_PROPERTY_TRUE = new ReplaceableProperty(true);
    public static final ReplaceableProperty REPLACEABLE_PROPERTY_FALSE = new ReplaceableProperty(false);

    public static final SolidCubeProperty SOLID_CUBE_PROPERTY_TRUE = new SolidCubeProperty(true);
    public static final SolidCubeProperty SOLID_CUBE_PROPERTY_FALSE = new SolidCubeProperty(false);

    public static final SolidSideProperty SOLID_SIDE_PROPERTY_TRUE = new SolidSideProperty(true);
    public static final SolidSideProperty SOLID_SIDE_PROPERTY_FALSE = new SolidSideProperty(false);

    public static final SolidMaterialProperty SOLID_MATERIAL_PROPERTY_TRUE = new SolidMaterialProperty(true);
    public static final SolidMaterialProperty SOLID_MATERIAL_PROPERTY_FALSE = new SolidMaterialProperty(false);

    public static final PassableProperty PASSABLE_PROPERTY_TRUE = new PassableProperty(true);
    public static final PassableProperty PASSABLE_PROPERTY_FALSE = new PassableProperty(false);

    public static final GravityAffectedProperty GRAVITY_AFFECTED_PROPERTY_TRUE = new GravityAffectedProperty(true);
    public static final GravityAffectedProperty GRAVITY_AFFECTED_PROPERTY_FALSE = new GravityAffectedProperty(false);

    public static final UnbreakableProperty UNBREAKABLE_PROPERTY_TRUE = new UnbreakableProperty(true);
    public static final UnbreakableProperty UNBREAKABLE_PROPERTY_FALSE = new UnbreakableProperty(false);

    public static final StatisticsTrackedProperty STATISTICS_TRACKED_PROPERTY_TRUE = new StatisticsTrackedProperty(true);
    public static final StatisticsTrackedProperty STATISTICS_TRACKED_PROPERTY_FALSE = new StatisticsTrackedProperty(false);

    public static final SurrogateBlockProperty SURROGATE_BLOCK_PROPERTY_TRUE = new SurrogateBlockProperty(true);
    public static final SurrogateBlockProperty SURROGATE_BLOCK_PROPERTY_FALSE = new SurrogateBlockProperty(false);

    public static final FullBlockSelectionBoxProperty FULL_BLOCK_SELECTION_BOX_PROPERTY_TRUE = new FullBlockSelectionBoxProperty(true);
    public static final FullBlockSelectionBoxProperty FULL_BLOCK_SELECTION_BOX_PROPERTY_FALSE = new FullBlockSelectionBoxProperty(false);

    public static final Map<MatterProperty.Matter, MatterProperty> MATTER_PROPERTIES;

    static {
        final Map<MatterProperty.Matter, MatterProperty> matterProperties = new EnumMap<>(MatterProperty.Matter.class);
        for (MatterProperty.Matter matter : MatterProperty.Matter.values()) {
            matterProperties.put(matter, new MatterProperty(matter));
        }
        MATTER_PROPERTIES = Collections.unmodifiableMap(matterProperties);
    }

    private PropertyConstants() {
    }
}
