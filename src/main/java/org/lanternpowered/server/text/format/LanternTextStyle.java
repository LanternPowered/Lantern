package org.lanternpowered.server.text.format;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import javax.annotation.Nullable;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.text.format.TextStyle;

public class LanternTextStyle extends TextStyle.Base implements SimpleCatalogType {

    private final String identifier;

    public LanternTextStyle(String identifier,
            @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underline,
            @Nullable Boolean strikethrough, @Nullable Boolean obfuscated) {
        super(bold, italic, underline, strikethrough, obfuscated);
        this.identifier = checkNotNullOrEmpty(identifier, "identifier");
    }

    @Override
    public String getId() {
        return this.identifier;
    }
}