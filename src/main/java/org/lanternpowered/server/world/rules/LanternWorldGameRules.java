package org.lanternpowered.server.world.rules;

import java.util.Objects;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.world.ChangeWorldGameRuleEvent;

public class LanternWorldGameRules extends LanternGameRules {

    private final LanternWorld world;

    public LanternWorldGameRules(LanternWorld world) {
        this.world = world;
    }

    @Override
    protected LanternGameRule createGameRule(String name) {
        return new LanternGameRule(name) {

            @Override
            public <T> void set(T object, Cause cause) {
                String oldValue = this.value;
                super.set(object);
                if (!Objects.equals(this.value, oldValue)) {
                    ChangeWorldGameRuleEvent event = SpongeEventFactory.createChangeWorldGameRuleEvent(LanternGame.get(),
                            cause, oldValue == null ? "" : oldValue, this.value == null ? "" : this.value, name, world);
                    LanternGame.get().getEventManager().post(event);
                }
            }
        };
    }
}
