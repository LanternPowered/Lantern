/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
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
package org.lanternpowered.server.network;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import com.flowpowered.math.vector.Vector3d;
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
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.config.user.ban.BanEntry;
import org.lanternpowered.server.config.world.WorldConfig;
import org.lanternpowered.server.data.io.PlayerIO;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.entity.living.player.tab.GlobalTabList;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.entity.EntityProtocolManager;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.lanternpowered.server.network.message.AsyncHelper;
import org.lanternpowered.server.network.message.BulkMessage;
import org.lanternpowered.server.network.message.HandlerMessage;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistration;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutKeepAlive;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.world.LanternWorld;
import org.lanternpowered.server.world.LanternWorldProperties;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public final class NetworkSession extends SimpleChannelInboundHandler<Message> implements PlayerConnection {

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
     * The attribute key for the FML (Forge Mod Loader) marker.
     */
    public static final AttributeKey<Boolean> FML_MARKER = AttributeKey.valueOf("fml-marker");

    /**
     * The formatter that is used to format the date used in the ban disconnect message.
     */
    private static final DateTimeFormatter BAN_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm:ss z");

    private final NetworkManager networkManager;
    private final LanternServer server;
    private final Channel channel;

    /**
     * A shared random.
     */
    private final Random random = new Random();

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
    @Nullable private volatile Text disconnectReason;

    /**
     * A queue of incoming messages that must be handled on
     * the synchronous thread.
     */
    private final Queue<HandlerMessage> messageQueue = new ConcurrentLinkedDeque<>();

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
     * The id that was used in the keep alive message.
     */
    private int keepAliveId = -1;

    /**
     * The time that the last the keep alive message was send.
     */
    private long keepAliveTime;

    /**
     * The protocol version.
     */
    private int protocolVersion = -1;

    public NetworkSession(Channel channel, LanternServer server, NetworkManager networkManager) {
        this.networkManager = networkManager;
        this.channel = channel;
        this.server = server;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // Send a keep alive message to the client every 40 ticks (2 seconds),
        // doing this also in the event loop to keep it separate from the main
        // thread.
        this.channel.eventLoop().scheduleAtFixedRate(() -> {
            final ProtocolState protocolState = this.protocolState;
            if (protocolState == ProtocolState.PLAY || protocolState == ProtocolState.FORGE_HANDSHAKE) {
                this.keepAliveId = this.random.nextInt();
                this.keepAliveTime = System.currentTimeMillis();
                this.send(new MessageInOutKeepAlive(this.keepAliveId));
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void handleKeepAlive(MessageInOutKeepAlive message) {
        if (this.keepAliveId == message.getId()) {
            // Calculate the latency
            this.latency = (int) ((this.latency * 3 + (System.currentTimeMillis() - this.keepAliveTime) / 1000L) / 4);
            if (this.gameProfile != null) {
                // Update the global tab list
                GlobalTabList.getInstance().get(this.gameProfile).ifPresent(entry -> entry.setLatency(this.latency));
            }
        } else {
            Lantern.getLogger().debug("A keep alive message {} didn't receive a response in time, "
                    + "the next message is already been send.", this.keepAliveId);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        this.messageReceived(message);
    }

    /**
     * Handles the inbound {@link Message} with the specified {@link Handler}.
     *
     * @param handler The handler
     * @param message The message
     */
    @SuppressWarnings("unchecked")
    private void handleMessage(Handler handler, Message message) {
        try {
            handler.handle(this.networkContext, message);
        } catch (Throwable throwable) {
            Lantern.getLogger().error("Error while handling {}", message, throwable);
        }
    }

    /**
     * Called when the server received a message from the client.
     *
     * @param message The message
     */
    @SuppressWarnings("unchecked")
    public void messageReceived(Message message) {
        if (message instanceof MessageInOutKeepAlive) { // Special case
            this.handleKeepAlive((MessageInOutKeepAlive) message);
        } else if (message == NullMessage.INSTANCE) {
            // Ignore
        } else if (message instanceof BulkMessage) {
            ((BulkMessage) message).getMessages().forEach(this::messageReceived);
        } else if (message instanceof HandlerMessage) {
            final HandlerMessage handlerMessage = (HandlerMessage) message;
            if (AsyncHelper.isAsyncMessage(handlerMessage.getMessage()) ||
                    AsyncHelper.isAsyncHandler(handlerMessage.getHandler())) {
                this.handleMessage(handlerMessage.getHandler(), handlerMessage.getMessage());
            } else {
                this.messageQueue.add(handlerMessage);
            }
        } else {
            final Class<? extends Message> messageClass = message.getClass();
            final MessageRegistration registration = this.getProtocol().inbound().findByMessageType(messageClass).orElse(null);
            if (registration == null) {
                throw new DecoderException("Failed to find a message registration for " + messageClass.getName() + "!");
            }
            registration.getHandler().ifPresent(handler -> {
                final Handler handler1 = (Handler) handler;
                if (AsyncHelper.isAsyncMessage(message) || AsyncHelper.isAsyncHandler(handler1)) {
                    this.handleMessage(handler1, message);
                } else {
                    this.messageQueue.add(new HandlerMessage(message, handler1));
                }
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.networkManager.onActive(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.networkManager.onInactive(this);
        // The player probably just left the server
        if (this.disconnectReason == null) {
            if (this.channel.isOpen()) {
                this.disconnectReason = t("disconnect.endOfStream");
            } else {
                this.disconnectReason = t("disconnect.leftServer");
            }
        }
        // The player was able to spawn before the connection closed
        if (this.player != null) {
            this.leavePlayer();
        }
        Lantern.getLogger().info("{} ({}) disconnected. Reason: {}", this.gameProfile == null ? "???" : this.gameProfile.getName().orElse("???"),
                this.channel.remoteAddress(), LanternTexts.toLegacy(this.disconnectReason));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Pipeline error, just log it
        if (cause instanceof CodecException) {
            Lantern.getLogger().error("A netty pipeline error occurred", cause);
        } else {
            // Use the debug level, don't spam the server with errors
            // caused by client disconnection, ...
            Lantern.getLogger().debug("A netty connection error occurred", cause);

            if (cause instanceof TimeoutException) {
                this.closeChannel(t("disconnect.timeout"));
            } else {
                this.closeChannel(t("disconnect.genericReason", "Internal Exception: " + cause));
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
        return this.virtualHostAddress == null ? this.getAddress() : this.virtualHostAddress;
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
     * Gets the protocol version.
     *
     * @return The protocol version
     */
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Sets the protocol version.
     *
     * @param protocolVersion The protocol version
     */
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
        HandlerMessage entry;
        while ((entry = this.messageQueue.poll()) != null) {
            this.handleMessage(entry.getHandler(), entry.getMessage());
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
    public LanternServer getServer() {
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
    public Protocol getProtocol() {
        return this.protocolState.getProtocol();
    }

    /**
     * Gets the current protocol state.
     *
     * @return The protocol state
     */
    public ProtocolState getProtocolState() {
        return this.protocolState;
    }

    /**
     * Sets the current protocol state.
     *
     * @param state The protocol state
     */
    public void setProtocolState(ProtocolState state) {
        this.protocolState = state;
    }

    /**
     * Closes the channel with a specific disconnect reason, this doesn't
     * send a disconnect message to the client, it just closes the connection.
     *
     * @param reason The reason
     */
    private void closeChannel(Text reason) {
        this.disconnectReason = checkNotNull(reason, "reason");
        this.channel.close();
    }

    /**
     * Sends a {@link Message} and returns the {@link ChannelFuture}.
     *
     * @param message The message
     * @return The channel future
     */
    public ChannelFuture sendWithFuture(Message message) {
        checkNotNull(message, "message");
        if (!this.channel.isActive()) {
            return this.channel.newPromise();
        }
        // Write the message and add a exception handler
        return this.channel.writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * Sends a array of {@link Message}s and returns the {@link ChannelFuture}.
     *
     * @param messages The messages
     * @return The channel future
     */
    public ChannelFuture sendWithFuture(Message... messages) {
        checkNotNull(messages, "messages");
        checkArgument(messages.length != 0, "messages cannot be empty");
        final ChannelPromise promise = this.channel.newPromise();
        if (!this.channel.isActive()) {
            return promise;
        }
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        if (messages.length == 1) {
            this.channel.writeAndFlush(messages[0], promise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            final ChannelPromise voidPromise = this.channel.voidPromise();
            if (eventLoop.inEventLoop()) {
                final int last = messages.length - 1;
                for (int i = 0; i < last; i++) {
                    ReferenceCountUtil.retain(messages[i]);
                    this.channel.writeAndFlush(messages[i], voidPromise);
                }
                ReferenceCountUtil.retain(messages[last]);
                this.channel.writeAndFlush(messages[last], promise);
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Message> messages0 = ImmutableList.copyOf(messages);
                messages0.forEach(ReferenceCountUtil::retain);
                eventLoop.submit(() -> {
                    final Iterator<Message> it0 = messages0.iterator();
                    do {
                        final Message message0 = it0.next();
                        // Only use a normal channel promise for the last message
                        this.channel.writeAndFlush(message0, it0.hasNext() ? voidPromise : promise);
                    } while (it0.hasNext());
                });
            }
        }
        return promise;
    }

    /**
     * Sends a iterable of {@link Message}s.
     *
     * @param messages The messages
     */
    public ChannelFuture sendWithFuture(Iterable<Message> messages) {
        checkNotNull(messages, "messages");
        final Iterator<Message> it = messages.iterator();
        checkArgument(it.hasNext(), "messages cannot be empty");
        final ChannelPromise promise = this.channel.newPromise();
        if (!this.channel.isActive()) {
            return promise;
        }
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        Message message = it.next();
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        if (!it.hasNext()) {
            this.channel.writeAndFlush(message, promise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            final ChannelPromise voidPromise = this.channel.voidPromise();
            if (eventLoop.inEventLoop()) {
                while (true) {
                    final boolean next = it.hasNext();
                    // Only use a normal channel promise for the last message
                    this.channel.writeAndFlush(message, next ? voidPromise : promise);
                    if (!next) {
                        break;
                    }
                    message = it.next();
                    ReferenceCountUtil.retain(message);
                }
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Message> messages0 = ImmutableList.copyOf(messages);
                messages0.forEach(ReferenceCountUtil::retain);
                eventLoop.submit(() -> {
                    final Iterator<Message> it0 = messages0.iterator();
                    do {
                        final Message message0 = it0.next();
                        // Only use a normal channel promise for the last message
                        this.channel.writeAndFlush(message0, it0.hasNext() ? voidPromise : promise);
                    } while (it0.hasNext());
                });
            }
        }
        return promise;
    }

    /**
     * Sends a {@link Message}.
     *
     * @param message The message
     */
    public void send(Message message) {
        checkNotNull(message, "message");
        if (!this.channel.isActive()) {
            return;
        }
        ReferenceCountUtil.retain(message);
        // Thrown exceptions will be delegated through the exceptionCaught method
        this.channel.writeAndFlush(message, this.channel.voidPromise());
    }

    /**
     * Sends a array of {@link Message}s.
     *
     * @param messages The messages
     */
    public void send(Message... messages) {
        checkNotNull(messages, "messages");
        checkArgument(messages.length != 0, "messages cannot be empty");
        if (!this.channel.isActive()) {
            return;
        }
        final ChannelPromise voidPromise = this.channel.voidPromise();
        if (messages.length == 1) {
            this.channel.writeAndFlush(messages[0], voidPromise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            if (eventLoop.inEventLoop()) {
                for (Message message : messages) {
                    ReferenceCountUtil.retain(message);
                    this.channel.writeAndFlush(message, voidPromise);
                }
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Message> messages0 = ImmutableList.copyOf(messages);
                messages0.forEach(ReferenceCountUtil::retain);
                eventLoop.submit(() -> {
                    for (Message message0 : messages0) {
                        this.channel.writeAndFlush(message0, voidPromise);
                    }
                });
            }
        }
    }

    /**
     * Sends a iterable of {@link Message}s.
     *
     * @param messages The messages
     */
    public void send(Iterable<Message> messages) {
        checkNotNull(messages, "messages");
        final Iterator<Message> it = messages.iterator();
        checkArgument(it.hasNext(), "messages cannot be empty");
        Message message = it.next();
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        final ChannelPromise voidPromise = this.channel.voidPromise();
        if (!it.hasNext()) {
            this.channel.writeAndFlush(message, voidPromise);
        } else {
            final EventLoop eventLoop = this.channel.eventLoop();
            if (eventLoop.inEventLoop()) {
                for (Message message0 : messages) {
                    this.channel.writeAndFlush(message0, voidPromise);
                }
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                final List<Message> messages0 = ImmutableList.copyOf(messages);
                eventLoop.submit(() -> {
                    for (Message message0 : messages0) {
                        this.channel.writeAndFlush(message0, voidPromise);
                    }
                });
            }
        }
    }

    /**
     * Disconnects the session with a unknown reason.
     */
    public void disconnect() {
        this.disconnect(Text.of("Unknown reason."));
    }

    /**
     * Disconnects the session with a specific reason.
     *
     * @param reason The reason
     */
    public void disconnect(Text reason) {
        checkNotNull(reason, "reason");
        if (this.disconnectReason != null) {
            return;
        }
        this.disconnectReason = reason;
        if (this.channel.isActive() && (this.protocolState == ProtocolState.PLAY ||
                this.protocolState == ProtocolState.LOGIN || this.protocolState == ProtocolState.FORGE_HANDSHAKE)) {
            this.sendWithFuture(new MessageOutDisconnect(reason)).addListener(ChannelFutureListener.CLOSE);
        } else {
            this.channel.close();
        }
    }

    /**
     * Is called when the {@link LanternPlayer} leaves the
     * server and needs to be cleaned up.
     */
    private void leavePlayer() {
        if (this.player == null) {
            throw new IllegalStateException("The player must first be available!");
        }
        final LanternWorld world = this.player.getWorld();
        //noinspection ConstantConditions
        if (world != null) {
            final MessageChannel messageChannel = this.player.getMessageChannel();
            final Text quitMessage = t("multiplayer.player.left", this.player.getName());

            final ClientConnectionEvent.Disconnect event = SpongeEventFactory.createClientConnectionEventDisconnect(
                    Cause.source(this.player).build(), messageChannel, Optional.of(messageChannel),
                    new MessageEvent.MessageFormatter(quitMessage), this.player, false);

            Sponge.getEventManager().post(event);
            if (!event.isMessageCancelled()) {
                event.getChannel().ifPresent(channel -> channel.send(this.player, event.getMessage()));
            }

            // Save the player data
            try {
                PlayerIO.save(Lantern.getGame().getSavesDirectory(), this.player);
            } catch (IOException e) {
                //noinspection ConstantConditions
                Lantern.getLogger().warn("An error occurred while saving the player data of {} ({})", this.gameProfile.getName().get(),
                        this.gameProfile.getUniqueId(), e);
            }

            this.player.getContainerSession().setOpenContainer(null);
            this.player.remove(LanternEntity.RemoveState.DESTROYED);
            this.player.setWorld(null);
            EntityProtocolManager.releaseEntityId(this.player.getNetworkId());
        }
    }

    /**
     * Initializes the {@link LanternPlayer} instance
     * and spawns it in a world if permitted to join
     * the server.
     */
    public void initPlayer() {
        if (this.gameProfile == null) {
            throw new IllegalStateException("The game profile must first be available!");
        }
        this.player = new LanternPlayer(this.gameProfile, this);
        this.player.setNetworkId(EntityProtocolManager.acquireEntityId());
        this.player.setEntityProtocolType(EntityProtocolTypes.PLAYER);

        try {
            PlayerIO.load(Lantern.getGame().getSavesDirectory(), this.player);
        } catch (IOException e) {
            Lantern.getLogger().warn("An error occurred while loading the player data", e);
        }

        LanternWorld world = this.player.getWorld();
        //noinspection ConstantConditions
        if (world == null) {
            LanternWorldProperties worldProperties = this.player.getTempWorld();
            boolean fixSpawnLocation = false;
            if (worldProperties == null) {
                Lantern.getLogger().warn("The player [{}] attempted to login in a non-existent world, this is not possible "
                        + "so we have moved them to the default's world spawn point.", this.gameProfile.getName().get());
                worldProperties = (LanternWorldProperties) Lantern.getServer().getDefaultWorld().get();
                fixSpawnLocation = true;
            } else if (!worldProperties.isEnabled()) {
                Lantern.getLogger().warn("The player [{}] attempted to login in a unloaded and not-enabled world [{}], this is not possible "
                        + "so we have moved them to the default's world spawn point.", this.gameProfile.getName().get(),
                        worldProperties.getWorldName());
                worldProperties = (LanternWorldProperties) Lantern.getServer().getDefaultWorld().get();
                fixSpawnLocation = true;
            }
            final Optional<World> optWorld = Lantern.getWorldManager().loadWorld(worldProperties);
            // Use the raw method to avoid triggering any network messages
            this.player.setRawWorld((LanternWorld) optWorld.get());
            this.player.setTempWorld(null);
            if (fixSpawnLocation) {
                // TODO: Use a proper spawn position
                this.player.setRawPosition(new Vector3d(0, 100, 0));
                this.player.setRawRotation(new Vector3d(0, 0, 0));
            }
        }

        // The kick reason
        Text kickReason = null;

        final BanConfig banConfig = Lantern.getGame().getBanConfig();
        // Check whether the player is banned and kick if necessary
        Optional<BanEntry> optBanEntry = banConfig.getEntryByProfile(gameProfile);
        if (!optBanEntry.isPresent()) {
            SocketAddress address = this.getChannel().remoteAddress();
            if (address instanceof InetSocketAddress) {
                optBanEntry = banConfig.getEntryByIp(((InetSocketAddress) address).getAddress());
            }
        }
        if (optBanEntry.isPresent()) {
            final BanEntry banEntry = optBanEntry.get();
            final Optional<Instant> optExpirationDate = banEntry.getExpirationDate();
            final Optional<Text> optReason = banEntry.getReason();

            // Generate the kick message
            Text.Builder builder = Text.builder();
            if (banEntry instanceof Ban.Profile) {
                builder.append(Text.of("You are banned from this server!"));
            } else {
                builder.append(Text.of("Your IP address is banned from this server!"));
            }
            // There is optionally a reason
            optReason.ifPresent(reason -> builder.append(Text.of("\nReason: ", reason)));
            // And a expiration date if present
            optExpirationDate.ifPresent(expirationDate ->
                    builder.append(Text.of("\nYour ban will be removed on ", BAN_TIME_FORMATTER.format(expirationDate))));

            kickReason = builder.build();
            // Check for white-list
        } else if (Lantern.getGame().getGlobalConfig().isWhitelistEnabled() &&
                !Lantern.getGame().getWhitelistConfig().isWhitelisted(this.gameProfile) &&
                !Lantern.getGame().getOpsConfig().getEntryByProfile(this.gameProfile).isPresent()) {
            kickReason = Text.of("You are not white-listed on this server!");
            // Check whether the server is full
        } else if (Lantern.getServer().getOnlinePlayers().size() >= Lantern.getServer().getMaxPlayers()) {
            kickReason = Text.of("The server is full!");
        }

        final MessageEvent.MessageFormatter messageFormatter = new MessageEvent.MessageFormatter(
                kickReason != null ? kickReason : t("disconnect.notAllowedToJoin"));

        final Cause cause = Cause.source(this.player).build();
        final Transform<World> fromTransform = this.player.getTransform();
        final ClientConnectionEvent.Login loginEvent = SpongeEventFactory.createClientConnectionEventLogin(cause,
                fromTransform, fromTransform, this, messageFormatter, this.gameProfile, this.player, false);

        if (kickReason != null) {
            loginEvent.setCancelled(true);
        }

        Sponge.getEventManager().post(loginEvent);
        if (loginEvent.isCancelled()) {
            this.disconnect(loginEvent.isMessageCancelled() ? t("disconnect.disconnected") : loginEvent.getMessage());
            return;
        }

        // Update the first join and last played data
        final Instant lastJoined = Instant.now();
        this.player.offer(Keys.LAST_DATE_PLAYED, lastJoined);
        if (!this.player.get(Keys.FIRST_DATE_PLAYED).isPresent()) {
            this.player.offer(Keys.FIRST_DATE_PLAYED, lastJoined);
        }

        final Transform<World> toTransform = loginEvent.getToTransform();
        world = (LanternWorld) toTransform.getExtent();
        final WorldConfig config = world.getProperties().getConfig();

        // Update the game mode if necessary
        if (config.isGameModeForced() || this.player.get(Keys.GAME_MODE).get().equals(GameModes.NOT_SET)) {
            this.player.offer(Keys.GAME_MODE, config.getGameMode());
        }

        // Reset the raw world
        this.player.setRawWorld(null);
        // Set the transform, this will trigger the initial
        // network messages to be send
        this.player.setTransform(toTransform);

        final MessageChannel messageChannel = this.player.getMessageChannel();
        final Text joinMessage = t("multiplayer.player.joined", this.player.getName());

        final ClientConnectionEvent.Join joinEvent = SpongeEventFactory.createClientConnectionEventJoin(cause, messageChannel,
                Optional.of(messageChannel), new MessageEvent.MessageFormatter(joinMessage), this.player, false);

        Sponge.getEventManager().post(joinEvent);
        if (!joinEvent.isMessageCancelled()) {
            joinEvent.getChannel().ifPresent(channel -> channel.send(this.player, joinEvent.getMessage()));
        }

        this.player.resetIdleTimeoutCounter();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("address", this.getAddress())
                .add("virtualHost", this.virtualHostAddress)
                .add("profile", this.gameProfile)
                .add("protocolVersion", this.protocolVersion)
                .add("protocolState", this.protocolState)
                .toString();
    }
}
