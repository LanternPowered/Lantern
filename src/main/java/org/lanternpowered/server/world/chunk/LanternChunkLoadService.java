package org.lanternpowered.server.world.chunk;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.world.ChunkLoadService;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

public class LanternChunkLoadService implements ChunkLoadService {

    private final Set<RegisteredCallback> callbacks = Sets.newConcurrentHashSet();

    public static class RegisteredCallback {

        private final Callback callback;
        private final String plugin;

        public RegisteredCallback(String plugin, Callback callback) {
            this.callback = callback;
            this.plugin = plugin;
        }

        public String getPlugin() {
            return this.plugin;
        }

        public Callback getCallback() {
            return this.callback;
        }

        @Override
        public int hashCode() {
            return this.plugin.hashCode() * 37 + this.callback.hashCode();
        }
    }

    /**
     * Gets all the registered callbacks.
     * 
     * @return the callbacks
     */
    public List<RegisteredCallback> getCallbacks() {
        return ImmutableList.copyOf(this.callbacks);
    }

    // TODO: Add values that make more sense, and make the server owner specify the limits

    /**
     * Gets the maximum amount of tickets the player can have.
     * 
     * @param playerUUID the player unique id
     * @return the maximum amount of tickets
     */
    public int getMaxTicketsForPlayer(UUID playerUUID) {
        return 50;
    }

    /**
     * Gets the maximum amount of tickets for the plugin per world.
     * 
     * @param plugin the plugin
     * @return the maximum amount of tickets
     */
    public int getMaxTicketsForPlugin(PluginContainer plugin) {
        return 100;
    }

    /**
     * Gets the maximum amount of forced chunks each ticket of the plugin can contain.
     * 
     * @param plugin the plugin
     * @return the maximum amount of forced chunks
     */
    public int getMaxChunksForPluginTicket(PluginContainer plugin) {
        return 32;
    }

    @Override
    public void registerCallback(Object plugin, Callback callback) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        this.callbacks.add(new RegisteredCallback(container.getId(), checkNotNull(callback, "callback")));
    }

    @Override
    public Optional<LoadingTicket> createTicket(Object plugin, World world) {
        return ((LanternWorld) checkNotNull(world, "world")).getChunkManager().createTicket(plugin);
    }

    @Override
    public Optional<EntityLoadingTicket> createEntityTicket(Object plugin, World world) {
        return ((LanternWorld) checkNotNull(world, "world")).getChunkManager().createEntityTicket(plugin);
    }

    @Override
    public Optional<PlayerLoadingTicket> createPlayerTicket(Object plugin, World world, UUID player) {
        return ((LanternWorld) checkNotNull(world, "world")).getChunkManager().createPlayerTicket(plugin, player);
    }

    @Override
    public Optional<PlayerEntityLoadingTicket> createPlayerEntityTicket(Object plugin, World world, UUID player) {
        return ((LanternWorld) checkNotNull(world, "world")).getChunkManager().createPlayerEntityTicket(plugin, player);
    }

    @Override
    public int getMaxTickets(Object plugin) {
        return this.getMaxTicketsForPlugin(checkPlugin(plugin, "plugin"));
    }

    @Override
    public int getAvailableTickets(Object plugin, World world) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        LanternWorld world0 = (LanternWorld) checkNotNull(world, "world");
        return this.getMaxTicketsForPlugin(container) - world0.getChunkManager().getTicketsForPlugin(container);
    }

    @Override
    public int getAvailableTickets(UUID player) {
        checkNotNull(player, "player");
        int count = 0;
        for (World world : LanternGame.get().getServer().getWorlds()) {
            count += ((LanternWorld) world).getChunkManager().getTicketsForPlayer(player);
        }
        return this.getMaxTicketsForPlayer(player) - count;
    }

    @Override
    public ImmutableSetMultimap<Vector3i, LoadingTicket> getForcedChunks(World world) {
        return ((LanternWorld) checkNotNull(world, "world")).getChunkManager().getForced();
    }
}
