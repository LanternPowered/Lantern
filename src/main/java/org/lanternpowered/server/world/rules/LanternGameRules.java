package org.lanternpowered.server.world.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.util.Coerce;

import com.google.common.base.Function;
import com.google.common.base.Optional;
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
    public Optional<String> asString(String name) {
        if (this.rules.containsKey(checkNotNull(name, "name"))) {
            return Optional.of(this.rules.get(name).value);
        } else {
            return Optional.absent();
        }
    }

    @Override
    public boolean asBoolean(String name) {
        return this.rules.containsKey(checkNotNull(name, "name")) ? this.rules.get(name).booleanValue() : false;
    }

    @Override
    public double asDouble(String name) {
        return this.rules.containsKey(checkNotNull(name, "name")) ? this.rules.get(name).doubleValue() : 0;
    }

    @Override
    public float asFloat(String name) {
        return this.rules.containsKey(checkNotNull(name, "name")) ? this.rules.get(name).floatValue() : 0;
    }

    @Override
    public int asInteger(String name) {
        return this.rules.containsKey(checkNotNull(name, "name")) ? this.rules.get(name).intValue() : 0;
    }

    @Override
    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(Maps.transformValues(this.rules, new Function<LanternGameRule, String>() {
            @Override
            public String apply(LanternGameRule input) {
                return input.value;
            }
        }));
    }

    @Override
    public List<GameRule> getRules() {
        return Collections.unmodifiableList(new ArrayList<>(this.rules.values()));
    }

    protected LanternGameRule createGameRule(String name) {
        return new LanternGameRule(name);
    }

    class LanternGameRule implements GameRule {

        private final String name;

        // The value of the game rule
        protected String value = "";

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
        public <T> void setValue(T object) {
            checkNotNull(object, "object");
            this.value = Coerce.toString(object);
            this.valueBoolean = Coerce.toBoolean(this.value);
            this.valueNumber = Coerce.toDouble(this.value);
        }

        @Override
        public String stringValue() {
            return this.value;
        }

        @Override
        public boolean booleanValue() {
            return this.valueBoolean;
        }

        @Override
        public double doubleValue() {
            return this.valueNumber.doubleValue();
        }

        @Override
        public float floatValue() {
            return this.valueNumber.floatValue();
        }

        @Override
        public int intValue() {
            return this.valueNumber.intValue();
        }
    }

}
