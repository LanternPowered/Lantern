package org.lanternpowered.server.text.selector;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.spongepowered.api.text.selector.Argument;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.Selector;
import org.spongepowered.api.text.selector.SelectorBuilder;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Map;

@NonnullByDefault
public class LanternSelectorBuilder implements SelectorBuilder {

    private SelectorType type;
    private Map<ArgumentType<?>, Argument<?>> arguments;

    public LanternSelectorBuilder(SelectorType type) {
        this.type = checkNotNull(type, "type");
        this.arguments = Maps.newLinkedHashMap();
    }

    public LanternSelectorBuilder(Selector selector) {
        this.type = selector.getType();
        this.arguments = Maps.newLinkedHashMap(((LanternSelector) selector).arguments);
    }

    @Override
    public SelectorBuilder type(SelectorType type) {
        this.type = checkNotNull(type, "type");
        return this;
    }

    @Override
    public SelectorBuilder add(Argument<?>... arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.put(argument.getType(), argument);
        }
        return this;
    }

    @Override
    public SelectorBuilder add(Iterable<Argument<?>> arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.put(argument.getType(), argument);
        }
        return this;
    }

    @Override
    public <T> SelectorBuilder add(ArgumentType<T> type, T value) {
        this.arguments.put(type, new LanternArgument<T>(type, value));
        return this;
    }

    @Override
    public SelectorBuilder remove(Argument<?>... arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.remove(argument.getType());
        }
        return this;
    }

    @Override
    public SelectorBuilder remove(Iterable<Argument<?>> arguments) {
        for (Argument<?> argument : checkNotNull(arguments, "arguments")) {
            checkNotNull(argument, "argument");
            this.arguments.remove(argument.getType());
        }
        return this;
    }

    @Override
    public SelectorBuilder remove(ArgumentType<?>... types) {
        for (ArgumentType<?> type : checkNotNull(types, "types")) {
            checkNotNull(type, "type");
            this.arguments.remove(type);
        }
        return this;
    }

    @Override
    public Selector build() {
        return new LanternSelector(this.type, ImmutableMap.copyOf(this.arguments));
    }
}
