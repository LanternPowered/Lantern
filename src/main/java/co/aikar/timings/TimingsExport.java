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
package co.aikar.timings;

import static co.aikar.timings.TimingsManager.HISTORY;
import static org.spongepowered.api.Platform.Component.IMPLEMENTATION;

import co.aikar.util.JSONUtil;
import co.aikar.util.JSONUtil.JsonObjectBuilder;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.ConfigurationNode;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.status.LanternFavicon;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.source.RconSource;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

class TimingsExport extends Thread {

    private static final Joiner AUTHOR_LIST_JOINER = Joiner.on(", ");
    private static final Joiner RUNTIME_FLAG_JOINER = Joiner.on(" ");
    private static final Joiner CONFIG_PATH_JOINER = Joiner.on(".");

    private final CommandSource sender;
    private final JsonObject out;
    private final TimingHistory[] history;

    private TimingsExport(CommandSource sender, JsonObject out, TimingHistory[] history) {
        super("Timings paste thread");
        this.sender = sender;
        this.out = out;
        this.history = history;
    }

    private static String getServerName() {
        return Lantern.getImplementationPlugin().getName() + " " + Lantern.getImplementationPlugin().getVersion().orElse("");
    }

    /**
     * Builds an XML report of the timings to be uploaded for parsing.
     *
     * @param sender Who to report to
     */
    static void reportTimings(CommandSource sender) {
        Platform platform = Lantern.getGame().getPlatform();
        JsonObjectBuilder builder = JSONUtil.objectBuilder()
                // Get some basic system details about the server
                .add("version", platform.getContainer(IMPLEMENTATION).getVersion().orElse(platform.getMinecraftVersion().getName() + "-DEV"))
                .add("maxplayers", Lantern.getGame().getServer().getMaxPlayers())
                .add("start", TimingsManager.timingStart / 1000)
                .add("end", System.currentTimeMillis() / 1000)
                .add("sampletime", (System.currentTimeMillis() - TimingsManager.timingStart) / 1000);
        if (!TimingsManager.privacy) {
            builder.add("server", getServerName())
                    .add("motd", Sponge.getServer().getMotd().toPlain())
                    .add("online-mode", Sponge.getServer().getOnlineMode())
                    .add("icon", ((LanternFavicon) Lantern.getServer().getFavicon().get()).getEncoded());
        }

        final Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        builder.add("system", JSONUtil.objectBuilder()
                .add("timingcost", getCost())
                .add("name", System.getProperty("os.name"))
                .add("version", System.getProperty("os.version"))
                .add("jvmversion", System.getProperty("java.version"))
                .add("arch", System.getProperty("os.arch"))
                .add("maxmem", runtime.maxMemory())
                .add("cpu", runtime.availableProcessors())
                .add("runtime", ManagementFactory.getRuntimeMXBean().getUptime())
                .add("flags", RUNTIME_FLAG_JOINER.join(runtimeBean.getInputArguments()))
                .add("gc", JSONUtil.mapArrayToObject(ManagementFactory.getGarbageCollectorMXBeans(), (input) ->
                        JSONUtil.singleObjectPair(input.getName(), JSONUtil.arrayOf(input.getCollectionCount(), input.getCollectionTime())))));

        final Set<BlockType> blockTypeSet = Sets.newHashSet();
        final Set<EntityType> entityTypeSet = Sets.newHashSet();

        TimingHistory[] history = new TimingHistory[HISTORY.size() + 1];
        int i = 0;
        for (TimingHistory timingHistory : HISTORY) {
            blockTypeSet.addAll(timingHistory.blockTypeSet);
            entityTypeSet.addAll(timingHistory.entityTypeSet);
            history[i++] = timingHistory;
        }

        history[i] = new TimingHistory(); // Current snapshot
        blockTypeSet.addAll(history[i].blockTypeSet);
        entityTypeSet.addAll(history[i].entityTypeSet);

        JsonObjectBuilder handlersBuilder = JSONUtil.objectBuilder();
        for (TimingIdentifier.TimingGroup group : TimingIdentifier.GROUP_MAP.values()) {
            for (TimingHandler id : group.handlers) {
                if (!id.timed && !id.isSpecial()) {
                    continue;
                }
                handlersBuilder.add(id.id, JSONUtil.arrayOf(group.id, id.name));
            }
        }

        builder.add("idmap", JSONUtil.objectBuilder()
                .add("groups", JSONUtil.mapArrayToObject(TimingIdentifier.GROUP_MAP.values(), (group) ->
                        JSONUtil.singleObjectPair(group.id, group.name)))
                .add("handlers", handlersBuilder)
                .add("worlds", JSONUtil.mapArrayToObject(TimingHistory.worldMap.entrySet(), (entry) ->
                        JSONUtil.singleObjectPair(entry.getValue(), entry.getKey())))
                .add("tileentity", JSONUtil.mapArrayToObject(blockTypeSet, (blockType) ->
                        JSONUtil.singleObjectPair(blockType.getId(), blockType.getId())))
                .add("entity", JSONUtil.mapArrayToObject(entityTypeSet, (entityType) -> {
                    if (entityType == EntityTypes.UNKNOWN) {
                        return null;
                    }
                    return JSONUtil.singleObjectPair(entityType.getId(), entityType.getId());
                })));

        // Information about loaded plugins

        builder.add("plugins", JSONUtil.mapArrayToObject(Lantern.getGame().getPluginManager().getPlugins(), (plugin) ->
                JSONUtil.objectBuilder().add(plugin.getId(), JSONUtil.objectBuilder()
                        .add("version", plugin.getVersion().orElse(""))
                        .add("description", plugin.getDescription().orElse(""))
                        .add("website", plugin.getUrl().orElse(""))
                        .add("authors", AUTHOR_LIST_JOINER.join(plugin.getAuthors())))
                        .build()));

        // Information on the users Config

        builder.add("config", JSONUtil.objectBuilder()
                .add("lantern", serializeConfigNode(Lantern.getGame().getGlobalConfig().getRoot())));

        new TimingsExport(sender, builder.build(), history).start();
    }

    static long getCost() {
        // Benchmark the users System.nanotime() for cost basis
        int passes = 200;
        TimingHandler SAMPLER1 = LanternTimingsFactory.ofSafe("Timings Sampler 1");
        TimingHandler SAMPLER2 = LanternTimingsFactory.ofSafe("Timings Sampler 2");
        TimingHandler SAMPLER3 = LanternTimingsFactory.ofSafe("Timings Sampler 3");
        TimingHandler SAMPLER4 = LanternTimingsFactory.ofSafe("Timings Sampler 4");
        TimingHandler SAMPLER5 = LanternTimingsFactory.ofSafe("Timings Sampler 5");
        TimingHandler SAMPLER6 = LanternTimingsFactory.ofSafe("Timings Sampler 6");

        long start = System.nanoTime();
        for (int i = 0; i < passes; i++) {
            SAMPLER1.startTiming();
            SAMPLER2.startTiming();
            SAMPLER3.startTiming();
            SAMPLER3.stopTiming();
            SAMPLER4.startTiming();
            SAMPLER5.startTiming();
            SAMPLER6.startTiming();
            SAMPLER6.stopTiming();
            SAMPLER5.stopTiming();
            SAMPLER4.stopTiming();
            SAMPLER2.stopTiming();
            SAMPLER1.stopTiming();
        }
        long timingsCost = (System.nanoTime() - start) / passes / 6;
        SAMPLER1.reset(true);
        SAMPLER2.reset(true);
        SAMPLER3.reset(true);
        SAMPLER4.reset(true);
        SAMPLER5.reset(true);
        SAMPLER6.reset(true);
        return timingsCost;
    }

    private static JsonElement serializeConfigNode(ConfigurationNode node) {
        if (node.hasMapChildren()) {
            JsonObject object = new JsonObject();
            for (Entry<Object, ? extends ConfigurationNode> entry : node.getChildrenMap().entrySet()) {
                String fullPath = CONFIG_PATH_JOINER.join(entry.getValue().getPath());
                if (fullPath.equals("sponge.sql") || TimingsManager.hiddenConfigs.contains(fullPath)) { // TODO: Add hidden config functionality
                    continue;
                }
                object.add(entry.getKey().toString(), serializeConfigNode(entry.getValue()));
            }
            return object;
        }
        if (node.hasListChildren()) {
            JsonArray array = new JsonArray();
            for (ConfigurationNode child : node.getChildrenList()) {
                array.add(serializeConfigNode(child));
            }
            return array;
        }
        return JSONUtil.toJsonElement(node.getValue());
    }

    @Override
    public synchronized void start() {
        if (this.sender instanceof RconSource) {
            this.sender.sendMessage(Text.of(TextColors.RED, "Warning: Timings report done over RCON will cause lag spikes."));
            this.sender.sendMessage(Text.of(TextColors.RED, "You should use ", TextColors.YELLOW, "/sponge timings report",
                    TextColors.RED, " in game or console."));
            run();
        } else {
            super.start();
        }
    }

    @Override
    public void run() {
        this.sender.sendMessage(Text.of(TextColors.GREEN, "Preparing Timings Report..."));

        this.out.add("data", JSONUtil.mapArray(this.history, TimingHistory::export));

        String response = null;
        try {
            String hostname = "localhost";
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (IOException e) {
                Lantern.getLogger().warn("Could not get own server hostname when uploading timings - falling back to 'localhost'", e);
            }
            HttpURLConnection con = (HttpURLConnection) new URL("http://timings.aikar.co/post").openConnection();
            con.setDoOutput(true);
            // Change this to Lantern once Timings adds support for Lantern
            con.setRequestProperty("User-Agent", "Sponge/" + getServerName() + "/" + hostname);
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);

            OutputStream request = new GZIPOutputStream(con.getOutputStream()) {

                {
                    this.def.setLevel(7);
                }
            };

            request.write(JSONUtil.toString(this.out).getBytes("UTF-8"));
            request.close();

            response = getResponse(con);

            if (con.getResponseCode() != 302) {
                this.sender.sendMessage(Text.of(
                        TextColors.RED, "Upload Error: " + con.getResponseCode() + ": " + con.getResponseMessage()));
                this.sender.sendMessage(Text.of(TextColors.RED, "Check your logs for more information"));
                if (response != null) {
                    Lantern.getLogger().error(response);
                }
                return;
            }

            String location = con.getHeaderField("Location");
            this.sender.sendMessage(Text.of(TextColors.GREEN, "View Timings Report: ", TextActions.openUrl(new URL(location)), location));
            if (!(this.sender instanceof ConsoleSource)) {
                Lantern.getLogger().info("View Timings Report: " + location);
            }

            if (response != null && !response.isEmpty()) {
                Lantern.getLogger().info("Timing Response: " + response);
            }
        } catch (IOException ex) {
            this.sender.sendMessage(Text.of(TextColors.RED, "Error uploading timings, check your logs for more information"));
            if (response != null) {
                Lantern.getLogger().error(response);
            }
            Lantern.getLogger().error("Could not paste timings", ex);
        }
    }

    private String getResponse(HttpURLConnection con) throws IOException {
        InputStream is = null;
        try {
            is = con.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            return bos.toString();

        } catch (IOException ex) {
            this.sender.sendMessage(Text.of(TextColors.RED, "Error uploading timings, check your logs for more information"));
            Lantern.getLogger().warn(con.getResponseMessage(), ex);
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
