package org.lanternpowered.server.behavior.neww;

import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a collection of {@link Behavior}s
 * assigned to {@link BehaviorType}s.
 */
public interface BehaviorCollection {

    /**
     * Assigns the given {@link Behavior} to the all
     * the annotation bound behavior types.
     *
     * @param behavior The behavior to assign
     * @return This for chaining
     */
    BehaviorCollection assign(Behavior behavior);

    /**
     * Assigns the given {@link Behavior} to the {@link BehaviorType}.
     *
     * @param type The behavior type the behavior should be assigned to
     * @param behavior The behavior to assign
     * @return This for chaining
     */
    BehaviorCollection assign(BehaviorType type, Behavior behavior);

    /**
     * Modifies the {@link Behavior} that's currently registered and
     * returns the new behavior. If no behavior is registered, then a empty
     * one which always returns {@code true} will be provided instead.
     *
     * @param type The behavior type of which its behavior should be modified
     * @param function The function to modify the registered behavior
     * @return This for chaining
     */
    BehaviorCollection modify(BehaviorType type, Function<Behavior, Behavior> function);

    /**
     * Gets the {@link Behavior} for the given {@link BehaviorType}, if present.
     *
     * @param type The behavior type
     * @return The behavior, if found
     */
    Optional<Behavior> get(BehaviorType type);

    /**
     * Gets the {@link Behavior} for the given {@link BehaviorType} if present. When it's
     * not present then will an empty one which always returns {@code true} be provided.
     *
     * @param type The behavior type
     * @return The behavior
     */
    Behavior getOrEmpty(BehaviorType type);
}
