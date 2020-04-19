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
package org.lanternpowered.server.service.permission;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.RemoteSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;

import java.net.InetAddress;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A context calculator handling world contexts.
 */
public class LanternContextCalculator implements ContextCalculator<Subject> {

    private final LoadingCache<RemoteSource, Set<Context>> remoteIpCache = buildAddressCache(Context.REMOTE_IP_KEY,
            input -> input.getConnection().getAddress().getAddress());

    private final LoadingCache<RemoteSource, Set<Context>> localIpCache = buildAddressCache(Context.LOCAL_IP_KEY,
            input -> input.getConnection().getVirtualHost().getAddress());

    private LoadingCache<RemoteSource, Set<Context>> buildAddressCache(final String contextKey,
            final Function<RemoteSource, InetAddress> function) {
        return Caffeine.newBuilder()
            .weakKeys()
            .build(key -> {
                final ImmutableSet.Builder<Context> builder = ImmutableSet.builder();
                final InetAddress address = checkNotNull(function.apply(key), "address");
                builder.add(new Context(contextKey, address.getHostAddress()));
                for (String set : Maps.filterValues(Lantern.getGame().getGlobalConfig().getIpSets(), input -> input.test(address)).keySet()) {
                    builder.add(new Context(contextKey, set));
                }
                return builder.build();
            });
    }

    @Override
    public void accumulateContexts(Subject subject, Set<Context> accumulator) {
        final Optional<CommandSource> subjSource = subject.getCommandSource();
        if (subjSource.isPresent()) {
            final CommandSource source = subjSource.get();
            if (source instanceof Locatable) {
                final World currentExt = ((Locatable) source).getWorld();
                accumulator.add(currentExt.getContext());
                accumulator.add((currentExt.getDimension().getContext()));
            }
            if (source instanceof RemoteSource) {
                final RemoteSource rem = (RemoteSource) source;
                accumulator.addAll(this.remoteIpCache.get(rem));
                accumulator.addAll(this.localIpCache.get(rem));
                accumulator.add(new Context(Context.LOCAL_PORT_KEY, String.valueOf(rem.getConnection().getVirtualHost().getPort())));
                accumulator.add(new Context(Context.LOCAL_HOST_KEY, rem.getConnection().getVirtualHost().getHostName()));
            }
        }
    }

    @Override
    public boolean matches(Context context, Subject subject) {
        final Optional<CommandSource> subjSource = subject.getCommandSource();
        if (subjSource.isPresent()) {
            final CommandSource source = subjSource.get();
            if (source instanceof Locatable) {
                final Locatable located = (Locatable) source;
                if (context.getType().equals(Context.WORLD_KEY)) {
                    return located.getWorld().getContext().equals(context);
                } else if (context.getType().equals(Context.DIMENSION_KEY)) {
                    return located.getWorld().getDimension().getContext().equals(context);
                }
            }
            if (source instanceof RemoteSource) {
                final RemoteSource remote = (RemoteSource) source;
                if (context.getType().equals(Context.LOCAL_HOST_KEY)) {
                    return context.getValue().equals(remote.getConnection().getVirtualHost().getHostName());
                } else if (context.getType().equals(Context.LOCAL_PORT_KEY)) {
                    return context.getValue().equals(String.valueOf(remote.getConnection().getVirtualHost().getPort()));
                } else if (context.getType().equals(Context.LOCAL_IP_KEY)) {
                    return this.localIpCache.get(remote).contains(context);
                } else if (context.getType().equals(Context.REMOTE_IP_KEY)) {
                    return this.remoteIpCache.get(remote).contains(context);
                }
            }
        }
        return false;
    }

}
