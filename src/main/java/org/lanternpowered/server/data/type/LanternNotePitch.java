package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.data.type.NotePitch;

public class LanternNotePitch extends SimpleLanternCatalogType implements NotePitch {

    private final byte internalId;
    private NotePitch next;

    public LanternNotePitch(String identifier, int internalId) {
        super(identifier);
        this.internalId = (byte) internalId;
    }

    @Override
    public NotePitch cycleNext() {
        return this.next;
    }

    public void setNext(NotePitch next) {
        if (this.next != null) {
            throw new IllegalStateException("The next pitch value can only be set once!");
        }
        this.next = next;
    }

    public byte getInternalId() {
        return this.internalId;
    }
}
