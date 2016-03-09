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
package org.lanternpowered.server.game.registry.type.economy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.game.registry.RegistryModuleHelper.validateIdentifier;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.economy.LanternTransactionType;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TransactionTypeRegistryModule implements AdditionalCatalogRegistryModule<TransactionType> {

    @RegisterCatalog(TransactionTypes.class)
    private final Map<String, TransactionType> transactionTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        List<TransactionType> types = Lists.newArrayList();
        types.add(new LanternTransactionType("deposit"));
        types.add(new LanternTransactionType("withdraw"));
        types.add(new LanternTransactionType("transfer"));
        types.forEach(type -> this.transactionTypes.put(type.getId(), type));
    }

    @Override
    public Optional<TransactionType> getById(String id) {
        return Optional.ofNullable(this.transactionTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<TransactionType> getAll() {
        return ImmutableSet.copyOf(this.transactionTypes.values());
    }

    @Override
    public void registerAdditionalCatalog(TransactionType transactionType) {
        checkNotNull(transactionType, "transactionType");
        String id = transactionType.getId();
        validateIdentifier(id);
        checkState(!this.transactionTypes.containsKey(id),
                "There is already a transaction type registered with the id. (" + id + ")");
        this.transactionTypes.put(id, transactionType);
    }
}
