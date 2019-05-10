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
package org.lanternpowered.server.network.block.vanilla;

import com.google.common.base.Objects;
import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.network.block.BlockEntityProtocol;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class SignBlockEntityProtocol<T extends LanternBlockEntity> extends BlockEntityProtocol<T> {

    private static final DataQuery[] lineQueries = {
            DataQuery.of("Text1"),
            DataQuery.of("Text2"),
            DataQuery.of("Text3"),
            DataQuery.of("Text4"),
    };

    @Nullable private List<Text> lastLines;

    /**
     * Constructs a new {@link SignBlockEntityProtocol} object.
     *
     * @param tile The blockEntity entity
     */
    public SignBlockEntityProtocol(T tile) {
        super(tile);
    }

    @Override
    protected String getType() {
        return "minecraft:sign";
    }

    @Override
    protected void populateInitData(DataView dataView) {
        addLines(dataView, this.blockEntity.get(Keys.SIGN_LINES).orElse(Collections.emptyList()));
    }

    @Override
    protected void populateUpdateData(Supplier<DataView> dataViewSupplier) {
        final List<Text> signLines = this.blockEntity.get(Keys.SIGN_LINES).orElse(Collections.emptyList());
        if (!Objects.equal(this.lastLines, signLines)) {
            addLines(dataViewSupplier.get(), signLines);
        }
        this.lastLines = new ArrayList<>(signLines);
    }

    private static void addLines(DataView view, List<Text> lines) {
        // TODO: Make localizable per player
        for (int i = 0; i < lineQueries.length; i++) {
            view.set(lineQueries[i], TextSerializers.JSON.serialize(i < lines.size() ? lines.get(i) : Text.of()));
        }
    }
}
