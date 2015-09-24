package org.lanternpowered.server.world.rules;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.Coerce;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;

public class LanternGameRules implements GameRules {

    private final Map<String, LanternGameRule> rules = Maps.newHashMap();

    @Override
    public LanternGameRule newRule(String name) {
        checkNotNull(name, "name");
        if (this.rules.containsKey(name)) {
            return this.rules.get(name);
        }
        LanternGameRule rule = this.createGameRule(name);
        this.rules.put(name, rule);
        return rule;
    }

    @Override
    public Optional<GameRule> getRule(String name) {
        return Optional.fromNullable(this.rules.get(checkNotNull(name, "name")));
    }

    @Override
    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(Maps.transformValues(this.rules,
                rule -> rule.value == null ? "" : rule.value));
    }

    @Override
    public List<GameRule> getRules() {
        return ImmutableList.<GameRule>copyOf(this.rules.values());
    }

    protected LanternGameRule createGameRule(String name) {
        return new LanternGameRule(name);
    }

    class LanternGameRule implements GameRule {

        private final String name;

        // The value of the game rule
        protected String value;

        // Optional possible types
        protected Boolean valueBoolean = false;
        protected Number valueNumber = 0;

        public LanternGameRule(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public <T> void set(T object) {
            this.set(object, Cause.empty());
        }

        @Override
        public <T> void set(T object, Cause cause) {
            if (object == null) {
                this.value = null;
                this.valueBoolean = null;
                this.valueNumber = null;
            } else {
                this.value = Coerce.asString(object).get();
                this.valueBoolean = Coerce.asBoolean(object).orNull();
                this.valueNumber = Coerce.asDouble(object).orNull();
            }
        }

        @Override
        public Optional<String> asString() {
            return Optional.fromNullable(this.value);
        }

        @Override
        public Optional<Boolean> asBoolean() {
            return Optional.fromNullable(this.valueBoolean);
        }

        @Override
        public Optional<Double> asDouble() {
            return this.valueNumber == null ? Optional.absent() : Optional.of(this.valueNumber.doubleValue());
        }

        @Override
        public Optional<Float> asFloat() {
            return this.valueNumber == null ? Optional.absent() : Optional.of(this.valueNumber.floatValue());
        }

        @Override
        public Optional<Integer> asInt() {
            return this.valueNumber == null ? Optional.absent() : Optional.of(this.valueNumber.intValue());
        }
    }

}
