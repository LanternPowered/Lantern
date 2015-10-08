package org.lanternpowered.server.effect.sound;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.effect.sound.SoundType;

public class LanternSoundType extends SimpleLanternCatalogType implements SoundType {

    public LanternSoundType(String identifier) {
        super(identifier);
    }
}
