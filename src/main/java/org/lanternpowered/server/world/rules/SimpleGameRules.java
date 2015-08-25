package org.lanternpowered.server.world.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.world.GameRuleChangeEvent;
import org.spongepowered.api.util.Coerce;
import org.spongepowered.api.world.World;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleGameRules implements GameRules {

    private final Map<String, GameRuleBase> rules = Maps.newHashMap();
    private final World world;

    public SimpleGameRules(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public GameRuleBase newRule(String name) {
        checkNotNull(name, "name");

        if (this.rules.containsKey(name)) {
            return this.rules.get(name);
        }

        GameRuleBase rule = new GameRuleBase(name);
        this.rules.put(name, rule);

        return rule;
    }

    @Override
    public Optional<GameRule> getRule(String name) {
        return Optional.<GameRule>fromNullable(this.rules.get(checkNotNull(name, "name")));
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
        return Collections.unmodifiableMap(Maps.transformValues(this.rules, new Function<GameRuleBase, String>() {

            @Override
            public String apply(GameRuleBase input) {
                return input.value;
            }

        }));
    }

    @Override
    public List<GameRule> getRules() {
        return Collections.unmodifiableList(new ArrayList<GameRule>(this.rules.values()));
    }

    class GameRuleBase implements GameRule {

        private final String name;

        // The value of the game rule
        private String value = "";

        // Optional possible types
        private Boolean valueBoolean = false;
        private Number valueNumber = 0;

        public GameRuleBase(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public <T> void setValue(T object) {
            checkNotNull(object, "object");

            String oldValue = this.value;

            this.value = Coerce.toString(object);
            this.valueBoolean = Coerce.toBoolean(this.value);
            this.valueNumber = Coerce.toDouble(this.value);

            if (!this.value.equals(oldValue)) {
                GameRuleChangeEvent event = SpongeEventFactory.createGameRuleChange(LanternGame.get(), world, this.name, oldValue, this.value);
                if (LanternGame.get().getEventManager().post(event)) {
                    return;
                }
            }
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
