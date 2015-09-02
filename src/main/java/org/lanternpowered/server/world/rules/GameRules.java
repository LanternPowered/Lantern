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
     * Gets the value of the game rule as a string.
     * 
     * @param name the name
     * @return the value
     */
    Optional<String> asString(String name);

    /**
     * Gets the value of the game rule as a boolean.
     * 
     * @param name the name
     * @return the value
     */
    boolean asBoolean(String name);

    /**
     * Gets the value of the game rule as a double.
     * 
     * @param name the name
     * @return the value
     */
    double asDouble(String name);

    /**
     * Gets the value of the game rule as a float.
     * 
     * @param name the name
     * @return the value
     */
    float asFloat(String name);

    /**
     * Gets the value of the game rule as a integer.
     * 
     * @param name the name
     * @return the value
     */
    int asInteger(String name);

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
