package org.lanternpowered.server.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Predicate;

import org.lanternpowered.server.catalog.LanternCatalogType;
import org.spongepowered.api.attribute.Attribute;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

public class LanternAttribute extends LanternCatalogType implements Attribute {

    private final Text name;
    private final Predicate<DataHolder> targets;

    private final double max;
    private final double min;
    private final double def;

    LanternAttribute(String identifier, Text name, double max, double min, double def, Predicate<DataHolder> targets) {
        super(identifier, Texts.toPlain(name));

        this.name = checkNotNull(name, "name");
        this.targets = checkNotNull(targets, "targets");

        // Make sure that the values aren't switched
        this.max = Math.max(max, min);
        this.min = Math.min(max, min);
        this.def = def;
    }

    @Override
    public double getMinimum() {
        return this.min;
    }

    @Override
    public double getMaximum() {
        return this.max;
    }

    @Override
    public double getDefaultValue() {
        return this.def;
    }

    @Override
    public Predicate<DataHolder> getTargets() {
        return this.targets;
    }

    /**
     * Gets the display name of the attribute.
     * 
     * <p>No method in the sponge api? There is one in the builder though.</p>
     * 
     * @return the display name
     */
    public Text getDisplayName() {
        return this.name;
    }
}
