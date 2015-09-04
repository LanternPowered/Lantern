package org.lanternpowered.server;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.lanternpowered.server.console.LanternConsoleSource;
import org.lanternpowered.server.network.buf.LanternChannelRegistrar;
import org.lanternpowered.server.util.SecurityHelper;
import org.lanternpowered.server.world.LanternWorldManager;
import org.lanternpowered.server.world.chunk.LanternChunkLayout;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelListener;
import org.spongepowered.api.network.ChannelRegistrationException;
import org.spongepowered.api.service.world.ChunkLoadService;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

public class LanternServer implements Server {

    private final LanternChannelRegistrar channelRegistrar = new LanternChannelRegistrar(this);
    private final LanternWorldManager worldManager = new LanternWorldManager();
    private final KeyPair keyPair = SecurityHelper.generateKeyPair();

    private Favicon favicon;
    private boolean onlineMode;
    private boolean whitelist;

    /**
     * Gets the key pair.
     * 
     * @return the key pair
     */
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    /**
     * Gets the favicon of the server.
     * 
     * @return the favicon
     */
    public Optional<Favicon> getFavicon() {
        return Optional.fromNullable(this.favicon);
    }

    /**
     * Gets all the active command sources.
     * 
     * @return the active command sources
     */
    public Collection<CommandSource> getActiveCommandSources() {
        ImmutableList.Builder<CommandSource> commandSources = ImmutableList.builder();
        commandSources.add(this.getConsole());
        commandSources.addAll(this.getOnlinePlayers());
        return commandSources.build();
    }

    @Override
    public void registerChannel(Object plugin, ChannelListener listener, String channel) throws ChannelRegistrationException {
        this.channelRegistrar.registerChannel(plugin, listener, channel);
    }

    @Override
    public List<String> getRegisteredChannels() {
        return this.channelRegistrar.getRegisteredChannels();
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMaxPlayers() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<World> getWorlds() {
        return this.worldManager.getWorlds();
    }

    @Override
    public Collection<WorldProperties> getUnloadedWorlds() {
        return this.worldManager.getUnloadedWorlds();
    }

    @Override
    public Collection<WorldProperties> getAllWorldProperties() {
        return this.worldManager.getAllWorldProperties();
    }

    @Override
    public Optional<World> getWorld(UUID uniqueId) {
        return this.worldManager.getWorld(uniqueId);
    }

    @Override
    public Optional<World> getWorld(String worldName) {
        return this.worldManager.getWorld(worldName);
    }

    @Override
    public Optional<WorldProperties> getDefaultWorld() {
        return this.worldManager.getDefaultWorld();
    }

    @Override
    public Optional<World> loadWorld(String worldName) {
        return this.worldManager.loadWorld(worldName);
    }

    @Override
    public Optional<World> loadWorld(UUID uniqueId) {
        return this.worldManager.loadWorld(uniqueId);
    }

    @Override
    public Optional<World> loadWorld(WorldProperties properties) {
        return this.worldManager.loadWorld(properties);
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(String worldName) {
        return this.worldManager.getWorldProperties(worldName);
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        return this.worldManager.getWorldProperties(uniqueId);
    }

    @Override
    public boolean unloadWorld(World world) {
        return this.worldManager.unloadWorld(world);
    }

    @Override
    public Optional<WorldProperties> createWorld(WorldCreationSettings settings) {
        return this.worldManager.createWorld(settings);
    }

    @Override
    public ListenableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        return this.worldManager.copyWorld(worldProperties, copyName);
    }

    @Override
    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        return this.worldManager.renameWorld(worldProperties, newName);
    }

    @Override
    public ListenableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        return this.worldManager.deleteWorld(worldProperties);
    }

    @Override
    public boolean saveWorldProperties(WorldProperties properties) {
        return this.worldManager.saveWorldProperties(properties);
    }

    @Override
    public ChunkLayout getChunkLayout() {
        return LanternChunkLayout.INSTANCE;
    }

    @Override
    public int getRunningTimeTicks() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public MessageSink getBroadcastSink() {
        return MessageSinks.toAll();
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasWhitelist() {
        return this.whitelist;
    }

    @Override
    public void setHasWhitelist(boolean enabled) {
        this.whitelist = enabled;
    }

    @Override
    public boolean getOnlineMode() {
        return this.onlineMode;
    }

    @Override
    public Text getMotd() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void shutdown(Text kickMessage) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ConsoleSource getConsole() {
        return LanternConsoleSource.INSTANCE;
    }

    @Override
    public ChunkLoadService getChunkLoadService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * The following will be hard to detect since we are multi-threaded.
     */
    @Override
    public double getTicksPerSecond() {
        // TODO Auto-generated method stub
        return 0;
    }
}
