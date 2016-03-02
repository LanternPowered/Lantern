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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.session;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Sets;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AttributeKey;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.entity.EntityIdAllocator;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.AsyncHelper;
import org.lanternpowered.server.network.message.BulkMessage;
import org.lanternpowered.server.network.message.HandlerMessage;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.MessageRegistration;
import org.lanternpowered.server.network.message.NullMessage;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.pipeline.MessageEncryptionHandler;
import org.lanternpowered.server.network.pipeline.NoopHandler;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn.ProxyData;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;

@SuppressWarnings({"rawtypes","unchecked"})
public class Session implements PlayerConnection {

    public static final String ENCRYPTION = "encryption";
    public static final String LEGACY_PING = "legacyping";
    public static final String COMPRESSION = "compression";
    public static final String FRAMING = "framing";
    public static final String CODECS = "codecs";
    public static final String PROCESSOR = "processor";
    public static final String HANDLER = "handler";

    public static final AttributeKey<Boolean> FML_MARKER = AttributeKey.valueOf("fml-marker");
    public static final AttributeKey<Session> SESSION = AttributeKey.valueOf("session");
    public static final AttributeKey<ProtocolState> STATE = AttributeKey.valueOf("state");

    // The game profile of the player
    private volatile LanternGameProfile gameProfile;

    // The random for this session
    private final Random random = new Random();

    // A queue of incoming messages that must be handled on
    // the synchronous thread
    private final Queue<HandlerMessage> messageQueue = new ArrayDeque<>();

    // The network context used by the handlers
    private final NetworkContext networkContext = new NetworkContext() {
        @Override
        public Session getSession() {
            return Session.this;
        }

        @Override
        public Channel getChannel() {
            return channel;
        }
    };

    // A list with all the registered channels (client)
    private final Set<String> registeredChannels = Sets.newHashSet();

    // A list with all the installed mods (client)
    private final Set<String> installedMods = Sets.newHashSet();

    // The server this session belongs to.
    private final LanternServer server;

    // The channel this session is associated with
    private final Channel channel;

    // The remote address of the connection
    private final InetSocketAddress address;

    // The virtual address of the connection, this is the address
    // that was used to join the server
    private InetSocketAddress virtualAddress;

    // The verify token used in authentication
    private byte[] verifyToken;

    // The verify user name used in authentication
    private String verifyUsername;

    // A message describing under what circumstances the connection ended
    private Text quitReason;

    // The player associated with this session (if there is one)
    @Nullable
    private LanternPlayer player;

    // The current protocol version
    private int protocolVersion = -1;

    // The id of the last ping message sent, used to ensure the client responded
    // correctly
    private int pingMessageId;

    // The current ping of the channel
    private volatile int ping;

    // The last ping time
    private long pingTimeStart;

    @Nullable
    private ProxyData proxyData;

    /**
     * Creates a new session.
     * 
     * @param server The server this session belongs to.
     * @param channel The channel associated with this session.
     */
    public Session(LanternServer server, Channel channel) {
        this.address = (InetSocketAddress) channel.remoteAddress();
        this.channel = channel;
        this.server = server;
    }

    /**
     * Gets the game profile.
     * 
     * @return the game profile
     */
    public LanternGameProfile getGameProfile() {
        return this.gameProfile;
    }

    /**
     * Gets a list with all the installed mods. (Client side.)
     * 
     * @return the installed mods
     */
    public Set<String> getInstalledMods() {
        return this.installedMods;
    }

    /**
     * Gets the registered channels. (Client side.)
     * 
     * @return the registered channel
     */
    public Set<String> getRegisteredChannels() {
        return this.registeredChannels;
    }

    @Nullable
    public ProxyData getProxyData() {
        return this.proxyData;
    }

    public void setProxyData(@Nullable ProxyData proxyData) {
        this.proxyData = proxyData;
    }

    /**
     * Gets the channel that is assigned to this session.
     * 
     * @return the channel
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * Gets the protocol associated with the current type.
     * 
     * @return the protocol
     */
    public Protocol getProtocol() {
        return this.channel.attr(STATE).get().getProtocol();
    }

    /**
     * Gets the current protocol state.
     * 
     * @return the state
     */
    public ProtocolState getProtocolState() {
        return this.channel.attr(STATE).get();
    }

    /**
     * Sets the current protocol state.
     * 
     * @param state the state
     */
    public void setProtocolState(ProtocolState state) {
        this.channel.attr(STATE).set(state);
    }

    /**
     * Gets the server associated with this session.
     * 
     * @return the server
     */
    public LanternServer getServer() {
        return this.server;
    }

    /**
     * Gets whether the session active is.
     * 
     * @return is active
     */
    public boolean isActive() {
        return this.channel.isActive();
    }

    /**
     * Sends a message across the network and returns a future result.
     * 
     * @param message the message
     * @return the future
     */
    public ChannelFuture send(Message message) {
        checkNotNull(message, "message");
        if (!this.channel.isActive()) {
            // Discard messages sent if we're closed, since this happens a lot
            return null;
        }
        return this.channel.writeAndFlush(message).addListener(future -> {
            if (future.cause() != null) {
                this.onOutboundThrowable(future.cause());
            }
        });
    }

    /**
     * Sends any amount of messages to the client.
     * 
     * @param messages the messages
     */
    public ChannelFuture sendAll(Message... messages) {
        checkNotNull(messages, "messages");
        checkArgument(messages.length != 0, "messages cannot be empty");
        ChannelFuture future = null;
        for (Message message : messages) {
            future = this.send(message);
        }
        return future;
    }

    /**
     * Sends a collection of messages to the client.
     * 
     * @param messages the messages
     */
    public ChannelFuture sendAll(Iterable<Message> messages) {
        checkNotNull(messages, "messages");
        Iterator<Message> it = messages.iterator();
        checkArgument(it.hasNext(), "messages cannot be empty");
        ChannelFuture future = null;
        while (it.hasNext()) {
            future = this.send(it.next());
        }
        return future;
    }

    /**
     * Handles the inbound message. Also requires the protocol to avoid a lot of
     * calls of the {@link #getProtocol()}.
     * 
     * @param handler the handler
     * @param message the message
     */
    protected void handleMessage(Handler handler, Message message) {
        try {
            handler.handle(this.networkContext, message);
        } catch (Throwable throwable) {
            this.onHandlerThrowable(message, handler, throwable);
        }
    }

    public void setEncryption(@Nullable SecretKey secretKey) {
        if (secretKey == null) {
            this.channel.pipeline().replace(ENCRYPTION, ENCRYPTION, NoopHandler.INSTANCE);
        } else {
            this.channel.pipeline().replace(ENCRYPTION, ENCRYPTION, new MessageEncryptionHandler(secretKey));
        }
    }

    /**
     * Called when the server received a message from the client.
     * 
     * @param message the message
     */
    public void messageReceived(Message message) {
        if (message == NullMessage.INSTANCE) {
            // Ignore
        } else if (message instanceof BulkMessage) {
            ((BulkMessage) message).getMessages().forEach(this::messageReceived);
        } else if (message instanceof HandlerMessage) {
            final HandlerMessage message1 = (HandlerMessage) message;
            if (AsyncHelper.isAsyncMessage(message1.getMessage()) || AsyncHelper.isAsyncHandler(message1.getHandler())) {
                this.handleMessage(message1.getHandler(), message1.getMessage());
            } else {
                this.messageQueue.add(message1);
            }
        } else {
            Class<? extends Message> messageClass = message.getClass();
            MessageRegistration registration = this.getProtocol().inbound().findByMessageType(messageClass).orElse(null);

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

    /**
     * Notify that the session is currently idle.
     */
    public void idle() {
        if (this.pingMessageId == 0 && this.getProtocolState().equals(ProtocolState.PLAY)) {
            this.pingMessageId = this.random.nextInt();
            if (this.pingMessageId == 0) {
                this.pingMessageId++;
            }
            this.send(new MessageInOutPing(this.pingMessageId));
        } else {
            this.disconnect("Timed out.");
        }
    }

    /**
     * Called when an exception occurs during inbound decoding and so on.
     * 
     * @param throwable the throwable
     */
    public void onInboundThrowable(Throwable throwable) {
        // Generated by the pipeline, not a network error
        if (throwable instanceof CodecException) {
            LanternGame.log().error("Error in network input!", throwable);
        } else {
            LanternGame.log().error("Message read error!", throwable);
            if (this.quitReason == null) {
                this.quitReason = Text.of("Message read error: " + throwable);
            }
            this.channel.close();
        }
    }

    /**
     * Called when an exception occurs during outbound encoding and so on.
     * 
     * @param throwable the throwable
     */
    public void onOutboundThrowable(Throwable throwable) {
        // Generated by the pipeline, not a network error
        if (throwable instanceof CodecException) {
            LanternGame.log().error("Error in network output!", throwable);
        // Probably a network-level error - consider the client gone
        } else {
            LanternGame.log().error("Message write error!", throwable);
            if (this.quitReason == null) {
                this.quitReason = Text.of("Message write error: " + throwable);
            }
            this.channel.close();
        }
    }

    /**
     * Called when an exception occurs during session handling.
     * 
     * @param message the message handler threw an exception on
     * @param handle handler that threw the an exception handling the message
     * @param throwable the throwable
     */
    public void onHandlerThrowable(Message message, Handler<?> handle, Throwable throwable) {
        LanternGame.log().error("Error while handling " + message + " (handler: " + handle.getClass().getSimpleName() + ")", throwable);
    }

    public void setPlayer(LanternGameProfile profile) {
        this.gameProfile = profile;
    }

    public void spawnPlayer() {
        this.player = new LanternPlayer(this.gameProfile, this);
        this.player.setEntityId(EntityIdAllocator.get().poll());
        // TODO: Which world?
        LanternWorld world = (LanternWorld) Sponge.getServer().getWorlds().iterator().next();
        // TODO: Read player data
        // TODO: User the proper location
        this.player.setLocation(new Location<>(world, new Vector3d(0, 100, 0)));
    }

    public void onDisconnect() {
        LanternGame.log().info("Connection for " + (this.gameProfile == null ? this.channel.remoteAddress().toString() : this.gameProfile.getName())
                + " disconnected from the server.");
        if (this.player != null) {
            this.player.setWorld(null);
        }
    }

    /**
     * public void onDisconnect() { if (player != null) { player.remove();
     * Message userListMessage =
     * UserListItemMessage.removeOne(player.getUniqueId()); for (GlowPlayer
     * player : server.getOnlinePlayers()) {
     * player.getSession().send(userListMessage); }
     * 
     * String message = player.getName() + " [" + address + "] disconnected"; if
     * (quitReason != null) { message += ": " + quitReason; }
     * GlowServer.logger.info(message);
     * 
     * String text = EventFactory.onPlayerQuit(player).getQuitMessage(); if
     * (text != null) { server.broadcastMessage(text); } player = null; // in
     * case we are disposed twice } }
     */

    /**
     * Get the randomly-generated verify token for this session.
     * 
     * @return the verify token
     */
    public byte[] getVerifyToken() {
        return verifyToken;
    }

    /**
     * Sets the verify token of this session.
     * 
     * @param token the verify token
     */
    public void setVerifyToken(byte[] token) {
        this.verifyToken = token;
    }

    /**
     * Gets the verify username for this session.
     * 
     * @return the verify username
     */
    public String getVerifyUsername() {
        return this.verifyUsername;
    }

    /**
     * Sets the verify username for this session.
     * 
     * @param username the verify username
     */
    public void setVerifyUsername(String username) {
        this.verifyUsername = username;
    }

    /**
     * Note that the client has responded to a keep-alive.
     * 
     * @param id the ping id to check for validity
     */
    public void pong(int id) {
        if (this.pingMessageId == id) {
            this.pingMessageId = 0;

            long time = System.nanoTime() / 1000000L;
            long timed = time - this.pingTimeStart;

            this.ping = (int) ((this.ping * 3 + timed) / 4);
        }
    }

    /**
     * Pulses the session.
     */
    protected void pulse() {
        HandlerMessage entry;
        while ((entry = this.messageQueue.poll()) != null) {
            this.handleMessage(entry.getHandler(), entry.getMessage());
        }
    }

    /**
     * Disconnects the session. This causes a KickMessage to be sent. When it
     * has been delivered, the channel is closed.
     */
    public void disconnect() {
        this.disconnect(Text.of("No reason specified."));
    }

    /**
     * Disconnects the session with the specified reason. This causes a
     * KickMessage to be sent. When it has been delivered, the channel is
     * closed.
     * 
     * @param objects The reason for disconnection.
     */
    public void disconnect(Object... objects) {
        this.disconnect(Text.of(objects), false);
    }

    /**
     * Disconnects the session with the specified reason. This causes a
     * KickMessage to be sent. When it has been delivered, the channel is
     * closed.
     * 
     * @param reason The reason for disconnection.
     */
    public void disconnect(Text reason) {
        this.disconnect(reason, false);
    }

    /**
     * Disconnects the session with the specified reason. This causes a
     * KickMessage to be sent. When it has been delivered, the channel is
     * closed.
     * 
     * @param reason the reason for disconnection
     * @param overrideKick whether to skip the kick event
     */
    private void disconnect(Text reason, boolean overrideKick) {
        this.quitReason = reason;

        if (this.player != null && !overrideKick) {
            // TODO: Send a disconnect message to the chat.
        }

        // Log that the player was kicked/disconnected.
        if (this.player != null) {
            LanternGame.log().info("{} kicked: {}", this.player.getName(), reason);
        } else {
            LanternGame.log().info("[{}] kicked: {}", this.address, reason);
        }

        ProtocolState current = this.getProtocolState();

        // Perform the kick, sending a kick message if possible
        if (this.channel.isActive() && (current.equals(ProtocolState.LOGIN) || current.equals(ProtocolState.PLAY))) {
            this.send(new MessageOutDisconnect(reason)).addListener(ChannelFutureListener.CLOSE);
        } else {
            this.channel.close();
        }
    }

    @Override
    public LanternPlayer getPlayer() {
        return this.player;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    /**
     * Sets the virtual host address that is used.
     * 
     * @param address the address
     */
    public void setVirtualHost(InetSocketAddress address) {
        this.virtualAddress = address;
    }

    /**
     * Gets the protocol version.
     * 
     * @return the protocol version
     */
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Sets the protocol version, this can only be set once.
     * 
     * @param version the version
     */
    public void setProtocolVersion(int version) {
        if (this.protocolVersion != -1) {
            throw new IllegalStateException("The protocol version can only be set once.");
        }
        this.protocolVersion = version;
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualAddress;
    }

    @Override
    public int getPing() {
        return this.ping;
    }
}
