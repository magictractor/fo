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
package uk.co.magictractor.fo.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.fop.configuration.DefaultConfiguration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * A builder that allows a DefaultConfiguration to be constructed from scratch
 * or loaded and modified. {@link DefaultConfigurationBuilder} is the usual way
 * to create a {@code DefaultConfiguration}.
 * </p>
 * <p>
 * Other approaches are possible, see
 * {@link https://xmlgraphics.apache.org/fop/2.9/fonts}.
 * </p>
 * <p>
 * One alternative is to configure available fonts in the MANIFEST.MF file. That
 * requires {@code auto-detect} to be on. However, this also detects all system
 * fonts and so is slow. A custom {@code FontDetector} could be used and set on
 * the {@FontManager} in an {@EnvironmentProfile} passed to a configuration
 * builder, see {@code ManifestFontDetector}. See the default
 * {@code FontDetector} in {@code FontDetectorFactory$DefaultFontDetector} and
 * {@link https://xmlgraphics.apache.org/fop/2.9/fonts#autodetect}.
 * </p>
 */
public class EmbeddedConfigurationBuilder {

    private static final byte[] EMPTY_CONFIGURATION_BYTES = "<fop version=\"1.0\"><renderers></renderers></fop>"
            .getBytes(StandardCharsets.ISO_8859_1);

    // These are non-null.
    private DefaultConfiguration configuration;
    private Document document;
    private Element rootElement;

    // These are caches for getters.
    // They may be null and are populated on demand.
    private Element defaultPageSettingsElement;
    private Element renderersElement;
    private Element pdfRendererElement;
    private Element fontsElement;
    private Optional<Element> fontsAutoDetectElement;

    public EmbeddedConfigurationBuilder() {
        this(new ByteArrayInputStream(EMPTY_CONFIGURATION_BYTES));
    }

    public EmbeddedConfigurationBuilder(InputStream configurationStream) {
        if (configurationStream == null) {
            throw new IllegalArgumentException("configurationStream must not be null");
        }
        init(() -> new DefaultConfigurationBuilder().build(configurationStream));
    }

    public EmbeddedConfigurationBuilder(File file) {
        init(() -> new DefaultConfigurationBuilder().buildFromFile(file));
    }

    private void init(DefaultConfigurationSupplier configurationSupplier) {
        try {
            init0(configurationSupplier);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void init0(DefaultConfigurationSupplier configurationSupplier) throws Exception {
        configuration = configurationSupplier.get();

        Method method = DefaultConfiguration.class.getDeclaredMethod("getElement");
        method.setAccessible(true);
        rootElement = (Element) method.invoke(configuration);

        document = rootElement.getOwnerDocument();
    }

    public DefaultConfiguration build() {
        return configuration;
    }

    public Document getDocument() {
        return document;
    }

    public String getDefaultPageHeight() {
        return emptyToNull(getDefaultPageSettings().getAttribute("height"));
    }

    public String getDefaultPageWidth() {
        return emptyToNull(getDefaultPageSettings().getAttribute("width"));
    }

    public Element getDefaultPageSettings() {
        if (defaultPageSettingsElement == null) {
            defaultPageSettingsElement = findOrCreateChild(rootElement, "default-page-settings");
        }
        return defaultPageSettingsElement;
    }

    // TODO! could also have a variant using awt constants. But might be tricky to keep in/mm rather than points. See PrintJob2D.
    public EmbeddedConfigurationBuilder withDefaultPageSettings(String height, String width) {
        Element defaultPageSettings = getDefaultPageSettings();
        defaultPageSettings.setAttribute("height", height);
        defaultPageSettings.setAttribute("width", width);

        return this;
    }

    public Element getRenderers() {
        if (renderersElement == null) {
            renderersElement = findOrCreateChild(rootElement, "renderers");
        }
        return renderersElement;
    }

    public Element getPdfRenderer() {
        if (pdfRendererElement == null) {
            pdfRendererElement = findOrCreateChild(getRenderers(), "renderer", "mime", "application/pdf");
        }
        return pdfRendererElement;
    }

    public Element getFonts() {
        if (fontsElement == null) {
            fontsElement = findOrCreateChild(getPdfRenderer(), "fonts");
        }
        return fontsElement;
    }

    public EmbeddedConfigurationBuilder withFontDirectory(String directory) {
        Element fonts = getFonts();

        Element directoryElement = document.createElement("directory");
        directoryElement.setTextContent(directory);
        fonts.appendChild(directoryElement);

        return this;
    }

    /**
     * Param uses URL because typical use will use {@link Class#getResource}.
     */
    public EmbeddedConfigurationBuilder withFontDirectory(URL directoryUrl) {
        // TODO! check scheme/protocol. If not file: then copy to a temp directory and use that.
        URI directoryUri;
        try {
            directoryUri = directoryUrl.toURI();
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException();
        }

        Path path = Paths.get(directoryUri).toAbsolutePath();
        if (!Files.isDirectory(path)) {
            // Alternatively, could copy the file to a temp directory and use that.
            throw new IllegalArgumentException();
        }

        return withFontDirectory(path.toString());
    }

    public boolean getFontAutoDetect() {
        if (fontsAutoDetectElement == null) {
            fontsAutoDetectElement = Optional.ofNullable(findChildNullable(getFonts(), "auto-detect"));
        }
        return fontsAutoDetectElement.isPresent();
    }

    public EmbeddedConfigurationBuilder withFontAutoDetect(boolean autoDetect) {
        if (getFontAutoDetect() != autoDetect) {
            if (autoDetect) {
                // Will create
                fontsAutoDetectElement = Optional.of(findOrCreateChild(getFonts(), "auto-detect"));
            }
            else {
                // Both should be non-null because getter was used.
                fontsElement.removeChild(fontsAutoDetectElement.get());
                fontsAutoDetectElement = Optional.empty();
            }
        }

        return this;
    }

    private String emptyToNull(String value) {
        return value.length() == 0 ? null : value;
    }

    @FunctionalInterface
    private static interface DefaultConfigurationSupplier {
        DefaultConfiguration get() throws Exception;
    }

    // TODO! bin these
    // DomUtil was modified to always use namespaces.
    // Methods here were formerly in DomUtil. These should be binned and code above modified to use null namespace.

    private static Element findOrCreateChild(Element parent, String childName) {
        Element child = findChildNullable(parent, childName);
        if (child == null) {
            child = parent.getOwnerDocument().createElement(childName);
            parent.appendChild(child);
        }

        return child;
    }

    private static Element findOrCreateChild(Element parent, String childName, String attr1Name, String attr1Value) {
        Element child = findChild(parent, c -> childName.equals(c.getNodeName()) && attr1Value.equals(c.getAttribute(attr1Name)));
        if (child == null) {
            child = parent.getOwnerDocument().createElement(childName);
            child.setAttribute(attr1Name, attr1Value);
            parent.appendChild(child);
        }

        return child;
    }

    private static Element findChildNullable(Element parent, String childName) {
        return findChild(parent, c -> childName.equals(c.getNodeName()));
    }

    private static Element findChild(Element parent, Predicate<Element> matcher) {
        NodeList children = parent.getChildNodes();
        int n = children.getLength();
        for (int i = 0; i < n; i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                if (matcher.test(childElement)) {
                    return childElement;
                }
            }
        }

        return null;
    }

}
