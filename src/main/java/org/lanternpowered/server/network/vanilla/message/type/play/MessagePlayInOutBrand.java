package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInOutBrand implements Message {

    private final String brand;

    /**
     * Creates a new brand message.
     * 
     * @param brand the brand
     */
    public MessagePlayInOutBrand(String brand) {
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

}
