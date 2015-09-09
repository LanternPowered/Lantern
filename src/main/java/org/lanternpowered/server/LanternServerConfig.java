package org.lanternpowered.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import ninja.leaping.configurate.gson.GsonConfigurationLoader;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.translator.ConfigurateTranslator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.data.DataQuery.of;

public class LanternServerConfig {

    // The launch parameters
    private final Map<Setting<?>, Object> parameters;

    // The config file
    private final File file;

    // The config folder
    private final File folder;

    private DataContainer dataContainer;

    public LanternServerConfig(File folder, File file, Map<Setting<?>, Object> parameters) {
        this.parameters = ImmutableMap.copyOf(parameters);
        this.folder = folder;
        this.file = file;
    }

    /**
     * Attempts to load the configuration file.
     * 
     * @throws IOException
     */
    public void load() throws IOException {
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setFile(this.file).setIndent(4).build();
        if (!this.folder.exists()) {
            this.folder.mkdirs();
        }
        if (!this.file.exists()) {
            this.dataContainer = new MemoryDataContainer();
            for (Setting<?> setting : getDefaultSettings()) {
                this.dataContainer.set(setting.path, setting.def);
            }
            loader.save(ConfigurateTranslator.instance().translateData(this.dataContainer));
            LanternGame.log().info("Unable to find the configuration file {}, generating a new one...", this.file);
        } else {
            this.dataContainer = (DataContainer) ConfigurateTranslator.instance().translateFrom(loader.load());
            LanternGame.log().info("Successfully loaded the configuration file {}", this.file);
        }
    }

    public File getConfigFolder() {
        return this.folder;
    }

    /**
     * Gets the value of the setting.
     * 
     * @param setting the setting
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Setting<T> setting) {
        checkNotNull(setting, "setting");
        if (this.parameters.containsKey(setting)) {
            return (T) this.parameters.get(setting);
        }
        return (T) this.dataContainer.get(setting.path).or(setting.def);
    }

    /**
     * Gets the default setting instances.
     * 
     * @return the default settings
     */
    public static Set<Setting<?>> getDefaultSettings() {
        return Settings.defaults;
    }

    /**
     * Represents a setting.
     *
     * @param <T>
     */
    public static class Setting<T> {

        private final DataQuery path;
        private final T def;

        public Setting(DataQuery path, T def) {
            this.path = path;
            this.def = def;
        }
    }

    public static class Settings {

        public static final Setting<String> SERVER_IP = new Setting<>(of('.', "server.ip"), "");
        public static final Setting<Integer> SERVER_PORT = new Setting<>(of('.', "server.port"), 25565);
        public static final Setting<String> SERVER_NAME = new Setting<>(of('.', "server.name"), "Lantern Server");
        public static final Setting<Boolean> ONLINE_MODE = new Setting<>(of('.', "server.online-mode"), true);
        public static final Setting<Integer> MAX_PLAYERS = new Setting<>(of('.', "server.max-players"), 20);
        public static final Setting<Boolean> WHITELIST = new Setting<>(of('.', "server.whitelist"), false);
        public static final Setting<String> MAIN_WORLD = new Setting<>(of('.', "server.main-world"), "world");
        public static final Setting<String> FAVICON = new Setting<>(of('.', "server.favicon"), "favicon.png");
        public static final Setting<String> MOTD = new Setting<>(of('.', "server.motd"), "A Lantern Mincraft Server");

        public static final Setting<String> PLUGIN_FOLDER = new Setting<>(of('.', "folders.plugins"), "plugins");
        public static final Setting<String> WORLD_FOLDER = new Setting<>(of('.', "folders.worlds"), "worlds");

        private static Set<Setting<?>> defaults;

        static {
            ImmutableSet.Builder<Setting<?>> builder = ImmutableSet.builder();
            for (Field field : Settings.class.getFields()) {
                if (field.getType().isAssignableFrom(Setting.class)) {
                    try {
                        builder.add((Setting<?>) field.get(null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            defaults = builder.build();
        }

    }
}
