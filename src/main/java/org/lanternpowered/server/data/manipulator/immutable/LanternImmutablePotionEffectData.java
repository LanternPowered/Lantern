package org.lanternpowered.server.data.manipulator.immutable;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.ImmutableFireworkEffectData;
import org.spongepowered.api.data.manipulator.mutable.FireworkEffectData;
import org.spongepowered.api.item.FireworkEffect;

public class LanternImmutableFireworkEffectData extends AbstractImmutableListData<FireworkEffect, ImmutableFireworkEffectData, FireworkEffectData>
        implements ImmutableFireworkEffectData {

    public LanternImmutableFireworkEffectData() {
        super(ImmutableFireworkEffectData.class, FireworkEffectData.class, Keys.FIREWORK_EFFECTS);
    }

    public LanternImmutableFireworkEffectData(FireworkEffectData manipulator) {
        super(manipulator);
    }
}
