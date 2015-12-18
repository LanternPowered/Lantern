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
package org.lanternpowered.server.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.lanternpowered.server.config.serializer.CatalogTypeSerializer;
import org.lanternpowered.server.config.serializer.TextTypeSerializer;
import org.lanternpowered.server.config.serializer.UUIDTypeSerializer;
import org.lanternpowered.server.profile.LanternGameProfile;
import org.lanternpowered.server.util.IpSet;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ConfigBase {

    public static final ConfigurationOptions DEFAULT_OPTIONS;

    static {
        final TypeSerializerCollection typeSerializers = ConfigurationOptions.defaults().getSerializers().newChild();
        typeSerializers.registerType(TypeToken.of(Text.class), new TextTypeSerializer())
                .registerType(TypeToken.of(CatalogType.class), new CatalogTypeSerializer())
                .registerType(TypeToken.of(IpSet.class), new IpSet.IpSetSerializer())
                .registerType(TypeToken.of(UUID.class), new UUIDTypeSerializer())
                .registerType(TypeToken.of(GameProfile.class), (TypeSerializer) typeSerializers.get(
                        TypeToken.of(LanternGameProfile.class)));
        DEFAULT_OPTIONS = ConfigurationOptions.defaults().setSerializers(typeSerializers);
    }

    private final ObjectMapper<ConfigBase>.BoundInstance configMapper;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final ConfigurationOptions options;
    private final Path path;

    private CommentedConfigurationNode root;

    /**
     * Creates a new config object.
     * 
     * @param path the config path
     * @throws IOException 
     */
    public ConfigBase(Path path) throws IOException {
        this(path, DEFAULT_OPTIONS);
    }

    /**
     * Creates a new config object.
     * 
     * @param loader the config loader
     * @param options the config options
     * @param configObject the configuration object the settings will be mapped to
     * @throws IOException 
     */
    public ConfigBase(Path path, ConfigurationOptions options) throws IOException {
        this.loader = HoconConfigurationLoader.builder().setPath(path).setDefaultOptions(options).build();
        try {
            this.configMapper = ObjectMapper.forObject(this);
        } catch (ObjectMappingException e) {
            throw new IOException("Unable to create a config mapper for the object.", e);
        }
        this.root = SimpleCommentedConfigurationNode.root(options);
        this.options = options;
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    public void load() throws IOException {
        if (!Files.exists(this.path)) {
            this.save();
        } else {
            this.root = this.loader.load(this.options);
            try {
                this.configMapper.populate(this.root);
            } catch (ObjectMappingException e) {
                throw new IOException("An error ocurred while serializing the object.", e);
            }
        }
    }

    public void save() throws IOException {
        if (!Files.exists(this.path.getParent())) {
            Files.createDirectories(this.path.getParent());
        }
        try {
            this.configMapper.serialize(this.root);
        } catch (ObjectMappingException e) {
            throw new IOException("An error ocurred while mapping the object.", e);
        }
        this.loader.save(this.root);
    }
}
