package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nullable;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.base.Optional;

public final class Conditions {

    private static final String NOT_AVAILABLE = "This function is not available yet.";

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is a valid plugin container or plugin reference.
     * 
     * @param object an object reference
     * @param message the exception message to use if the check fails; will be
     *        converted to a string using {@link String.valueOf(Object)}.
     * @return the resulted plugin container
     * @throws NullPointerException - if reference is null
     * @throws IllegalArgumentException - if reference is invalid
     */
    public static PluginContainer checkPlugin(Object object, @Nullable Object message) {
        // Make sure that the game and plugin manager is already loaded
        LanternGame game = LanternGame.get();

        checkState(game != null && game.getPluginManager() != null, NOT_AVAILABLE);
        checkNotNull(object, message);

        if (object instanceof PluginContainer) {
            return (PluginContainer) object;
        }

        Optional<PluginContainer> container = LanternGame.get().getPluginManager().fromInstance(object);
        checkArgument(container.isPresent(), (message != null ? message + ": " : "") + "invalid plugin ({})", object);
        return container.get();
    }

    /**
     * Ensures that an string reference passed as a parameter to the calling
     * method is not null or empty.
     * 
     * @param object an object reference
     * @param message the exception message to use if the check fails; will be
     *        converted to a string using {@link String.valueOf(Object)}.
     * @return the reference that was validated
     * @throws NullPointerException - if reference is null
     * @throws IllegalArgumentException - if reference is empty
     */
    public static String checkNotNullOrEmpty(String object, @Nullable Object message) {
        checkNotNull(object, message);
        checkArgument(!object.isEmpty(), (message != null ? message + ": " : "") + "empty object");
        return object;
    }

    /**
     * Ensures that an string reference passed as a parameter to the calling
     * method is not null or empty.
     * 
     * @param object an object reference
     * @return the reference that was validated
     * @throws NullPointerException - if reference is null
     * @throws IllegalArgumentException - if reference is empty
     */
    public static String checkNotNullOrEmpty(String object) {
        checkNotNull(object);
        checkArgument(!object.isEmpty());
        return object;
    }

    private Conditions() {
    }

}
