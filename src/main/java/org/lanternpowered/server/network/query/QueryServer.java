/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.network.query;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.AbstractServer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * Implementation of a server for the minecraft server query protocol.
 * @see <a href="http://wiki.vg/Query">Protocol Specifications</a>
 */
public class QueryServer extends AbstractServer {

    // The {@link EventLoopGroup} used by the query server.
    private EventLoopGroup group;

    // The {@link Bootstrap} used by netty to instantiate the query server
    private Bootstrap bootstrap;

    // Instance of the LanternGame
    private final LanternGame game;

    // Maps each InetSocketAddress of a client to its challenge token
    private final Map<InetSocketAddress, Integer> challengeTokens = new ConcurrentHashMap<>();

    // The task used to invalidate all challenge tokens every 30 seconds
    @Nullable private ScheduledTask flushTask;

    private final boolean showPlugins;

    public QueryServer(LanternGame game, boolean showPlugins) {
        this.showPlugins = showPlugins;
        this.game = game;
    }

    @Override
    protected ChannelFuture init(SocketAddress address, TransportType channelType) {
        this.group = createEventLoopGroup(channelType);
        this.bootstrap = new Bootstrap()
                .group(this.group)
                .channel(getDatagramChannelClass(channelType))
                .handler(new QueryHandler(this, showPlugins));
        if (this.flushTask == null) {
            this.flushTask = this.game.getAsyncScheduler().submit(Task.builder()
                    .delay(30, TimeUnit.SECONDS)
                    .interval(30, TimeUnit.SECONDS)
                    .execute(this::flushChallengeTokens)
                    .plugin(this.game.getMinecraftPlugin())
                    .build());
        }
        return this.bootstrap.bind(address);
    }

    @Override
    protected void shutdown0() {
        this.bootstrap = null;
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
    int generateChallengeToken(InetSocketAddress address) {
        final int token = ThreadLocalRandom.current().nextInt();
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
    boolean verifyChallengeToken(InetSocketAddress address, int token) {
        return Objects.equals(this.challengeTokens.get(address), token);
    }

    /**
     * Invalidates all challenge tokens.
     */
    private void flushChallengeTokens() {
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
