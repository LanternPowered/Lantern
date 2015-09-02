package org.lanternpowered.server;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.lanternpowered.server.console.LanternConsoleSource;
import org.lanternpowered.server.network.buf.LanternChannelRegistrar;
import org.lanternpowered.server.util.SecurityHelper;
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
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;

public class LanternServer implements Server {

    private final LanternChannelRegistrar channelRegistrar = new LanternChannelRegistrar(this);
    private final KeyPair keyPair = SecurityHelper.generateKeyPair();

    private Favicon favicon;

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<WorldProperties> getUnloadedWorlds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<WorldProperties> getAllWorldProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<World> getWorld(UUID uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<World> getWorld(String worldName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<WorldProperties> getDefaultWorld() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<World> loadWorld(String worldName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<World> loadWorld(UUID uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<World> loadWorld(WorldProperties properties) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(String worldName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean unloadWorld(World world) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Optional<WorldProperties> createWorld(WorldCreationSettings settings) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ListenableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean saveWorldProperties(WorldProperties properties) {
        // TODO Auto-generated method stub
        return false;
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setHasWhitelist(boolean enabled) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getOnlineMode() {
        // TODO Auto-generated method stub
        return false;
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
