package org.lanternpowered.server.data.manipulator.mutable.item;

import org.lanternpowered.server.data.manipulator.mutable.AbstractData;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.item.ImmutableAuthorData;
import org.spongepowered.api.data.manipulator.mutable.item.AuthorData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;

public class LanternAuthorData extends AbstractData<AuthorData, ImmutableAuthorData> implements AuthorData {

    public LanternAuthorData() {
        super(AuthorData.class, ImmutableAuthorData.class);
    }

    public LanternAuthorData(ImmutableAuthorData manipulator) {
        super(manipulator);
    }

    public LanternAuthorData(AuthorData manipulator) {
        super(manipulator);
    }

    @Override
    public void registerKeys() {
        registerKey(Keys.BOOK_AUTHOR, Text.EMPTY).notRemovable();
    }

    @Override
    public Value<Text> author() {
        return getValue(Keys.BOOK_AUTHOR).get();
    }
}
