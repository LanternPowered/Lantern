package org.lanternpowered.server.world;

import java.util.List;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetDifficulty;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.rules.GameRules;
import org.lanternpowered.server.world.rules.LanternWorldGameRules;
import org.spongepowered.api.world.difficulty.Difficulty;

/**
 * A world properties class that is attached to a world instance to track changes.
 */
public class LanternWorldWorldProperties extends LanternWorldProperties {

    private final LanternWorld world;

    public LanternWorldWorldProperties(LanternWorld world) {
        this.world = world;
    }

    @Override
    protected GameRules createGameRules() {
        return new LanternWorldGameRules(this.world);
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        if (this.difficulty != difficulty) {
            List<LanternPlayer> players = this.world.getPlayers();
            if (!players.isEmpty()) {
                MessagePlayOutSetDifficulty message = new MessagePlayOutSetDifficulty((LanternDifficulty) difficulty);
                for (LanternPlayer player : players) {
                    player.getConnection().send(message);
                }
            }
        }
        super.setDifficulty(difficulty);
    }
}
