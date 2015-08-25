package org.lanternpowered.server.text.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.Arrays;

import javax.xml.bind.UnmarshallerHandler;

/**
 * Courtesy of http://jazzjuice.blogspot.de/2009/06/jaxb-xmlmixed-and-white-space-anomalies.html
 * Improves the XML parser's handling of whitespace.
 */
class WhitespaceAwareUnmarshallerHandler implements ContentHandler {

    private final UnmarshallerHandler uh;

    public WhitespaceAwareUnmarshallerHandler(UnmarshallerHandler uh) {
        this.uh = uh;
    }

    /**
     * Replace all-whitespace character blocks with the character '\u000B',
     * which satisfies the following properties:
     * <ol>
     *  <li>"\u000B".matches( "\\s" ) == true</li>
     *  <li>when parsing XmlMixed content, JAXB does not suppress the whitespace</li>
     * </ol>
     **/
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        for (int i = start + length - 1; i >= start; --i) {
            if (!Character.isWhitespace(ch[i])) {
                this.uh.characters(ch, start, length);
                return;
            }
        }
        Arrays.fill(ch, start, start + length, '\u000B');
        this.uh.characters(ch, start, length);
    }

    /* what follows is just blind delegation monkey code */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.uh.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        this.uh.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        this.uh.endElement(uri, localName, name);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        this.uh.endPrefixMapping(prefix);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.uh.processingInstruction(target, data);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.uh.setDocumentLocator(locator);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.uh.skippedEntity(name);
    }

    @Override
    public void startDocument() throws SAXException {
        this.uh.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        this.uh.startElement(uri, localName, name, atts);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.uh.startPrefixMapping(prefix, uri);
    }
}
