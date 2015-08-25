package org.lanternpowered.server.world.chunk;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.chunk.tickets.TicketsProvider;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.world.ChunkLoadService;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

public class LanternChunkLoadService implements ChunkLoadService {

    private final Set<RegisteredCallback> callbacks = Collections.newSetFromMap(Maps.<RegisteredCallback, Boolean>newConcurrentMap());

    // TODO: Add values that make more sense, and make the server owner specify the limits
    private final TicketsProvider provider = new TicketsProvider() {

        @Override
        public int getMaxTicketsFor(String plugin) {
            return 100;
        }

        @Override
        public int getMaxChunksForTicket(String plugin) {
            return 32;
        }

    };

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

    @Override
    public void registerCallback(Object plugin, Callback callback) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        this.callbacks.add(new RegisteredCallback(container.getId(), checkNotNull(callback, "callback")));
    }

    @Override
    public Optional<LoadingTicket> createTicket(Object plugin, World world) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        LanternWorld world0 = (LanternWorld) checkNotNull(world, "world");
        return world0.getChunkManager().getLoadingTickets().createTicket(container.getId());
    }

    @Override
    public Optional<EntityLoadingTicket> createEntityTicket(Object plugin, World world) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        LanternWorld world0 = (LanternWorld) checkNotNull(world, "world");
        return world0.getChunkManager().getLoadingTickets().createEntityTicket(container.getId());
    }

    @Override
    public Optional<PlayerLoadingTicket> createPlayerTicket(Object plugin, World world, UUID player) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        LanternWorld world0 = (LanternWorld) checkNotNull(world, "world");
        return world0.getChunkManager().getLoadingTickets().createPlayerTicket(container.getId(), player);
    }

    @Override
    public Optional<PlayerEntityLoadingTicket> createPlayerEntityTicket(Object plugin, World world, UUID player) {
        PluginContainer container = checkPlugin(plugin, "plugin");
        LanternWorld world0 = (LanternWorld) checkNotNull(world, "world");
        return world0.getChunkManager().getLoadingTickets().createPlayerEntityTicket(container.getId(), player);
    }

    @Override
    public int getMaxTickets(Object plugin) {
        return this.provider.getMaxTicketsFor(checkPlugin(plugin, "plugin").getId());
    }

    @Override
    public int getAvailableTickets(Object plugin, World world) {
        String id = checkPlugin(plugin, "plugin").getId();
        LanternWorld world0 = (LanternWorld) checkNotNull(world, "world");
        return this.provider.getMaxTicketsFor(id) - world0.getChunkManager().getLoadingTickets().getTicketsFor(id);
    }

    @Override
    public int getAvailableTickets(UUID player) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ImmutableSetMultimap<Vector3i, LoadingTicket> getForcedChunks(World world) {
        return ((LanternWorld) checkNotNull(world, "world")).getChunkManager().getLoadingTickets().getForced();
    }

}
