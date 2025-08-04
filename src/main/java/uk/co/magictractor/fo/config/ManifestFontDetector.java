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

import java.net.URL;
import java.util.List;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontAdder;
import org.apache.fop.fonts.FontEventListener;
import org.apache.fop.fonts.FontManager;
import org.apache.xmlgraphics.util.ClasspathResource;

/**
 * <p>
 * A stripped down version of {@code FontDetectorFactory$DefaultFontDetector}
 * that detects only fonts defined in manifest files.
 * </p>
 * <p>
 * To use this, {@code auto-detect} must be turned on in the configuration, and
 * an {@code EnviromentProfile} containing this detector must be passed to the
 * {@code FopFactoryBuilder}.
 * </p>
 */
public class ManifestFontDetector extends AbstractFontDetector {

    /**
     * {@code application/x-font} and {@code application/x-font-truetype} are
     * copied from FontDetectorFactory$DefaultFontDetector, but are not standard
     * mime types. RFC 8081 added {@code font/*} media types.
     *
     * @see https://www.rfc-editor.org/rfc/rfc8081
     * @see https://www.iana.org/assignments/media-types/media-types.xhtml#font
     */
    private static final String[] FONT_MIMETYPES = {
            "application/x-font", "application/x-font-truetype"
    };

    @Override
    public void detect(FontManager fontManager, FontAdder fontAdder, boolean strict, FontEventListener eventListener, List<EmbedFontInfo> fontInfoList) throws FOPException {
        ClasspathResource resource = ClasspathResource.getInstance();
        for (String mimeTypes : FONT_MIMETYPES) {
            @SuppressWarnings("unchecked")
            List<URL> fontUrls = resource.listResourcesOfMimeType(mimeTypes);
            super.addFonts(fontUrls, fontAdder, strict, eventListener, fontInfoList);
        }
    }

}
