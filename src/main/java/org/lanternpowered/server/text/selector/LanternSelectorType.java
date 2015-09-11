package org.lanternpowered.server.text.selector;

import org.lanternpowered.server.catalog.LanternSimpleCatalogType;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class LanternSelectorType extends LanternSimpleCatalogType implements SelectorType {

    public LanternSelectorType(String identifier) {
        super(identifier);
    }
}
