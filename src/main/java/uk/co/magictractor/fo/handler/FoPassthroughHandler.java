/**
 * Copyright 2024 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.fo.handler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.CharBuffer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FoPassthroughHandler implements BlankContentHandler, BlankLexicalHandler {

    private OutputStreamWriter writer;

    protected FoPassthroughHandler(OutputStream outputStream) {
        this.writer = new OutputStreamWriter(new BufferedOutputStream(outputStream));
    }

    // TODO! commented out @Override when switching to Java 8. Investigate and maybe tidy.
    // <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    // @Override
    public void declaration(String version, String encoding, String standalone)
            throws SAXException {

        try {
            writer.append("<?xml version=\"");
            writer.append(version);
            writer.append("\" encoding=\"");
            writer.append(encoding);
            writer.append("\" standalone=\"");
            writer.append(standalone);
            writer.append("\"?>");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Not used for most docsS: "A SAX parser must never report an XML declaration"
    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        try {
            writer.append("processingInstruction: target=" + target + ", data=" + data);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        try {
            writer.append('<');
            writer.append(qName);
            int n = attributes.getLength();
            for (int i = 0; i < n; i++) {
                writer.append(' ');
                writer.append(attributes.getQName(i));
                writer.append("=\"");
                writer.append(attributes.getValue(i));
                writer.append('"');
            }
            writer.append('>');
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        try {
            writer.append("</");
            writer.append(qName);
            writer.append(">");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        try {
            writer.append(CharBuffer.wrap(ch, start, length));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
        try {
            writer.append(CharBuffer.wrap(ch, start, length));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void comment(char ch[], int start, int length) {
        try {
            writer.append("<!-- ");
            writer.append(CharBuffer.wrap(ch, start, length));
            writer.append(" -->");
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void endDocument()
            throws SAXException {
        try {
            writer.close();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
