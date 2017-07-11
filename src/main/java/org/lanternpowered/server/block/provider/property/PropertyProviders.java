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

import org.lanternpowered.server.block.property.FlameInfoProperty;
import org.lanternpowered.server.block.property.FlameInfo;
import org.lanternpowered.server.block.property.InstrumentProperty;
import org.lanternpowered.server.block.property.LightAbsorptionProperty;
import org.lanternpowered.server.block.property.PushBehavior;
import org.lanternpowered.server.block.property.PushBehaviorProperty;
import org.lanternpowered.server.block.property.SlipperinessProperty;
import org.lanternpowered.server.block.property.SolidSideProperty;
import org.lanternpowered.server.block.provider.ObjectProvider;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.block.BlastResistanceProperty;
import org.spongepowered.api.data.property.block.FlammableProperty;
import org.spongepowered.api.data.property.block.GravityAffectedProperty;
import org.spongepowered.api.data.property.block.HardnessProperty;
import org.spongepowered.api.data.property.block.LightEmissionProperty;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.data.property.block.StatisticsTrackedProperty;
import org.spongepowered.api.data.property.block.SurrogateBlockProperty;
import org.spongepowered.api.data.property.block.UnbreakableProperty;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.extra.fluid.data.property.FluidTemperatureProperty;
import org.spongepowered.api.extra.fluid.data.property.FluidViscosityProperty;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class PropertyProviders {

    private static final Map<MatterProperty.Matter, MatterProperty> MATTER_PROPERTIES = new EnumMap<>(MatterProperty.Matter.class);

    private static final FlammableProperty FLAMMABLE_PROPERTY_TRUE = new FlammableProperty(true);
    private static final FlammableProperty FLAMMABLE_PROPERTY_FALSE = new FlammableProperty(false);

    private static final ReplaceableProperty REPLACEABLE_PROPERTY_TRUE = new ReplaceableProperty(true);
    private static final ReplaceableProperty REPLACEABLE_PROPERTY_FALSE = new ReplaceableProperty(false);

    private static final SolidCubeProperty SOLID_CUBE_PROPERTY_TRUE = new SolidCubeProperty(true);
    private static final SolidCubeProperty SOLID_CUBE_PROPERTY_FALSE = new SolidCubeProperty(false);

    private static final SolidSideProperty SOLID_SIDE_PROPERTY_TRUE = new SolidSideProperty(true);
    private static final SolidSideProperty SOLID_SIDE_PROPERTY_FALSE = new SolidSideProperty(false);

    private static final PassableProperty PASSABLE_PROPERTY_TRUE = new PassableProperty(true);
    private static final PassableProperty PASSABLE_PROPERTY_FALSE = new PassableProperty(false);

    private static final GravityAffectedProperty GRAVITY_AFFECTED_PROPERTY_TRUE = new GravityAffectedProperty(true);
    private static final GravityAffectedProperty GRAVITY_AFFECTED_PROPERTY_FALSE = new GravityAffectedProperty(false);

    private static final UnbreakableProperty UNBREAKABLE_PROPERTY_TRUE = new UnbreakableProperty(true);
    private static final UnbreakableProperty UNBREAKABLE_PROPERTY_FALSE = new UnbreakableProperty(false);

    private static final StatisticsTrackedProperty STATISTICS_TRACKED_PROPERTY_TRUE = new StatisticsTrackedProperty(true);
    private static final StatisticsTrackedProperty STATISTICS_TRACKED_PROPERTY_FALSE = new StatisticsTrackedProperty(false);

    private static final SurrogateBlockProperty SURROGATE_BLOCK_PROPERTY_TRUE = new SurrogateBlockProperty(true);
    private static final SurrogateBlockProperty SURROGATE_BLOCK_PROPERTY_FALSE = new SurrogateBlockProperty(false);

    static {
        for (MatterProperty.Matter matter : MatterProperty.Matter.values()) {
            MATTER_PROPERTIES.put(matter, new MatterProperty(matter));
        }
    }

    public static PropertyProviderCollection constant(Property<?,?> property) {
        return PropertyProviderCollection.builder()
                .add(property.getClass(), new ConstantPropertyProvider(property))
                .build();
    }

    public static PropertyProviderCollection matter(MatterProperty.Matter constant) {
        final MatterProperty property = MATTER_PROPERTIES.get(constant);
        return PropertyProviderCollection.builder()
                .add(MatterProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection matter(ObjectProvider<MatterProperty.Matter> provider) {
        return PropertyProviderCollection.builder()
                .add(MatterProperty.class, (blockState, location, face) ->
                        MATTER_PROPERTIES.get(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection hardness(double constant) {
        final HardnessProperty property = new HardnessProperty(constant);
        return PropertyProviderCollection.builder()
                .add(HardnessProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection hardness(ObjectProvider<Double> provider) {
        return PropertyProviderCollection.builder()
                .add(HardnessProperty.class, (blockState, location, face) ->
                        new HardnessProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection blastResistance(double constant) {
        final BlastResistanceProperty property = new BlastResistanceProperty(constant);
        return PropertyProviderCollection.builder()
                .add(BlastResistanceProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection blastResistance(ObjectProvider<Double> provider) {
        return PropertyProviderCollection.builder()
                .add(BlastResistanceProperty.class, (blockState, location, face) ->
                        new BlastResistanceProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection unbreakable(boolean constant) {
        final UnbreakableProperty property = constant ? UNBREAKABLE_PROPERTY_TRUE : UNBREAKABLE_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(UnbreakableProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection unbreakable(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(UnbreakableProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? UNBREAKABLE_PROPERTY_TRUE : UNBREAKABLE_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection flammable(boolean constant) {
        final FlammableProperty property = constant ? FLAMMABLE_PROPERTY_TRUE : FLAMMABLE_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(FlammableProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection flammable(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(FlammableProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? FLAMMABLE_PROPERTY_TRUE : FLAMMABLE_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection lightEmission(int constant) {
        final LightEmissionProperty property = new LightEmissionProperty(constant);
        return PropertyProviderCollection.builder()
                .add(LightEmissionProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection lightEmission(ObjectProvider<Integer> provider) {
        return  PropertyProviderCollection.builder()
                .add(LightEmissionProperty.class, (blockState, location, face) ->
                        new LightEmissionProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection replaceable(boolean constant) {
        final ReplaceableProperty property = constant ? REPLACEABLE_PROPERTY_TRUE : REPLACEABLE_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(ReplaceableProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection replaceable(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(ReplaceableProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? REPLACEABLE_PROPERTY_TRUE : REPLACEABLE_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection solidCube(boolean constant) {
        final SolidCubeProperty property = constant ? SOLID_CUBE_PROPERTY_TRUE : SOLID_CUBE_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(SolidCubeProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection solidCube(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(SolidCubeProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? SOLID_CUBE_PROPERTY_TRUE : SOLID_CUBE_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection solidSide(boolean constant) {
        final SolidSideProperty property = constant ? SOLID_SIDE_PROPERTY_TRUE : SOLID_SIDE_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(SolidSideProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection solidSide(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(SolidSideProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? SOLID_SIDE_PROPERTY_TRUE : SOLID_SIDE_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection passable(boolean constant) {
        final PassableProperty property = constant ? PASSABLE_PROPERTY_TRUE : PASSABLE_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(PassableProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection passable(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(PassableProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? PASSABLE_PROPERTY_TRUE : PASSABLE_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection gravityAffected(boolean constant) {
        final GravityAffectedProperty property = constant ?
                GRAVITY_AFFECTED_PROPERTY_TRUE : GRAVITY_AFFECTED_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(GravityAffectedProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection gravityAffected(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(GravityAffectedProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? GRAVITY_AFFECTED_PROPERTY_TRUE : GRAVITY_AFFECTED_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection statisticsTracked(boolean constant) {
        final StatisticsTrackedProperty property = constant ?
                STATISTICS_TRACKED_PROPERTY_TRUE : STATISTICS_TRACKED_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(StatisticsTrackedProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection statisticsTracked(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(StatisticsTrackedProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? STATISTICS_TRACKED_PROPERTY_TRUE : STATISTICS_TRACKED_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection surrogateBlock(boolean constant) {
        final SurrogateBlockProperty property = constant ? SURROGATE_BLOCK_PROPERTY_TRUE : SURROGATE_BLOCK_PROPERTY_FALSE;
        return PropertyProviderCollection.builder()
                .add(SurrogateBlockProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection surrogateBlock(ObjectProvider<Boolean> provider) {
        return PropertyProviderCollection.builder()
                .add(SurrogateBlockProperty.class, (blockState, location, face) ->
                        provider.get(blockState, location, face) ? SURROGATE_BLOCK_PROPERTY_TRUE : SURROGATE_BLOCK_PROPERTY_FALSE)
                .build();
    }

    public static PropertyProviderCollection flammableInfo(int encouragement, int flammability) {
        return flammableInfo(new FlameInfo(encouragement, flammability));
    }

    public static PropertyProviderCollection flammableInfo(FlameInfo flameInfo) {
        final FlameInfoProperty property = new FlameInfoProperty(flameInfo);
        return PropertyProviderCollection.builder()
                .add(FlammableProperty.class, new ConstantPropertyProvider<>(FLAMMABLE_PROPERTY_TRUE))
                .add(FlameInfoProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection flammableInfo(ObjectProvider<FlameInfo> provider) {
        return PropertyProviderCollection.builder()
                .add(FlammableProperty.class, new ConstantPropertyProvider<>(FLAMMABLE_PROPERTY_TRUE))
                .add(FlameInfoProperty.class, (blockState, location, face) ->
                        new FlameInfoProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection instrument(InstrumentType instrument) {
        return PropertyProviderCollection.builder()
                .add(InstrumentProperty.class, new ConstantPropertyProvider<>(new InstrumentProperty(instrument)))
                .build();
    }

    public static PropertyProviderCollection instrument(ObjectProvider<InstrumentType> provider) {
        return PropertyProviderCollection.builder()
                .add(InstrumentProperty.class, (blockState, location, face) ->
                        new InstrumentProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection pushBehavior(PushBehavior pushBehavior) {
        return PropertyProviderCollection.builder()
                .add(PushBehaviorProperty.class, new ConstantPropertyProvider<>(new PushBehaviorProperty(pushBehavior)))
                .build();
    }

    public static PropertyProviderCollection pushBehavior(ObjectProvider<PushBehavior> provider) {
        return PropertyProviderCollection.builder()
                .add(PushBehaviorProperty.class, (blockState, location, face) ->
                        new PushBehaviorProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection fluidViscosity(int viscosity) {
        return PropertyProviderCollection.builder()
                .add(FluidViscosityProperty.class, new ConstantPropertyProvider<>(new FluidViscosityProperty(viscosity)))
                .build();
    }

    public static PropertyProviderCollection fluidViscosity(ObjectProvider<Integer> provider) {
        return PropertyProviderCollection.builder()
                .add(FluidViscosityProperty.class, (blockState, location, face) ->
                        new FluidViscosityProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection fluidTemperature(int temperature) {
        return PropertyProviderCollection.builder()
                .add(FluidTemperatureProperty.class, new ConstantPropertyProvider<>(new FluidTemperatureProperty(temperature)))
                .build();
    }

    public static PropertyProviderCollection fluidTemperature(ObjectProvider<Integer> provider) {
        return PropertyProviderCollection.builder()
                .add(FluidTemperatureProperty.class, (blockState, location, face) ->
                        new FluidTemperatureProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection slipperiness(double constant) {
        final SlipperinessProperty property = new SlipperinessProperty(constant);
        return PropertyProviderCollection.builder()
                .add(SlipperinessProperty.class, new ConstantPropertyProvider<>(property))
                .build();
    }

    public static PropertyProviderCollection slipperiness(ObjectProvider<Double> provider) {
        return PropertyProviderCollection.builder()
                .add(SlipperinessProperty.class, (blockState, location, face) ->
                        new SlipperinessProperty(provider.get(blockState, location, face)))
                .build();
    }

    public static PropertyProviderCollection lightAbsorption(int absorbedLight) {
        return PropertyProviderCollection.builder()
                .add(LightAbsorptionProperty.class, new ConstantPropertyProvider<>(new LightAbsorptionProperty(absorbedLight)))
                .build();
    }

    public static PropertyProviderCollection lightAbsorption(ObjectProvider<Integer> provider) {
        return PropertyProviderCollection.builder()
                .add(LightAbsorptionProperty.class, (blockState, location, face) ->
                        new LightAbsorptionProperty(provider.get(blockState, location, face)))
                .build();
    }
}
