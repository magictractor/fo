/**
 * Copyright 2025 Ken Dobson
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

/**
 * <p>
 * </p>
 * <p>
 * </p>
 */
public class FoWriterFontDetector extends AbstractFontDetector {

    private static ThreadLocal<List<URL>> documentFontUrls = new ThreadLocal<>();

    public static void setFontUrls(List<URL> fontUrls) {
        if (documentFontUrls.get() != null) {
            // Could be something bad. Likely reset() wasn't called for an earlier document.
            // Tidy this ThreadLocal to prevent this Thread being unusable.
            documentFontUrls.remove();
            // Bomb to force investigation.
            throw new IllegalStateException("Font URLs have already been set, perhaps reset() wasn't called previously.");
        }
        documentFontUrls.set(fontUrls);
    }

    public static void reset() {
        //       if (documentFontUrls.get() == null) {
        //
        //       }
        documentFontUrls.remove();
    }

    @Override
    public void detect(FontManager fontManager, FontAdder fontAdder, boolean strict, FontEventListener eventListener, List<EmbedFontInfo> fontInfoList) throws FOPException {
        List<URL> fontUrls = documentFontUrls.get();
        if (fontUrls != null) {
            addFonts(fontUrls, fontAdder, strict, eventListener, fontInfoList);
        }
    }

}
