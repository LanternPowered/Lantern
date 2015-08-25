package org.lanternpowered.server.text.xml;

import org.spongepowered.api.text.TextBuilder;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Span extends Element {
    @Override
    protected void modifyBuilder(TextBuilder builder) {
    }
}
