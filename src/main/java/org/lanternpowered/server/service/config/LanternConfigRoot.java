/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.service.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.spongepowered.api.config.ConfigRoot;

import java.io.File;
import java.nio.file.Path;

/**
 * Root for lantern configurations.
 */
public class LanternConfigRoot implements ConfigRoot {

    private final String pluginName;
    private final File baseDir;

    public LanternConfigRoot(String pluginName, File baseDir) {
        this.pluginName = pluginName;
        this.baseDir = baseDir;
    }

    @Override
    public Path getConfigPath() {
        File configFile = new File(this.baseDir, this.pluginName + ".conf");
        if (configFile.getParentFile().isDirectory()) {
            configFile.getParentFile().mkdirs();
        }
        return configFile.toPath();
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> getConfig() {
        return HoconConfigurationLoader.builder()
                .setPath(this.getConfigPath())
                .build();
    }

    @Override
    public Path getDirectory() {
        return this.baseDir.toPath();
    }
}
