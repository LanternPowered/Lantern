package org.lanternpowered.server.world.rules;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

public interface GameRules {

    /**
     * Adds a new game rule and returns the instance.
     * 
     * @param name the name
     * @return the game rule
     */
    GameRule newRule(String name);

    /**
     * Get the game rule instance.
     * 
     * @param name the name
     * @return the game rule
     */
    Optional<GameRule> getRule(String name);

    /**
     * Gets the game rules.
     * 
     * @return the rules
     */
    List<GameRule> getRules();

    /**
     * Gets a map with all the string values.
     * 
     * @return the values
     */
    Map<String, String> getValues();
}
