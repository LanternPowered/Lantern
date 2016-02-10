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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public final class LanternEntityType extends SimpleLanternCatalogType implements EntityType {

    private final Class<? extends Entity> entityClass;
    private final String minecraftId;
    private final Translation name;

    /**
     * Creates a new entity type.
     *
     * @param identifier the identifier
     * @param entityClass the entity class
     * @param name the name
     */
    public LanternEntityType(String identifier, Class<? extends Entity> entityClass, Translation name) {
        this(identifier, identifier, entityClass, name);
    }

    /**
     * Creates a new entity type.
     *
     * @param identifier the identifier
     * @param minecraftId the minecraft identifier (for internal use)
     * @param entityClass the entity class
     */
    public LanternEntityType(String identifier, String minecraftId, Class<? extends Entity> entityClass) {
        this(identifier, minecraftId, entityClass, LanternGame.get().getRegistry().getTranslationManager().get("entity." + minecraftId + ".name"));
    }

    /**
     * Creates a new entity type.
     *
     * @param identifier the identifier
     * @param minecraftId the minecraft identifier (for internal use)
     * @param entityClass the entity class
     * @param name the name
     */
    private LanternEntityType(String identifier, String minecraftId, Class<? extends Entity> entityClass, Translation name) {
        super(identifier);

        this.entityClass = checkNotNull(entityClass, "entityClass");
        this.minecraftId = checkNotNull(minecraftId, "minecraftId");
        this.name = checkNotNull(name, "name");
    }

    public String getMinecraftId() {
        return this.minecraftId;
    }

    @Override
    public Translation getTranslation() {
        return this.name;
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

}
