/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.block.vanilla;

import com.google.common.base.Objects;
import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.network.block.BlockEntityProtocol;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

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
