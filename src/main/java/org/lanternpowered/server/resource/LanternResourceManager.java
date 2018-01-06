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
package org.lanternpowered.server.resource;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.collect.Iterables2;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.resource.Pack;
import org.spongepowered.api.resource.Resource;
import org.spongepowered.api.resource.ResourceManager;
import org.spongepowered.api.resource.ResourcePath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("SuspiciousMethodCalls")
public class LanternResourceManager implements ResourceManager, IResourceProvider {

    private final Map<String, LanternPack> packs = new HashMap<>();
    private final List<LanternPack> sortedPacks = new ArrayList<>();
    private final Iterable<LanternPack> reverseSortedPacks = Iterables2.reverse(this.sortedPacks);

    @Override
    public Pack getPack(Object plugin) {
        return this.packs.get(checkPlugin(plugin, "plugin").getId());
    }

    @Override
    public Optional<Pack> getPack(String name) {
        checkNotNull(name, "name");
        return Optional.ofNullable(this.packs.get(name));
    }

    @Override
    public List<Pack> getActivePacks() {
        return this.sortedPacks.stream()
                .filter(LanternPack::isActive)
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public Map<String, Pack> getPacks() {
        return ImmutableMap.copyOf(this.packs);
    }

    @Override
    public void registerPack(String name, Pack pack) {
        checkNotNull(name, "name");
        checkNotNull(pack, "pack");
        checkState(this.packs.containsKey(name),
                "There is already a pack registered with the name %s", name);
        checkState(this.packs.containsValue(pack),
                "The pack is already registered under a different name");
        this.packs.put(name, (LanternPack) pack);
        this.sortedPacks.add((LanternPack) pack);
    }

    @Override
    public Optional<Pack> unregisterPack(String name) {
        checkNotNull(name, "name");
        final LanternPack pack = this.packs.get(name);
        if (pack == null) {
            return Optional.empty();
        }
        checkState(pack.plugin == null,
                "Cannot unregister plugin pack: %s", name);
        this.packs.remove(name);
        this.sortedPacks.remove(pack);
        return Optional.of(pack);
    }

    @Override
    public Collection<Resource> getResources(ResourcePath path) {
        checkNotNull(path, "path");
        final ImmutableList.Builder<Resource> builder = ImmutableList.builder();
        for (LanternPack pack : this.sortedPacks) {
            if (pack.isActive()) {
                pack.getResource(path).ifPresent(builder::add);
            }
        }
        return builder.build();
    }

    @Override
    public Optional<Resource> getResource(ResourcePath path) {
        checkNotNull(path, "path");
        for (LanternPack pack : this.reverseSortedPacks) {
            final Optional<Resource> optResource = pack.getResource(path);
            if (optResource.isPresent()) {
                return optResource;
            }
        }
        return Optional.empty();
    }

    @Override
    public CompletableFuture<Void> reload() {
        return reload(getActivePacks());
    }

    @Override
    public CompletableFuture<Void> reload(List<Pack> packs) {
        final Cause cause = CauseStack.current().getCurrentCause();
        // Call the pre event
        Sponge.getEventManager().post(SpongeEventFactory.createResourceReloadEventPre(cause, this));
        // Reload/cleanup all the current packs
        for (Pack pack : packs) {
            ((LanternPack) pack).reload();
        }
        // Trigger the reload event across the server, outside
        // of the world tick
        Lantern.getScheduler().callSync(() -> {
            // Post event?
            // Sponge.getEventManager().post(SpongeEventFactory.createResourceReloadEventPost(cause, this));
        });
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Collection<Resource> getAllResources() {
        return walkResources(LanternResourcePath.ALL).collect(Collectors.toList());
    }

    @Override
    public Stream<ResourcePath> walkResourcePaths(ResourcePath resourcePath, int maxDepth) {
        // Get all the active packs
        final Iterator<LanternPack> it = Iterables2.reverse(this.sortedPacks.<LanternPack>stream()
                .filter(LanternPack::isActive)
                .collect(Collectors.toList()))
                .iterator();
        // Should never happen, someone disabled all the packs?
        if (!it.hasNext()) {
            return Stream.empty();
        }
        Stream<ResourcePath> stream = it.next().walkResourcePaths(resourcePath, maxDepth);
        while (it.hasNext()) {
            stream = Stream.concat(stream, it.next().walkResourcePaths(resourcePath, maxDepth));
        }
        // Make it a distinct stream to avoid duplicates
        // A distinct stream suggests to make it sequential
        // for better performance, while not breaking the
        // possibility of using sorted
        return stream.sequential().distinct();
    }

    private static final class ResourceMapping {

        private final LanternResource resource;

        private ResourceMapping(LanternResource resource) {
            this.resource = resource;
        }

        LanternResource unwrap() {
            return this.resource;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ResourceMapping)) {
                return false;
            }
            final ResourceMapping that = (ResourceMapping) obj;
            return that.resource.getResourcePath()
                    .equals(this.resource.getResourcePath());
        }

        @Override
        public int hashCode() {
            return this.resource.getResourcePath().hashCode();
        }
    }

    @Override
    public Stream<LanternResource> walkResources(ResourcePath resourcePath, int maxDepth) {
        // Get all the active packs
        final Iterator<LanternPack> it = Iterables2.reverse(this.sortedPacks.<LanternPack>stream()
                .filter(LanternPack::isActive)
                .collect(Collectors.toList()))
                .iterator();
        // Should never happen, someone disabled all the packs?
        if (!it.hasNext()) {
            return Stream.empty();
        }
        Stream<LanternResource> stream = it.next().walkResources(resourcePath, maxDepth);
        while (it.hasNext()) {
            stream = Stream.concat(stream, it.next().walkResources(resourcePath, maxDepth));
        }
        // Make it a distinct stream to avoid duplicates
        // A distinct stream suggests to make it sequential
        // for better performance, while not breaking the
        // possibility of using sorted
        // The LanternResource is temporarily wrapped into a
        // ResourceMapping for custom `equals` behavior
        return stream.sequential().map(ResourceMapping::new).distinct().map(ResourceMapping::unwrap);
    }
}
