package org.lanternpowered.server.effect.sound;

import org.lanternpowered.server.catalog.LanternSimpleCatalogType;
import org.spongepowered.api.effect.sound.SoundType;

public class LanternSoundType extends LanternSimpleCatalogType implements SoundType {

    public LanternSoundType(String identifier) {
        super(identifier);
    }
}
