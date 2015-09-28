package org.lanternpowered.server.attribute;

import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.spongepowered.api.attribute.Attribute;
import org.spongepowered.api.attribute.AttributeBuilder;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.text.Text;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public final class LanternAttributeBuilder implements AttributeBuilder {

    private final CatalogTypeRegistry<Attribute> registry;

    private String identifier;
    private Text name;

    private Double min;
    private Double max;
    private Double def;

    private Predicate<DataHolder> targets = Predicates.alwaysTrue();

    public LanternAttributeBuilder(CatalogTypeRegistry<Attribute> registry) {
        this.registry = checkNotNull(registry, "registry");
    }

    @Override
    public AttributeBuilder id(String id) {
        this.identifier = checkNotNullOrEmpty(id, "identifier");
        return this;
    }

    @Override
    public AttributeBuilder minimum(double minimum) {
        this.min = minimum;
        return this;
    }

    @Override
    public AttributeBuilder maximum(double maximum) {
        this.max = maximum;
        return this;
    }

    @Override
    public AttributeBuilder defaultValue(double defaultValue) {
        this.def = defaultValue;
        return this;
    }

    @Override
    public AttributeBuilder targets(Predicate<DataHolder> targets) {
        this.targets = checkNotNull(targets, "targets");
        return this;
    }

    @Override
    public AttributeBuilder name(Text name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    @Override
    public Attribute build() {
        checkState(this.identifier != null, "identifier is not set");
        checkState(!this.registry.get(this.identifier).isPresent(), "identifier already in use");
        checkState(this.name != null, "name is not set");
        checkState(this.min != null, "minimum is not set");
        checkState(this.max != null, "maximum is not set");
        checkState(this.def != null, "defaultValue is not set");
        checkState(this.def >= this.min && this.def <= this.max, "defaultValue must scale between the minimum and maximum value");
        LanternAttribute attribute = new LanternAttribute(this.identifier, this.name, this.min, this.max, this.def, this.targets);
        this.registry.register(attribute);
        return attribute;
    }

    @Override
    public AttributeBuilder reset() {
        this.targets = Predicates.alwaysTrue();
        this.identifier = null;
        this.name = null;
        this.max = null;
        this.min = null;
        this.def = null;
        return this;
    }
}
