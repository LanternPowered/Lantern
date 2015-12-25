/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.network.query;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.scheduler.Task;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a server for the minecraft server query protocol.
 * @see <a href="http://wiki.vg/Query">Protocol Specifications</a>
 */
public class QueryServer {

    // The {@link EventLoopGroup} used by the query server.
    private EventLoopGroup group = new NioEventLoopGroup();

    // The {@link Bootstrap} used by netty to instantiate the query server
    private Bootstrap bootstrap = new Bootstrap();

    // Instance of the LanternServer
    private LanternGame game;

    // Maps each {@link InetSocketAddress} of a client to its challenge token
    private Map<InetSocketAddress, Integer> challengeTokens = new ConcurrentHashMap<>();

    // The {@link Random} used to generate challenge tokens
    private Random random = new Random();

    // The task used to invalidate all challenge tokens every 30 seconds
    private Task flushTask;

    public QueryServer(LanternGame game, boolean showPlugins) {
        this.game = game;
        this.bootstrap
                .group(this.group)
                .channel(NioDatagramChannel.class)
                .handler(new QueryHandler(this, showPlugins));
    }

    /**
     * Bind the server on the specified address.
     * 
     * @param address the address
     * @return the netty channel future for bind operation
     */
    public ChannelFuture bind(final SocketAddress address) {
        if (this.flushTask == null) {
            this.flushTask = this.game.getScheduler().createTaskBuilder().async()
                    .delay(30, TimeUnit.SECONDS).interval(30, TimeUnit.SECONDS)
                    .execute(this::flushChallengeTokens).submit(this.game.getMinecraftPlugin());
        }
        return this.bootstrap.bind(address);
    }

    /**
     * Shut the query server down.
     */
    public void shutdown() {
        this.group.shutdownGracefully();
        if (this.flushTask != null) {
            this.flushTask.cancel();
        }
    }

    /**
     * Generate a new token.
     * 
     * @param address the sender address
     * @return the generated valid token
     */
    public int generateChallengeToken(InetSocketAddress address) {
        int token = this.random.nextInt();
        this.challengeTokens.put(address, token);
        return token;
    }

    /**
     * Verify that the request is using the correct challenge token.
     * 
     * @param address the sender address
     * @param token the token
     * @return whether the token is valid
     */
    public boolean verifyChallengeToken(InetSocketAddress address, int token) {
        return Objects.equals(this.challengeTokens.get(address), token);
    }

    /**
     * Invalidates all challenge tokens.
     */
    public void flushChallengeTokens() {
        this.challengeTokens.clear();
    }

    /**
     * Gets the game instance.
     * 
     * @return the game
     */
    public LanternGame getGame() {
        return this.game;
    }
}
