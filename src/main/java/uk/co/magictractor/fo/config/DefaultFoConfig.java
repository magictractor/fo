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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.apache.fop.apps.EnvironmentProfile;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.io.ResourceResolverFactory;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfiguration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.fop.fonts.FontCacheManagerFactory;
import org.apache.fop.fonts.FontManager;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageSessionContext.FallbackResolver;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageSessionContext.UnrestrictedFallbackResolver;
import org.apache.xmlgraphics.io.ResourceResolver;

/**
 *
 */
// TODO! FoConfig builder? would make it easy to use a non-standard resolver, transformer etc.
public class DefaultFoConfig implements FoConfig {

    private static final FoConfig INSTANCE = new DefaultFoConfig();

    public static FoConfig getInstance() {
        return INSTANCE;
    }

    private FopFactory fopFactory;

    private DefaultFoConfig() {
    }

    // Transformer is not thread safe.
    @Override
    public Transformer getTransformer() {
        try {
            return TransformerFactory.newInstance().newTransformer();
            // return TransformerFactory.newDefaultInstance().newTransformer();
        }
        catch (TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public FopFactory getFopFactory() {
        if (fopFactory == null) {
            try {
                fopFactory = createFopFactory();
            }
            catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return fopFactory;
    }

    private FopFactory createFopFactory() throws URISyntaxException {
        // Allow {@code src="images/myimage.png"} to pick up images from the classpath.
        URI defaultBaseUri = DefaultFoConfig.class.getResource("/").toURI();
        // TODO! better URI, file:, jrt:, and jar: supported by default (how did I find that out?)
        // {file=sun.net.www.protocol.file.Handler@3e2055d6, jrt=sun.net.www.protocol.jrt.Handler@50029372, jar=sun.net.www.protocol.jar.Handler@e3b3b2f}
        // Would be nice to explicitly use something like {@code src="url(classpath:images/myimage.png)"}

        EnvironmentProfile environmentProfile = new EnvironmentProfile() {
            private final ResourceResolver resourceResolver = ResourceResolverFactory.createDefaultResourceResolver();
            private final FallbackResolver fallbackResolver = new UnrestrictedFallbackResolver();

            private final FontManager fontManager = new FontManager(
                ResourceResolverFactory.createInternalResourceResolver(defaultBaseUri, resourceResolver),
                // Maybe allow the FontDetector to be configurable? Might want default. Options include: also reads system fonts (FOP default),
                // or ManifestFontDetector, or none (same as turning off auto detect).
                // TODO! log warning/error if auto detect not on and a font detector is comnfigured
                // hmm... maybe combine the detectors so per doc fonts can be ignored if also in the manifest??
                // More weight for a FoConfigBuilder?
                new MultiFontDetector(new ManifestFontDetector(), new FoWriterFontDetector()),
                FontCacheManagerFactory.createDefault());

            @Override
            public ResourceResolver getResourceResolver() {
                return resourceResolver;
            }

            @Override
            public FontManager getFontManager() {
                return fontManager;
            }

            @Override
            public FallbackResolver getFallbackResolver() {
                return fallbackResolver;
            }

            @Override
            public URI getDefaultBaseURI() {
                return defaultBaseUri;
            }
        };

        // TODO! check for a user /fop.xconf and use /fo_default.xconf if not present.
        DefaultConfiguration configuration;
        try (InputStream confStream = getClass().getResourceAsStream("/fop.xconf")) {
            configuration = new DefaultConfigurationBuilder().build(confStream);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }

        return new FopFactoryBuilder(environmentProfile)
                .setConfiguration(configuration)
                .build();
    }

}
