/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.lanternpowered.server.LanternServerConfig.Setting;
import org.lanternpowered.server.LanternServerConfig.Settings;
import org.lanternpowered.server.console.ConsoleManager;
import org.lanternpowered.server.console.LanternConsoleSource;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.network.NetworkManager;
import org.lanternpowered.server.service.profile.LanternGameProfileResolver;
import org.lanternpowered.server.status.LanternFavicon;
import org.lanternpowered.server.util.SecurityHelper;
import org.lanternpowered.server.util.ShutdownMonitorThread;
import org.lanternpowered.server.world.LanternWorldManager;
import org.lanternpowered.server.world.chunk.LanternChunkLayout;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.service.profile.GameProfileResolver;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.world.ChunkLoadService;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ConsoleSource;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;

public class LanternServer implements Server {

    public static void main(String[] args) {
        try {
            // Create the console instance
            ConsoleManager consoleManager = new ConsoleManager();
            // Initialize the console manager (setup basic logging)
            consoleManager.init();

            // Parse launch arguments
            LanternServerConfig config = parseArguments(args);
            if (config == null) {
                return;
            }

            // Create the game instance
            final LanternGame game = new LanternGame();

            // Start the console (command input/completer)
            consoleManager.start(game);

            // Create the server instance
            final LanternServer server = new LanternServer(game, config, consoleManager);

            // Send some startup info
            LanternGame.log().info("Starting Lantern Server (Minecraft: {} (Protocol: {}))",
                    LanternMinecraftVersion.CURRENT.getName(),
                    LanternMinecraftVersion.CURRENT.getProtocol()); 

            // Load the config file
            config.load();

            File pluginsFolder = new File(config.get(Settings.PLUGIN_FOLDER));
            File worldsFolder = new File(config.get(Settings.WORLD_FOLDER));

            // Initialize the game
            game.initialize(server, config.getFolder(), pluginsFolder, worldsFolder);

            // Bind the network channel
            server.bind();
            // Start the server
            server.start();
            
            // server.bindQuery();
            // server.bindRcon();
            LanternGame.log().info("Ready for connections.");
        } catch (BindException e) {
            // descriptive bind error messages
            LanternGame.log().error("The server could not bind to the requested address.");
            if (e.getMessage().startsWith("Cannot assign requested address")) {
                LanternGame.log().error("The 'server.ip' in your configuration may not be valid.");
                LanternGame.log().error("Unless you are sure you need it, try removing it.");
                LanternGame.log().error(e.toString());
            } else if (e.getMessage().startsWith("Address already in use")) {
                LanternGame.log().error("The address was already in use. Check that no server is");
                LanternGame.log().error("already running on that port. If needed, try killing all");
                LanternGame.log().error("Java processes using Task Manager or similar.");
                LanternGame.log().error(e.toString());
            } else {
                LanternGame.log().error("An unknown bind error has occurred.", e);
            }
            System.exit(1);
        } catch (Throwable t) {
            // general server startup crash
            LanternGame.log().error("Error during server startup.", t);
            System.exit(1);
        }
    }

    private static LanternServerConfig parseArguments(String[] args) throws IOException {
        final Map<Setting<?>, Object> parameters = Maps.newHashMap();

        String configDirName = "config";
        String configFileName = "lantern.json";

        // Calculate acceptable parameters
        for (int i = 0; i < args.length; i++) {
            final String opt = args[i];

            if (!opt.startsWith("-")) {
                LanternGame.log().warn("Ignored invalid option: " + opt);
                continue;
            }

            // Help and version
            if ("--help".equals(opt) || "-h".equals(opt) || "-?".equals(opt)) {
                LanternGame.log().info("Available command-line options:");
                LanternGame.log().info("  --help, -h, -?                 Shows this help message and exits.");
                LanternGame.log().info("  --version, -v                  Shows version information and exits.");
                LanternGame.log().info("  --config-dir <directory>       Sets the configuration directory.");
                LanternGame.log().info("  --config-file <file>           Sets the configuration file.");
                LanternGame.log().info("  --port, -p <port>              Sets the server listening port.");
                LanternGame.log().info("  --host, -H <ip | hostname>     Sets the server listening address.");
                LanternGame.log().info("  --onlinemode, -o <onlinemode>  Sets the server's online-mode.");
                LanternGame.log().info("  --jline <true/false>           Enables or disables JLine console.");
                LanternGame.log().info("  --plugins-dir, -P <directory>  Sets the plugin directory to use.");
                LanternGame.log().info("  --worlds-dir, -W <directory>   Sets the world directory to use.");
                LanternGame.log().info("  --update-dir, -U <directory>   Sets the plugin update folder to use.");
                LanternGame.log().info("  --max-players, -M <director>   Sets the maximum amount of players.");
                LanternGame.log().info("  --world-name, -N <name>        Sets the main world name.");
                LanternGame.log().info("  --log-pattern, -L <pattern>    Sets the log file pattern (%D for date).");
                return null;
            } else if ("--version".equals(opt) || "-v".equals(opt)) {
                LanternGame.log().info("Lantern version:   {}", LanternServer.class.getPackage().getImplementationVersion());
                LanternGame.log().info("SpongeAPI version: {}", LanternServer.class.getPackage().getSpecificationVersion());
                LanternGame.log().info("Minecraft version: {} (protocol: {})", LanternMinecraftVersion.CURRENT.getName(),
                        LanternMinecraftVersion.CURRENT.getProtocol());
                return null;
            }

            // Below this point, options require parameters
            if (i == args.length - 1) {
                LanternGame.log().warn("Ignored option specified without value: " + opt);
                continue;
            }

            switch (opt) {
                case "--config-dir":
                    configDirName = args[++i];
                    break;
                case "--config-file":
                    configFileName = args[++i];
                    break;
                case "--port":
                case "-p":
                    parameters.put(Settings.SERVER_PORT, Integer.valueOf(args[++i]));
                    break;
                case "--host":
                case "-H":
                    parameters.put(Settings.SERVER_IP, args[++i]);
                    break;
                case "--onlinemode":
                case "-o":
                    parameters.put(Settings.ONLINE_MODE, Boolean.valueOf(args[++i]));
                    break;
                case "--plugins-dir":
                case "-P":
                    parameters.put(Settings.PLUGIN_FOLDER, args[++i]);
                    break;
                case "--worlds-dir":
                case "-W":
                    parameters.put(Settings.WORLD_FOLDER, args[++i]);
                    break;
                case "--max-players":
                case "-M":
                    parameters.put(Settings.MAX_PLAYERS, Integer.valueOf(args[++i]));
                    break;
                case "--world-name":
                case "-N":
                    parameters.put(Settings.MAIN_WORLD, args[++i]);
                    break;
                default:
                    LanternGame.log().warn("Ignored invalid option: " + opt);
            }
        }

        final File configDir = new File(configDirName);
        final File configFile = new File(configDir, configFileName);

        return new LanternServerConfig(configDir, configFile, parameters);
    }

    // The executor service for the server ticks
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            runnable -> new Thread(runnable, "server"));

    // The world manager
    private LanternWorldManager worldManager;

    // The network manager
    private final NetworkManager networkManager = new NetworkManager(this);

    // The key pair used for authentication
    private final KeyPair keyPair = SecurityHelper.generateKeyPair();

    // The settings
    private final LanternServerConfig config;

    // The game instance
    private final LanternGame game;

    // The console manager
    private ConsoleManager consoleManager;

    // The maximum amount of players that can join
    private int maxPlayers;

    // The amount of ticks the server is running
    private final AtomicInteger runningTimeTicks = new AtomicInteger(0);

    private Text motd;
    private Favicon favicon;
    private boolean onlineMode;
    private boolean whitelist;

    private volatile boolean shuttingDown;

    public LanternServer(LanternGame game, LanternServerConfig config, ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
        this.config = config;
        this.game = game;
    }

    public void bind() throws BindException {
        SocketAddress address;

        String ip = this.config.get(Settings.SERVER_IP);
        int port = this.config.get(Settings.SERVER_PORT);
        if (ip.isEmpty()) {
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(ip, port);
        }

        ChannelFuture future = this.networkManager.init(address);
        Channel channel = future.awaitUninterruptibly().channel();
        if (!channel.isActive()) {
            Throwable cause = future.cause();
            if (cause instanceof BindException) {
                throw (BindException) cause;
            }
            throw new RuntimeException("Failed to bind to address", cause);
        }

        LanternGame.log().info("Successfully bound to: " + channel.localAddress());
    }

    public void start() {
        String defaultWorld = this.config.get(Settings.MAIN_WORLD);
        if (defaultWorld.isEmpty()) {
            defaultWorld = null;
        }
        this.worldManager = new LanternWorldManager(this.game.getSavesDirectory(), defaultWorld);

        this.game.setGameState(GameState.SERVER_ABOUT_TO_START);
        this.game.getEventManager().post(SpongeEventFactory.createGameAboutToStartServerEvent(this.game,
                GameState.SERVER_ABOUT_TO_START));
        this.game.setGameState(GameState.SERVER_STARTING);
        this.game.getEventManager().post(SpongeEventFactory.createGameStartingServerEvent(this.game, 
                GameState.SERVER_STARTING));

        this.maxPlayers = this.config.get(Settings.MAX_PLAYERS);
        this.motd = Texts.json().fromUnchecked(this.config.get(Settings.MOTD));

        File file = new File(this.config.get(Settings.FAVICON));
        if (file.exists()) {
            try {
                this.favicon = LanternFavicon.load(file);
            } catch (IOException e) {
                LanternGame.log().error("Failed to load the favicon", e);
            }
        }

        this.executor.scheduleAtFixedRate(() -> {
            try {
                pulse();
            } catch (Exception e) {
                LanternGame.log().error("Error while pulsing", e);
            }
        }, 0, LanternGame.TICK_DURATION, TimeUnit.MILLISECONDS);

        this.game.setGameState(GameState.SERVER_STARTED);
        this.game.getEventManager().post(SpongeEventFactory.createGameStartedServerEvent(this.game, 
                GameState.SERVER_STARTED));
    }

    /**
     * Pulses (ticks) the game.
     */
    private void pulse() {
        this.runningTimeTicks.incrementAndGet();
        // Pulse the network sessions
        this.networkManager.getSessionRegistry().pulse();
        // Pulse the sync scheduler tasks
        this.game.getScheduler().pulseSyncScheduler();
        // Pulse the world threads
        this.worldManager.pulse();
    }

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
        return Optional.ofNullable(this.favicon);
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
    public Collection<Player> getOnlinePlayers() {
        return Lists.newArrayList();
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
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
        return this.runningTimeTicks.get();
    }

    @Override
    public MessageSink getBroadcastSink() {
        return MessageSinks.toAll();
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        return Optional.of((InetSocketAddress) this.networkManager.getAddress());
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
        return this.motd;
    }

    @Override
    public void shutdown() {
        this.shutdown(Texts.of(this.game.getRegistry().getTranslationManager().get("disconnect.closed")));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void shutdown(Text kickMessage) {
        if (this.shuttingDown) {
            return;
        }
        this.shuttingDown = true;

        this.game.setGameState(GameState.SERVER_STOPPING);
        this.game.getEventManager().post(SpongeEventFactory.createGameStoppingServerEvent(this.game, 
                GameState.SERVER_STOPPING));

        // Debug a message
        LanternGame.log().info("Stopping the server... ({})", Texts.legacy().to(kickMessage));

        // Stop the console
        this.consoleManager.shutdown();

        // Kick all the online players
        this.getOnlinePlayers().forEach(player -> ((LanternPlayer) player).getConnection().disconnect(kickMessage));

        // Stop the network servers - starts the shutdown process
        // It may take a second or two for Netty to totally clean up
        this.networkManager.shutdown();

        // Stop the world manager
        this.worldManager.shutdown();

        // Shutdown the executor
        this.executor.shutdown();

        // Stop the async scheduler
        this.game.getScheduler().shutdownAsyncScheduler();

        SqlService service = this.game.getServiceManager().provide(SqlService.class).orElse(null);
        if (service instanceof Closeable) {
            try {
                ((Closeable) service).close();
            } catch (IOException e) {
                LanternGame.log().error("A error occurred while closing the sql service.", e);
            }
        }

        GameProfileResolver gameProfileResolver = this.game.getServiceManager().provide(GameProfileResolver.class).orElse(null);
        if (gameProfileResolver instanceof LanternGameProfileResolver) {
            ((LanternGameProfileResolver) gameProfileResolver).shutdown();
        }

        this.game.setGameState(GameState.SERVER_STOPPED);
        this.game.getEventManager().post(SpongeEventFactory.createGameStoppedServerEvent(this.game, 
                GameState.SERVER_STOPPED));

        // Wait for a while and terminate any rogue threads
        new ShutdownMonitorThread().start();
    }

    @Override
    public ConsoleSource getConsole() {
        return LanternConsoleSource.INSTANCE;
    }

    @Override
    public ChunkLoadService getChunkLoadService() {
        return this.game.getChunkLoadService();
    }

    @Override
    public double getTicksPerSecond() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Optional<ResourcePack> getDefaultResourcePack() {
        // TODO Auto-generated method stub
        return null;
    }
}
