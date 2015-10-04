package org.lanternpowered.server.world.rules;

import java.util.Optional;

import org.spongepowered.api.event.cause.Cause;

public interface GameRule {

    /**
     * Gets the name of the game rule.
     * 
     * @return the name
     */
    String getName();

    /**
     * Sets the value of the game rule.
     * 
     * @param object the object
     */
    <T> void set(T object);

    /**
     * Sets the value of the game rule with a specific cause.
     * 
     * @param object the object
     * @param cause the cause
     */
    <T> void set(T object, Cause cause);

    /**
     * Gets the value of the game rule as a string.
     * 
     * @return the value
     */
    Optional<String> asString();

    /**
     * Gets the value of the game rule as a boolean.
     * 
     * @return the value
     */
    Optional<Boolean> asBoolean();

    /**
     * Gets the value of the game rule as a double.
     * 
     * @return the value
     */
    Optional<Double> asDouble();

    /**
     * Gets the value of the game rule as a float.
     * 
     * @return the value
     */
    Optional<Float> asFloat();

    /**
     * Gets the value of the game rule as a integer.
     * 
     * @return the value
     */
    Optional<Integer> asInt();
}
