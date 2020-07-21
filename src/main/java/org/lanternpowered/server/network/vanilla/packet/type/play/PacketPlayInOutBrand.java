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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.network.message.Packet;

public final class PacketPlayInOutBrand implements Packet {

    private final String brand;

    /**
     * Creates a new brand message.
     * 
     * @param brand the brand
     */
    public PacketPlayInOutBrand(String brand) {
        this.brand = checkNotNull(brand, "brand");
    }

    /**
     * Gets the brand.
     * 
     * @return the brand
     */
    public String getBrand() {
        return this.brand;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("brand", this.brand)
                .toString();
    }
}
