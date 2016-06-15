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
package org.lanternpowered.server.catalog;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;

public interface PluginCatalogType extends CatalogType {

    /**
     * {@inheritDoc}
     */
    @Override
    default String getId() {
        return this.getPluginId() + ':' + this.getName();
    }

    /**
     * Gets the identifier of the plugin that
     * owns the catalog type.
     * 
     * @return the plugin identifier
     */
    String getPluginId();

    abstract class Base implements PluginCatalogType {

        private final String id;
        private final String pluginId;
        private final String name;

        public Base(String pluginId, String name) {
            this.pluginId = checkNotNullOrEmpty(pluginId, "pluginId").toLowerCase(Locale.ENGLISH);
            this.name = checkNotNullOrEmpty(name, "name");
            this.id = this.pluginId + name.toLowerCase(Locale.ENGLISH);
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getPluginId() {
            return this.pluginId;
        }

        public static abstract class Translatable extends Base implements org.spongepowered.api.text.translation.Translatable {

            private final Translation translation;

            public Translatable(String pluginId, String name, String translation) {
                this(pluginId, name, Lantern.getRegistry().getTranslationManager().get(translation));
            }

            public Translatable(String pluginId, String name, Translation translation) {
                super(pluginId, name);
                this.translation = checkNotNull(translation, "translation");
            }

            @Override
            public Translation getTranslation() {
                return this.translation;
            }
        }
    }
}
