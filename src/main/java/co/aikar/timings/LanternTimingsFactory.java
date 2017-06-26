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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.EvictingQueue;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

public class LanternTimingsFactory implements TimingsFactory {

    private final int MAX_HISTORY_FRAMES = 12;
    public final Timing NULL_HANDLER = new NullTimingHandler();
    private boolean timingsEnabled = false;
    private boolean verboseEnabled = true;
    private int historyInterval = -1;
    private int historyLength = -1;
    private final boolean moduleEnabled;

    public LanternTimingsFactory() {
        this.moduleEnabled = Lantern.getGame().getGlobalConfig().getTimings().isEnabled();
    }

    public void init() {
        final GlobalConfig.Timings config = Lantern.getGame().getGlobalConfig().getTimings();

        setTimingsEnabled(config.isEnabled());
        setVerboseTimingsEnabled(config.isVerbose());
        setHistoryInterval(config.getHistoryInterval());
        setHistoryLength(config.getHistoryLength());

        Lantern.getLogger().debug("Sponge Timings: " + this.timingsEnabled +
                                    " - Verbose: " + this.verboseEnabled +
                                    " - Interval: " + timeSummary(this.historyInterval / 20) +
                                    " - Length: " + timeSummary(this.historyLength / 20));
    }

    private static String timeSummary(int seconds) {
        String time = "";
        if (seconds > 60 * 60) {
            time += TimeUnit.SECONDS.toHours(seconds) + "h";
            seconds /= 60;
        }

        if (seconds > 0) {
            time += TimeUnit.SECONDS.toMinutes(seconds) + "m";
        }
        return time;
    }

    private static PluginContainer checkPlugin(Object plugin) {
        Optional<PluginContainer> optPlugin = Lantern.getGame().getPluginManager().fromInstance(plugin);
        checkArgument(optPlugin.isPresent(), "Provided object is not a plugin instance");
        return optPlugin.get();
    }

    @Override
    public Timing of(Object pluginObj, String name, @Nullable Timing groupHandler) {
        PluginContainer plugin = checkPlugin(pluginObj);
        return TimingsManager.getHandler(plugin.getId(), name, groupHandler, true);
    }

    @Override
    public boolean isTimingsEnabled() {
        return this.timingsEnabled;
    }

    @Override
    public void setTimingsEnabled(boolean enabled) {
        if (!this.moduleEnabled) {
            return;
        }
        this.timingsEnabled = enabled;
        reset();
    }

    @Override
    public boolean isVerboseTimingsEnabled() {
        return this.verboseEnabled;
    }

    @Override
    public void setVerboseTimingsEnabled(boolean enabled) {
        this.verboseEnabled = enabled;
        TimingsManager.needsRecheckEnabled = true;
    }

    @Override
    public int getHistoryInterval() {
        return this.historyInterval;
    }

    @Override
    public void setHistoryInterval(int interval) {
        this.historyInterval = Math.max(20 * 60, interval);
        // Recheck the history length with the new Interval
        if (this.historyLength != -1) {
            setHistoryLength(this.historyLength);
        }
    }

    @Override
    public int getHistoryLength() {
        return this.historyLength;
    }

    @Override
    public void setHistoryLength(int length) {
        // Cap at 12 History Frames, 1 hour at 5 minute frames.
        int maxLength = this.historyInterval * this.MAX_HISTORY_FRAMES;
        // For special cases of servers with special permission to bypass the
        // max.
        // This max helps keep data file sizes reasonable for processing on
        // Aikar's Timing parser side.
        // Setting this will not help you bypass the max unless Aikar has added
        // an exception on the API side.
        if (System.getProperty("timings.bypassMax") != null) {
            maxLength = Integer.MAX_VALUE;
        }
        this.historyLength = Math.max(Math.min(maxLength, length), this.historyInterval);
        Queue<TimingHistory> oldQueue = TimingsManager.HISTORY;
        int frames = (getHistoryLength() / getHistoryInterval());
        if (length > maxLength) {
            Lantern.getLogger().warn(
                    "Timings Length too high. Requested " + length + ", max is " + maxLength
                            + ". To get longer history, you must increase your interval. Set Interval to "
                            + Math.ceil(length / this.MAX_HISTORY_FRAMES)
                            + " to achieve this length.");
        }
        TimingsManager.HISTORY = EvictingQueue.create(frames);
        TimingsManager.HISTORY.addAll(oldQueue);
    }

    @Override
    public void reset() {
        TimingsManager.reset();
    }

    @Override
    public void generateReport(@Nullable CommandSource sender) {
        if (sender == null) {
            sender = Lantern.getServer().getConsole();
        }
        TimingsExport.reportTimings(sender);
    }

    public static long getCost() {
        return TimingsExport.getCost();
    }

    public static TimingHandler ofSafe(String name) {
        return ofSafe(null, name, null);
    }

    public static Timing ofSafe(PluginContainer plugin, String name) {
        return ofSafe(plugin != null ? plugin.getName() : "Minecraft - Invalid Plugin", name);
    }

    public static TimingHandler ofSafe(String name, Timing groupHandler) {
        return ofSafe(null, name, groupHandler);
    }

    public static TimingHandler ofSafe(String groupName, String name) {
        return TimingsManager.getHandler(groupName, name, null, false);
    }

    public static TimingHandler ofSafe(String groupName, String name, Timing groupHandler) {
        return TimingsManager.getHandler(groupName, name, groupHandler, false);
    }

}
