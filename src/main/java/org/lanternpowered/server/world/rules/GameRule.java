package org.lanternpowered.server.world.rules;

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
    <T> void setValue(T object);

    /**
     * Gets the value of the game rule as a string.
     * 
     * @return the value
     */
    String stringValue();

    /**
     * Gets the value of the game rule as a boolean.
     * 
     * @return the value
     */
    boolean booleanValue();

    /**
     * Gets the value of the game rule as a double.
     * 
     * @return the value
     */
    double doubleValue();

    /**
     * Gets the value of the game rule as a float.
     * 
     * @return the value
     */
    float floatValue();

    /**
     * Gets the value of the game rule as a integer.
     * 
     * @return the value
     */
    int intValue();

}
