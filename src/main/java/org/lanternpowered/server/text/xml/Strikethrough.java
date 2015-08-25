package org.lanternpowered.server.text.xml;

import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.format.TextStyles;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(Strikethrough.S.class)
@XmlRootElement
public class Strikethrough extends Element {

    @Override
    protected void modifyBuilder(TextBuilder builder) {
        builder.style(TextStyles.STRIKETHROUGH);
    }

    @XmlRootElement
    public static class S extends Strikethrough {
    }
}
