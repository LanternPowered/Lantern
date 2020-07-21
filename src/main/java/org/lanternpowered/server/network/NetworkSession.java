/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ScheduledFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.LanternServerNew;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.tab.GlobalTabList;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.entity.EntityProtocolManager;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.lanternpowered.server.network.packet.BulkPacket;
import org.lanternpowered.server.network.packet.HandlerPacket;
import org.lanternpowered.server.network.packet.Packet;
import org.lanternpowered.server.network.packet.MessageRegistration;
import org.lanternpowered.server.network.packet.UnknownPacket;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.packet.type.KeepAlivePacket;
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSettingsPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutBrand;
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerJoinPacket;
import org.lanternpowered.server.permission.Permissions;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldPropertiesOld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.network.ServerPlayerConnection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.util.Transform;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.dimension.DimensionTypes;
import org.spongepowered.math.vector.Vector3d;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("ConstantConditions")
public final class NetworkSession extends SimpleChannelInboundHandler<Packet> implements ServerPlayerConnection {

    /**
     * The read timeout in seconds.
     */
    public static final int READ_TIMEOUT_SECONDS = 10;

    public static final String ENCRYPTION = "encryption";
    public static final String LEGACY_PING = "legacy-ping";
    public static final String COMPRESSION = "compression";
    public static final String FRAMING = "framing";
    public static final String CODECS = "codecs";
    public static final String PROCESSOR = "processor";
    public static final String HANDLER = "handler";

    /**
     * The game profile of the player the last time he joined.
     */
    public static final AttributeKey<GameProfile> PREVIOUS_GAME_PROFILE = AttributeKey.valueOf("previous-game-profile");

    /**
     * The attribute key for the FML (Forge Mod Loader) marker.
     */
    public static final AttributeKey<Boolean> FML_MARKER = AttributeKey.valueOf("fml-marker");

    private final NetworkManager networkManager;
    private final LanternServerNew server;
    private final Channel channel;

    /**
     * The network context that is used by the handlers.
     */
    private final NetworkContext networkContext = new NetworkContext() {
        @Override
        public NetworkSession getSession() {
            return NetworkSession.this;
        }

        @Override
        public Channel getChannel() {
            return channel;
        }
    };

    /**
     * The game profile of the player that owns this connection.
     */
    @Nullable private LanternGameProfile gameProfile;

    /**
     * The player that owns this connection.
     */
    @Nullable private LanternPlayer player;

    /**
     * The reason that caused the channel to disconnect.
     */
    @Nullable private volatile Component disconnectReason;

    /**
     * A queue of incoming messages that must be handled on
     * the synchronous thread.
     */
    private final Queue<HandlerPacket> messageQueue = new ConcurrentLinkedDeque<>();

    /**
     * The virtual host address.
     */
    @Nullable private InetSocketAddress virtualHostAddress;

    /**
     * The protocol state that is currently active.
     */
    private volatile ProtocolState protocolState = ProtocolState.HANDSHAKE;

    /**
     * A list with all the registered channels.
     */
    private final Set<String> registeredChannels = new HashSet<>();

    /**
     * A list with all the installed client mods.
     */
    private final Set<String> installedMods = new HashSet<>();

    /**
     * The latency of the connection.
     */
    private volatile int latency;

    /**
     * The time that the last the keep alive message was send.
     */
    private long keepAliveTime = -1L;

    /**
     * The keep alive/initial connection timer task.
     */
    @Nullable private ScheduledFuture<?> connectionTask;

    /**
     * The protocol version.
     */
    private int protocolVersion = -1;

    /**
     * The locale of the player.
     */
    private Locale locale = Locales.DEFAULT;

    /**
     * Whether the first client settings message was received.
     */
    private boolean firstClientSettingsMessage;

    public NetworkSession(Channel channel, LanternServerNew server, NetworkManager networkManager) {
        this.networkManager = networkManager;
        this.channel = channel;
        this.server = server;
    }

    @Override
    public GameProfile getProfile() {
        return checkNotNull(this.gameProfile);
    }

    private static long currentTime() {
        return System.nanoTime() / 1000000L;
    }

    private void handleKeepAlive(KeepAlivePacket message) {
        if (this.keepAliveTime == message.getTime()) {
            final long time = currentTime();
            final int latency = this.latency;
            // Calculate the latency
            this.latency = (int) ((latency * 3 + (time - this.keepAliveTime)) / 4);
            this.keepAliveTime = -1L;
            if (this.gameProfile != null) {
                // Update the global tab list
                messageReceived(new HandlerPacket<UnknownPacket>(UnknownPacket.INSTANCE, (context, initMessage) ->
                        GlobalTabList.getInstance().get(this.gameProfile).ifPresent(entry -> entry.setLatency(this.latency))));
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        Packet actualPacket = packet;
        if (actualPacket instanceof HandlerPacket) {
            actualPacket = ((HandlerPacket) actualPacket).getPacket();
        }
        if (actualPacket instanceof ClientSettingsPacket) { // Special case, keep track of the locale
            this.locale = ((ClientSettingsPacket) actualPacket).getLocale();
            if (!this.firstClientSettingsMessage) {
                this.firstClientSettingsMessage = true;
                // Trigger the init
                messageReceived(new HandlerPacket<UnknownPacket>(UnknownPacket.INSTANCE,
                        (context, initMessage) -> finalizePlayer()));
            }
        }
        messageReceived(packet);
    }

    /**
     * Handles the inbound {@link Packet} with the specified {@link Handler}.
     *
     * @param handler The handler
     * @param packet The message
     */
    @SuppressWarnings("unchecked")
    private void handleMessage(Handler handler, Packet packet) {
        try {
            handler.handle(this.networkContext, packet);
        } catch (Throwable throwable) {
            Lantern.getLogger().error("Error while handling {}", packet, throwable);
        } finally {
            ReferenceCountUtil.release(packet);
        }
    }

    /**
     * Queues the {@link Packet} to be handled.
     *
     * @param packet The message
     */
    public void queueReceivedMessage(Packet packet) {
        final EventLoop eventLoop = this.channel.eventLoop();
        if (eventLoop.inEventLoop()) {
            messageReceived(packet);
        } else {
            this.channel.eventLoop().execute(() -> messageReceived(packet));
        }
    }

    /**
     * Called when the server received a message from the client.
     *
     * @param packet The message
     */
    @SuppressWarnings("unchecked")
    @NettyThreadOnly
    private void messageReceived(Packet packet) {
        if (packet == UnknownPacket.INSTANCE) {
            return;
        }
        if (packet instanceof KeepAlivePacket) { // Special case
            handleKeepAlive((KeepAlivePacket) packet);
        } else if (packet instanceof BulkPacket) {
            ((BulkPacket) packet).getPackets().forEach(this::messageReceived);
        } else if (packet instanceof HandlerPacket) {
            final HandlerPacket handlerMessage = (HandlerPacket) packet;
            if (handlerMessage.getHandleThread() == HandlerPacket.HandleThread.NETTY) {
                handleMessage(handlerMessage.getHandler(), handlerMessage.getPacket());
            } else if (handlerMessage.getHandleThread() == HandlerPacket.HandleThread.ASYNC) {
                Lantern.getAsyncScheduler().submit(() -> handleMessage(handlerMessage.getHandler(), handlerMessage.getPacket()));
            } else {
                this.messageQueue.add(handlerMessage);
            }
        } else {
            final Class<? extends Packet> messageClass = packet.getClass();
            final MessageRegistration registration = getProtocol().inbound().findByMessageType(messageClass).orElse(null);
            if (registration == null) {
                throw new DecoderException("Failed to find a message registration for " + messageClass.getName() + "!");
            }
            registration.getHandler().ifPresent(handler -> {
                final Handler handler1 = (Handler) handler;
                if (NettyThreadOnlyHelper.INSTANCE.isHandlerNettyThreadOnly((Class) handler1.getClass())) {
                    handleMessage(handler1, packet);
                } else {
                    this.messageQueue.add(new HandlerPacket(packet, handler1));
                }
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.networkManager.onActive(this);
        // If the connection isn't established after 30 seconds,
        // kick the player. 30 seconds is the value used in vanilla
        this.connectionTask = this.channel.eventLoop().schedule(
                () -> close(t("multiplayer.disconnect.slow_login")), 30, TimeUnit.SECONDS);
    }

    @NettyThreadOnly
    private void initKeepAliveTask() {
        if (this.connectionTask != null) {
            this.connectionTask.cancel(true);
        }
        this.connectionTask = this.channel.eventLoop().scheduleAtFixedRate(() -> {
            final ProtocolState protocolState = this.protocolState;
            if (protocolState == ProtocolState.PLAY) {
                final long time = currentTime();
                if (this.keepAliveTime == -1L) {
                    this.keepAliveTime = time;
                    send(new KeepAlivePacket(time));
                } else {
                    close(t("disconnect.timeout"));
                }
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.networkManager.onInactive(this);
        // The player probably just left the server
        if (this.disconnectReason == null) {
            if (this.channel.isOpen()) {
                this.disconnectReason = t("disconnect.endOfStream");
            } else {
                this.disconnectReason = t("multiplayer.disconnect.generic");
            }
        }
        // The player was able to spawn before the connection closed
        if (this.player != null) {
            Lantern.getSyncScheduler().submit(this::leavePlayer);
            Lantern.getLogger().debug("{} ({}) disconnected. Reason: {}", this.gameProfile.getName().orElse("Unknown"),
                    this.channel.remoteAddress(), LanternTexts.toLegacy(this.disconnectReason));
        } else if (getProtocolState() != ProtocolState.STATUS) { // Ignore the status requests
            // The player left before he was able to connect
            Lantern.getLogger().debug("A player{} failed to join from {}. Reason: {}", this.gameProfile == null ? "" :
                            " (" + this.gameProfile.getName().orElse("Unknown") + ')',
                    this.channel.remoteAddress(), LanternTexts.toLegacy(this.disconnectReason));
        }
        this.connectionTask.cancel(false);
        this.connectionTask = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Pipeline error, just log it
        if (cause instanceof CodecException) {
            Lantern.getLogger().error("A netty pipeline error occurred", cause);
        } else {
            if (cause instanceof IOException) {
                final StackTraceElement[] stack = cause.getStackTrace();
                if (stack.length != 0 && stack[0].toString().startsWith("sun.nio.ch.SocketDispatcher.read0")) {
                    return;
                }
            }

            // Use the debug level, don't spam the server with errors
            // caused by client disconnection, ...
            Lantern.getLogger().debug("A netty connection error occurred", cause);

            if (cause instanceof TimeoutException) {
                closeChannel(t("disconnect.timeout"));
            } else {
                closeChannel(t("disconnect.genericReason", "Internal Exception: " + cause));
            }
        }
    }

    @Override
    public LanternPlayer getPlayer() {
        if (this.player == null) {
            throw new IllegalStateException("The player is not yet available.");
        }
        return this.player;
    }

    @Override
    public int getLatency() {
        return this.latency;
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualHostAddress == null ? getAddress() : this.virtualHostAddress;
    }

    /**
     * Sets the virtual host address.
     *
     * @param address The virtual host address
     */
    public void setVirtualHost(@Nullable InetSocketAddress address) {
        this.virtualHostAddress = address;
    }

    /**
     * Gets the {@link Locale}.
     *
     * @return The locale
     */
    @NettyThreadOnly
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Gets the protocol version.
     *
     * @return The protocol version
     */
    @NettyThreadOnly
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Sets the protocol version.
     *
     * @param protocolVersion The protocol version
     */
    @NettyThreadOnly
    public void setProtocolVersion(int protocolVersion) {
        if (this.protocolVersion != -1) {
            throw new IllegalStateException("The protocol version may only be set once.");
        }
        this.protocolVersion = protocolVersion;
    }

    /**
     * Gets the {@link GameProfile}.
     *
     * @return The game profile
     */
    public LanternGameProfile getGameProfile() {
        if (this.gameProfile == null) {
            throw new IllegalStateException("The game profile is not yet available.");
        }
        return this.gameProfile;
    }

    /**
     * Sets the {@link GameProfile}.
     *
     * @param gameProfile The game profile
     */
    public void setGameProfile(GameProfile gameProfile) {
        if (this.gameProfile != null) {
            throw new IllegalStateException("The game profile may only be set once.");
        }
        this.gameProfile = (LanternGameProfile) gameProfile;
    }

    /**
     * Pulses the session. This should be called
     * from the main thread.
     */
    public void pulse() {
        HandlerPacket entry;
        while ((entry = this.messageQueue.poll()) != null) {
            handleMessage(entry.getHandler(), entry.getPacket());
        }
    }

    /**
     * Gets a list with all the installed client mods.
     *
     * @return The installed mods
     */
    public Set<String> getInstalledMods() {
        return this.installedMods;
    }

    /**
     * Gets the registered channels.
     *
     * @return The registered channels
     */
    public Set<String> getRegisteredChannels() {
        return this.registeredChannels;
    }

    /**
     * Gets the {@link LanternServer}.
     *
     * @return The server
     */
    public LanternServerNew getServer() {
        return this.server;
    }

    /**
     * Gets the {@link Channel} of this session.
     *
     * @return The channel
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * Gets the protocol associated with the current type.
     *
     * @return The protocol
     */
    @NettyThreadOnly
    public Protocol getProtocol() {
        return this.protocolState.getProtocol();
    }

    /**
     * Gets the current protocol state.
     *
     * @return The protocol state
     */
    @NettyThreadOnly
    public ProtocolState getProtocolState() {
        return this.protocolState;
    }

    /**
     * Sets the current protocol state.
     *
     * @param state The protocol state
     */
    @NettyThreadOnly
    public void setProtocolState(ProtocolState state) {
        this.protocolState = state;
    }

    /**
     * Closes the channel with a specific disconnect reason, this doesn't
     * send a disconnect message to the client, it just closes the connection.
     *
     * @param reason The reason
     */
    @NettyThreadOnly
    private void closeChannel(Component reason) {
        this.disconnectReason = checkNotNull(reason, "reason");
        this.channel.close();
    }

    /**
     * Sends a {@link Packet} and returns the {@link ChannelFuture}.
     *
     * @param packet The message
     * @return The channel future
     */
    public ChannelFuture sendWithFuture(Packet packet) {
        checkNotNull(packet, "message");
        if (!this.channel.isActive()) {
            return this.channel.newPromise();
        }
        ReferenceCountUtil.retain(packet);
        // Write the message and add a exception handler
        return this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Sends a array of {@link Packet}s and returns the {@link ChannelFuture}.
     *
     * @param packets The messages
     * @return The channel future
     */
    public ChannelFuture sendWithFuture(Packet... packets) {
        checkNotNull(packets, "messages");
        if (packets.length == 0) {
            return this.channel.voidPromise();
        }
        final ChannelPromise promise = this.channel.newPromise();
        if (!this.channel.isActive()) {
            return promise;
        }
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        if (packets.length == 1) {
            this.channel.writeAndFlush(packets[0], promise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            // Retain the messages
            for (Packet packet : packets) {
                ReferenceCountUtil.retain(packet);
            }
            final ChannelPromise voidPromise = this.channel.voidPromise();
            if (eventLoop.inEventLoop()) {
                final int last = packets.length - 1;
                for (int i = 0; i < last; i++) {
                    this.channel.write(packets[i], voidPromise);
                }
                this.channel.writeAndFlush(packets[last], promise);
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Packet> messages0 = ImmutableList.copyOf(packets);
                eventLoop.submit(() -> {
                    final Iterator<Packet> it0 = messages0.iterator();
                    do {
                        final Packet packet0 = it0.next();
                        // Only use a normal channel promise for the last message
                        this.channel.write(packet0, it0.hasNext() ? voidPromise : promise);
                    } while (it0.hasNext());
                    this.channel.flush();
                });
            }
        }
        return promise;
    }

    /**
     * Sends a iterable of {@link Packet}s.
     *
     * @param messages The messages
     */
    public ChannelFuture sendWithFuture(Iterable<Packet> messages) {
        checkNotNull(messages, "messages");
        final Iterator<Packet> it = messages.iterator();
        if (!it.hasNext()) {
            return this.channel.voidPromise();
        }
        final ChannelPromise promise = this.channel.newPromise();
        if (!this.channel.isActive()) {
            return promise;
        }
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        Packet packet = it.next();
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        if (!it.hasNext()) {
            this.channel.writeAndFlush(packet, promise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            messages.forEach(ReferenceCountUtil::retain);
            final ChannelPromise voidPromise = this.channel.voidPromise();
            if (eventLoop.inEventLoop()) {
                while (true) {
                    final boolean next = it.hasNext();
                    // Only use a normal channel promise for the last message
                    this.channel.write(packet, next ? voidPromise : promise);
                    if (!next) {
                        break;
                    }
                    packet = it.next();
                }
                this.channel.flush();
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Packet> messages0 = ImmutableList.copyOf(messages);
                eventLoop.submit(() -> {
                    final Iterator<Packet> it0 = messages0.iterator();
                    do {
                        final Packet packet0 = it0.next();
                        // Only use a normal channel promise for the last message
                        this.channel.writeAndFlush(packet0, it0.hasNext() ? voidPromise : promise);
                    } while (it0.hasNext());
                    this.channel.flush();
                });
            }
        }
        return promise;
    }

    /**
     * Sends a {@link Packet}.
     *
     * @param packet The message
     */
    public void send(Packet packet) {
        checkNotNull(packet, "message");
        if (!this.channel.isActive()) {
            return;
        }
        ReferenceCountUtil.retain(packet);
        // Thrown exceptions will be delegated through the exceptionCaught method
        this.channel.writeAndFlush(packet, this.channel.voidPromise());
    }

    /**
     * Sends a array of {@link Packet}s.
     *
     * @param packets The messages
     */
    public void send(Packet... packets) {
        checkNotNull(packets, "messages");
        if (packets.length == 0 || !this.channel.isActive()) {
            return;
        }
        final ChannelPromise voidPromise = this.channel.voidPromise();
        if (packets.length == 1) {
            this.channel.writeAndFlush(packets[0], voidPromise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            // Retain the messages
            for (Packet packet : packets) {
                ReferenceCountUtil.retain(packet);
            }
            if (eventLoop.inEventLoop()) {
                for (Packet packet : packets) {
                    this.channel.write(packet, voidPromise);
                }
                this.channel.flush();
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Packet> messages0 = ImmutableList.copyOf(packets);
                eventLoop.submit(() -> {
                    for (Packet packet0 : messages0) {
                        this.channel.write(packet0, voidPromise);
                    }
                    this.channel.flush();
                });
            }
        }
    }

    /**
     * Sends a iterable of {@link Packet}s.
     *
     * @param messages The messages
     */
    public void send(Iterable<Packet> messages) {
        checkNotNull(messages, "messages");
        final Iterator<Packet> it = messages.iterator();
        if (!it.hasNext()) {
            return;
        }
        Packet packet = it.next();
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        final ChannelPromise voidPromise = this.channel.voidPromise();
        if (!it.hasNext()) {
            this.channel.writeAndFlush(packet, voidPromise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            messages.forEach(ReferenceCountUtil::retain);
            if (eventLoop.inEventLoop()) {
                for (Packet packet0 : messages) {
                    this.channel.write(packet0, voidPromise);
                }
                this.channel.flush();
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Packet> messages0 = ImmutableList.copyOf(messages);
                eventLoop.submit(() -> {
                    for (Packet packet0 : messages0) {
                        this.channel.write(packet0, voidPromise);
                    }
                    this.channel.flush();
                });
            }
        }
    }

    @Override
    public void close() {
        close(TextComponent.of("Unknown reason."));
    }

    @Override
    public void close(Component reason) {
        checkNotNull(reason, "reason");
        if (this.disconnectReason != null) {
            return;
        }
        this.disconnectReason = reason;
        if (this.channel.isActive() && (this.protocolState == ProtocolState.PLAY ||
                this.protocolState == ProtocolState.LOGIN)) {
            sendWithFuture(new DisconnectPacket(reason)).addListener(ChannelFutureListener.CLOSE);
        } else {
            this.channel.close();
        }
    }

    /**
     * Is called when the {@link LanternPlayer} leaves the
     * server and needs to be cleaned up.
     */
    private void leavePlayer() {
        checkState(this.player != null, "The player must first be available!");
        final LanternWorld world = this.player.getWorld();
        if (world != null) {
            final CauseStack causeStack = CauseStack.current();
            causeStack.pushCause(this.player);

            // Close the open container
            this.player.getContainerSession().setRawOpenContainer(causeStack, null);

            final MessageChannel messageChannel = this.player.getMessageChannel();
            final Text quitMessage = t("multiplayer.player.left", this.player.getName());

            final ServerSideConnectionEvent.Disconnect event = SpongeEventFactory.createServerSideConnectionEventDisconnect(
                    causeStack.getCurrentCause(), messageChannel, Optional.of(messageChannel), this,
                    new MessageEvent.MessageFormatter(quitMessage), this.player, false);

            Sponge.getEventManager().post(event);
            if (!event.isMessageCancelled()) {
                event.getChannel().ifPresent(channel -> channel.send(this.player, event.getMessage()));
            }

            causeStack.popCause();

            // Remove the proxy user from the player and save the player data
            this.player.getProxyUser().setInternalUser(null);
            // Destroy the player entity
            this.player.remove(LanternEntity.RemoveState.DESTROYED);
            // Detach the player from the world
            this.player.setWorld(null);
            // Release the players entity id
            EntityProtocolManager.releaseEntityId(this.player.getNetworkId());
        }
    }

    /**
     * Pre initializes the {@link LanternPlayer}, after this state we need
     * to wait for the client to send a {@link ClientSettingsPacket}
     * so that we have the {@link Locale} before we start sending translated
     * {@link Text} objects.
     */
    public void initPlayer() {
        initKeepAliveTask();
        if (this.gameProfile == null) {
            throw new IllegalStateException("The game profile must first be available!");
        }
        this.player = new LanternPlayer(this.gameProfile, this);
        this.player.setNetworkId(EntityProtocolManager.acquireEntityId());

        // Actually too early to send this, but we want to trigger
        // the client settings to be send to the server, respawn
        // messages will be send afterwards with the proper values
        send(new PlayerJoinPacket(GameModes.SURVIVAL.get(), DimensionTypes.OVERWORLD.get(),
                this.player.getNetworkId(), getServer().getMaxPlayers(), false, false, false,
                this.player.getServerViewDistance(), true, 0L));
    }

    /**
     * Finally initializes the {@link LanternPlayer} instance
     * and spawns it in a world if permitted to join
     * the server.
     */
    private void finalizePlayer() {
        this.player.setEntityProtocolType(EntityProtocolTypes.PLAYER);

        LanternWorld world = this.player.getWorld();
        if (world == null) {
            LanternWorldPropertiesOld worldProperties = this.player.getUserWorld();
            boolean fixSpawnLocation = false;
            if (worldProperties == null) {
                Lantern.getLogger().warn("The player [{}] attempted to login in a non-existent world, this is not possible "
                        + "so we have moved them to the default's world spawn point.", this.gameProfile.getName().get());
                worldProperties = (LanternWorldPropertiesOld) Lantern.getServer().getDefaultWorld().get();
                fixSpawnLocation = true;
            } else if (!worldProperties.isEnabled()) {
                Lantern.getLogger().warn("The player [{}] attempted to login in a unloaded and not-enabled world [{}], this is not possible "
                        + "so we have moved them to the default's world spawn point.", this.gameProfile.getName().get(),
                        worldProperties.getWorldName());
                worldProperties = (LanternWorldPropertiesOld) Lantern.getServer().getDefaultWorld().get();
                fixSpawnLocation = true;
            }
            final Optional<World> optWorld = Lantern.getWorldManager().loadWorld(worldProperties);
            // Use the raw method to avoid triggering any network messages
            this.player.setRawWorld((LanternWorld) optWorld.get());
            this.player.setUserWorld(null);
            if (fixSpawnLocation) {
                // TODO: Use a proper spawn position
                this.player.setRawPosition(new Vector3d(0, 100, 0));
                this.player.setRawRotation(new Vector3d(0, 0, 0));
            }
        }

        // The kick reason
        Text kickReason = null;

        final BanService banService = Sponge.getServiceProvider().provide(BanService.class).get();
        // Check whether the player is banned and kick if necessary
        Ban ban = banService.getBanFor(this.gameProfile).orElse(null);
        if (ban == null) {
            final SocketAddress address = getChannel().remoteAddress();
            if (address instanceof InetSocketAddress) {
                ban = banService.getBanFor(((InetSocketAddress) address).getAddress()).orElse(null);
            }
        }
        if (ban != null) {
            final Optional<Instant> optExpirationDate = ban.getExpirationDate();
            final Optional<Text> optReason = ban.getReason();

            // Generate the kick message
            Text.Builder builder = Text.builder();
            if (ban instanceof Ban.Profile) {
                builder.append(t("multiplayer.disconnect.ban.banned"));
            } else {
                builder.append(t("multiplayer.disconnect.ban.ip_banned"));
            }
            // There is optionally a reason
            optReason.ifPresent(reason -> builder.append(Text.newLine()).append(t("multiplayer.disconnect.ban.reason", reason)));
            // And a expiration date if present
            optExpirationDate.ifPresent(expirationDate -> {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(tr("multiplayer.disconnect.ban.expiration_date_format").get());
                builder.append(Text.newLine()).append(t("multiplayer.disconnect.ban.expiration", formatter.format(expirationDate)));
            });

            kickReason = builder.build();
            // Check for white-list
        } else if (!isWhitelisted(this.gameProfile)) {
            kickReason = t("multiplayer.disconnect.not_whitelisted");
            // Check whether the server is full
        } else if (Lantern.getServer().getOnlinePlayers().size() >= Lantern.getServer().getMaxPlayers()
                && !canBypassPlayerLimit(this.gameProfile)) {
            kickReason = t("multiplayer.disconnect.server_full");
        }

        final MessageEvent.MessageFormatter messageFormatter = new MessageEvent.MessageFormatter(
                kickReason != null ? kickReason : t("multiplayer.disconnect.not_allowed_to_join"));

        final Cause cause = Cause.builder().append(this).build(EventContext.builder().add(EventContextKeys.PLAYER, this.player).build());
        final Location fromLocation = this.player.getLocation();
        final ServerSideConnectionEvent.Login loginEvent = SpongeEventFactory.createServerSideConnectionEventLogin(cause,
                fromLocation, fromLocation, world, world, this, messageFormatter, this.player, false);

        if (kickReason != null) {
            loginEvent.setCancelled(true);
        }

        Sponge.getEventManager().post(loginEvent);
        if (loginEvent.isCancelled()) {
            close(loginEvent.isMessageCancelled() ? t("multiplayer.disconnect.generic") : loginEvent.getMessage());
            return;
        }

        Lantern.getLogger().debug("The player {} successfully to joined from {}.",
                this.gameProfile.getName().get(), this.channel.remoteAddress());

        // Update the first join and last played data
        final Instant lastJoined = Instant.now();
        this.player.offer(Keys.LAST_DATE_PLAYED, lastJoined);
        if (!this.player.get(Keys.FIRST_DATE_PLAYED).isPresent()) {
            this.player.offer(Keys.FIRST_DATE_PLAYED, lastJoined);
        }

        final Transform toTransform = loginEvent.getToTransform();
        world = (LanternWorld) loginEvent.getToWorld();
        final WorldConfig config = world.getProperties().getConfig();

        // Update the game mode if necessary
        if (config.isGameModeForced() || this.player.get(Keys.GAME_MODE).get().equals(GameModes.NOT_SET)) {
            this.player.offer(Keys.GAME_MODE, config.getGameMode());
        }

        // Send the server brand
        send(new PacketPlayInOutBrand(Lantern.getImplementationPlugin().getName()));

        // Reset the raw world
        this.player.setRawWorld(null);
        // Set the transform, this will trigger the initial
        // network messages to be send
        this.player.setTransform(toTransform);

        final MessageChannel messageChannel = this.player.getMessageChannel();
        final Text joinMessage;

        final GameProfile previousProfile = this.channel.attr(PREVIOUS_GAME_PROFILE).getAndSet(null);
        if (previousProfile != null && previousProfile.getName().isPresent() &&
                !previousProfile.getName().get().equals(this.gameProfile.getName().get())) {
            joinMessage = t("multiplayer.player.joined.renamed", this.player.getName(), previousProfile.getName().get());
        } else {
            joinMessage = t("multiplayer.player.joined", this.player.getName());
        }

        final ClientConnectionEvent.Join joinEvent = SpongeEventFactory.createClientConnectionEventJoin(cause, messageChannel,
                Optional.of(messageChannel), new MessageEvent.MessageFormatter(joinMessage), this.player, false);

        Sponge.getEventManager().post(joinEvent);
        if (!joinEvent.isMessageCancelled()) {
            joinEvent.getChannel().ifPresent(channel -> channel.send(this.player, joinEvent.getMessage()));
        }

        this.server.getDefaultResourcePack().ifPresent(this.player::sendResourcePack);
        this.player.resetIdleTimeoutCounter();
    }

    private static boolean canBypassPlayerLimit(GameProfile gameProfile) {
        final PermissionService permissionService = Sponge.getServiceManager().provideUnchecked(PermissionService.class);
        return permissionService.getUserSubjects()
                .getSubject(gameProfile.getUniqueId().toString())
                        .map(subject -> subject.hasPermission(Permissions.Login.BYPASS_PLAYER_LIMIT_PERMISSION))
                .orElse(false);
    }

    private static boolean isWhitelisted(GameProfile gameProfile) {
        if (!Lantern.getGame().getGlobalConfig().isWhitelistEnabled()) {
            return true;
        }
        final WhitelistService whitelistService = Sponge.getServiceManager().provideUnchecked(WhitelistService.class);
        if (whitelistService.isWhitelisted(gameProfile)) {
            return true;
        }
        final PermissionService permissionService = Sponge.getServiceManager().provideUnchecked(PermissionService.class);
        return permissionService.getUserSubjects()
                .getSubject(gameProfile.getUniqueId().toString())
                .map(subject -> subject.hasPermission(Permissions.Login.BYPASS_WHITELIST_PERMISSION))
                .orElse(false);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("address", getAddress())
                .add("virtualHost", this.virtualHostAddress)
                .add("profile", this.gameProfile)
                .add("protocolVersion", this.protocolVersion)
                .add("protocolState", this.protocolState)
                .toString();
    }
}
