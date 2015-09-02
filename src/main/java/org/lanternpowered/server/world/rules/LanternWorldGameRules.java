package org.lanternpowered.server.world.rules;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.event.SpongeEventFactory;
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
            public <T> void setValue(T object) {
                String oldValue = this.value;
                super.setValue(object);
                if (!this.value.equals(oldValue)) {
                    ChangeWorldGameRuleEvent event = SpongeEventFactory.createChangeWorldGameRuleEvent(
                            LanternGame.get(), this.getName(), oldValue, this.value, world);
                    LanternGame.get().getEventManager().post(event);
                }
            }
        };
    }
}
