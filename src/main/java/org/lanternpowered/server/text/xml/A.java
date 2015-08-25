package org.lanternpowered.server.text.xml;

import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.action.TextActions;

import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class A extends Element {

    @XmlAttribute(required = true)
    private URL href;

    public A() {
    }

    public A(URL href) {
        this.href = href;
    }

    @Override
    protected void modifyBuilder(TextBuilder builder) {
        if (this.href == null) {
            throw new IllegalArgumentException("href is null! Make sure it is a valid URL");
        }
        builder.onClick(TextActions.openUrl(this.href));
    }

    public void setUrl(URL href) {
        this.href = href;
    }
}
