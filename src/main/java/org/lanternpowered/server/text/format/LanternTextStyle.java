package org.lanternpowered.server.text.format;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import javax.annotation.Nullable;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.format.TextStyle;

public class LanternTextStyle extends TextStyle.Base implements CatalogType {

    private final String identifier;
    private final String name;

    protected LanternTextStyle(String identifier, String name,
            @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underline,
            @Nullable Boolean strikethrough, @Nullable Boolean obfuscated) {
        super(bold, italic, underline, strikethrough, obfuscated);
        this.identifier = checkNotNullOrEmpty(identifier, "identifier");
        this.name = checkNotNullOrEmpty(name, "name");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

}