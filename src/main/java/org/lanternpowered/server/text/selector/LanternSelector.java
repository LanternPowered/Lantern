package org.lanternpowered.server.text.selector;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorBuilder;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.text.selector.Selectors;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@NonnullByDefault
public class LanternSelector implements Selector {

    protected final SelectorType type;
    protected final ImmutableMap<ArgumentType<?>, Argument<?>> arguments;

    private final String plain;

    public LanternSelector(SelectorType type, ImmutableMap<ArgumentType<?>, Argument<?>> arguments) {
        this.type = type;
        this.arguments = arguments;
        this.plain = buildString();
    }

    @Override
    public SelectorType getType() {
        return this.type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(ArgumentType<T> type) {
        Argument<T> argument = (Argument<T>) this.arguments.get(type);
        return argument != null ? Optional.of(argument.getValue()) : Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Argument<T>> getArgument(ArgumentType<T> type) {
        return Optional.ofNullable((Argument<T>) this.arguments.get(type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Argument.Invertible<T>> getArgument(ArgumentType.Invertible<T> type) {
        return Optional.ofNullable((Argument.Invertible<T>) this.arguments.get(type));
    }

    @Override
    public List<Argument<?>> getArguments() {
        return this.arguments.values().asList();
    }

    @Override
    public boolean has(ArgumentType<?> type) {
        return this.arguments.containsKey(type);
    }

    @Override
    public boolean isInverted(ArgumentType.Invertible<?> type) {
        if (!this.has(type)) {
            return false;
        }
        return ((Argument.Invertible<?>) this.arguments.get(type)).isInverted();
    }

    @Override
    public Set<Entity> resolve(CommandSource origin) {
        return new SelectorResolver(origin, this, false).resolve();
    }

    @Override
    public Set<Entity> resolve(Extent... extents) {
        return resolve(ImmutableSet.copyOf(extents));
    }

    @Override
    public Set<Entity> resolve(Collection<? extends Extent> extents) {
        return new SelectorResolver(extents, this, false).resolve();
    }

    @Override
    public Set<Entity> resolve(Location<World> location) {
        return new SelectorResolver(location, this, false).resolve();
    }

    @Override
    public Set<Entity> resolveForce(CommandSource origin) {
        return new SelectorResolver(origin, this, true).resolve();
    }

    @Override
    public Set<Entity> resolveForce(Extent... extents) {
        return resolveForce(ImmutableSet.copyOf(extents));
    }

    @Override
    public Set<Entity> resolveForce(Collection<? extends Extent> extents) {
        return new SelectorResolver(extents, this, true).resolve();
    }

    @Override
    public Set<Entity> resolveForce(Location<World> location) {
        return new SelectorResolver(location, this, true).resolve();
    }

    @Override
    public String toPlain() {
        return this.plain;
    }

    @Override
    public SelectorBuilder builder() {
        SelectorBuilder builder = Selectors.builder(this.getType());
        builder.add(this.getArguments());
        return builder;
    }

    private String buildString() {
        StringBuilder result = new StringBuilder();
        result.append('@').append(this.type.getId());

        if (!this.arguments.isEmpty()) {
            result.append('[');
            Collection<Argument<?>> args = this.arguments.values();
            for (Iterator<Argument<?>> iter = args.iterator(); iter.hasNext();) {
                Argument<?> arg = iter.next();
                result.append(arg.toPlain());
                if (iter.hasNext()) {
                    result.append(',');
                }
            }
            result.append(']');
        }

        return result.toString();
    }
}
