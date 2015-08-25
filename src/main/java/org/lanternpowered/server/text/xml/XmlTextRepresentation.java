package org.lanternpowered.server.text.xml;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.TextMessageException;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshallerHandler;

/**
 * Xml format serializer for Text instances.
 */
public class XmlTextRepresentation implements TextRepresentation {

    private static final JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(Element.class);
        } catch (JAXBException e) {
            ExceptionInInitializerError err = new ExceptionInInitializerError("Error creating JAXB context: " + e);
            err.initCause(e);
            throw err;
        }
    }

    public XmlTextRepresentation() {}

    @Override
    public String to(Text text) {
        return this.to(text, Locale.ENGLISH);
    }

    @Override
    public String to(Text text, Locale locale) {
        final StringWriter writer = new StringWriter();
        try {
            Marshaller marshaller = CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(Element.fromText(text, locale), writer);
        } catch (JAXBException e) {
            return Texts.toPlain(text, locale);
        }
        return writer.getBuffer().toString();
    }


    /**
     * Also courtesy of http://jazzjuice.blogspot.de/2009/06/jaxb-xmlmixed-and-white-space-anomalies.html
     */
    @SuppressWarnings("unchecked")
    private static <T> T unmarshal(JAXBContext ctx, String strData, boolean flgWhitespaceAware) throws Exception {
        UnmarshallerHandler uh = ctx.createUnmarshaller().getUnmarshallerHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(flgWhitespaceAware ? new WhitespaceAwareUnmarshallerHandler(uh) : uh);
        xr.setErrorHandler(new DefaultHandler());
        xr.parse(new InputSource(new StringReader(strData)));
        return (T) uh.getResult();
    }

    @Override
    public Text from(String input) throws TextMessageException {
        try {
            input = "<span>" + input + "</span>";
            final Element element = unmarshal(CONTEXT, input, true);
            return element.toText().build();
        } catch (Exception e) {
            throw new TextMessageException(Texts.of("Error parsing TextXML message '" + input + "'"), e);
        }
    }

    @Override
    public Text fromUnchecked(String input) {
        try {
            return from(input);
        } catch (TextMessageException e) {
            return Texts.of(input);
        }
    }
}
