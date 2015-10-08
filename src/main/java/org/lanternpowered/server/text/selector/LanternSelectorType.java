package org.lanternpowered.server.text.selector;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public class LanternSelectorType extends SimpleLanternCatalogType implements SelectorType {

    public LanternSelectorType(String identifier) {
        super(identifier);
    }
}
