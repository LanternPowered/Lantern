package org.lanternpowered.server.text.xml;

import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.format.TextStyles;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class I extends Element {
    @Override
    protected void modifyBuilder(TextBuilder builder) {
        builder.style(TextStyles.ITALIC);
    }
}
