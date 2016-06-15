package org.lanternpowered.server.catalog;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.translation.Translation;

public abstract class CatalogTypeBase implements CatalogType {

    protected final String identifier;
    protected final String name;

    public CatalogTypeBase(String identifier, String name) {
        this.identifier = checkNotNullOrEmpty(identifier, "identifier");
        this.name = checkNotNullOrEmpty(name, "name");
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static abstract class Translatable extends CatalogTypeBase implements org.spongepowered.api.text.translation.Translatable {

        private final Translation translation;

        public Translatable(String identifier, String name, String translation) {
            this(identifier, name, Lantern.getRegistry().getTranslationManager().get(translation));
        }

        public Translatable(String identifier, String name, Translation translation) {
            super(identifier, name);
            this.translation = checkNotNull(translation, "translation");
        }

        @Override
        public Translation getTranslation() {
            return this.translation;
        }
    }
}
