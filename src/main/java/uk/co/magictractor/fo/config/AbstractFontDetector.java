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

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOPException;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontAdder;
import org.apache.fop.fonts.FontDetector;
import org.apache.fop.fonts.FontEventListener;
import org.apache.fop.fonts.FontTriplet;
import org.apache.fop.util.LogUtil;

/**
 * Base class for {@code FontDetector} implementations that logs information
 * about fonts loaded for each {@code URL}.
 */
public abstract class AbstractFontDetector implements FontDetector {

    // TODO! this library should use JCL rather than SL44J (FOP uses JCL). Consumers can bridge if desired.
    private final Log log = LogFactory.getLog(getClass());

    protected void addFonts(List<URL> fontUrls, FontAdder fontAdder, boolean strict, FontEventListener eventListener, List<EmbedFontInfo> fontInfoList) throws FOPException {
        for (URL fontUrl : fontUrls) {
            addFont(fontUrl, fontAdder, strict, eventListener, fontInfoList);
        }
    }

    private void addFont(URL fontUrl, FontAdder fontAdder, boolean strict, FontEventListener eventListener, List<EmbedFontInfo> fontInfoList) throws FOPException {
        int fontCountPre = fontInfoList.size();
        try {
            fontAdder.add(Arrays.asList(fontUrl), fontInfoList);
        }
        catch (URISyntaxException use) {
            LogUtil.handleException(log, use, strict);
        }

        int fontCountPost = fontInfoList.size();
        if (fontCountPost > fontCountPre) {
            if (log.isInfoEnabled()) {
                //for (int fontIndex = fontCountPre; fontIndex < fontCountPost; fontIndex++) {
                logTripletInfo(fontUrl, fontInfoList, fontCountPre, fontCountPost);
                // }
            }
        }
        else {
            // TODO! determine whether anything was logged.
            // TODO! check for duplicate
            // Can check is font cache is used on the FontManager, but would need reflection on the FontAdder (or a custom FontAdder?)
            // FontAdder created in DefaultFoConfigurator (not easy to change)
            // FontInfo has setEventListener, but no getter
            log.warn("No font added. Error or duplicate? No errors are logged if the font cache is used." + fontUrl);
        }
        // Ah! if error is cached then nothing is logged.
    }

    // TODO! ensure this gives good information from TTC or OTC files (font collections).
    // Could test against C:\WINDOWS\FONTS\msgothic.ttc, but better to find an open source TTC
    private void logTripletInfo(URL fontUrl, List<EmbedFontInfo> fontInfoList, int from, int to) {
        for (int i = from; i < to; i++) {
            log.info("Added " + fontInfoList.get(i));
        }
    }

    // ah, no, listener is already embedded within the FontAdder
    private static class FontEventListenerCounter implements FontEventListener {

        private final FontEventListener wrapped;

        private int fontLoadingErrorCount = 0;

        /* default */ FontEventListenerCounter(FontEventListener wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public void fontSubstituted(Object source, FontTriplet requested, FontTriplet effective) {
            wrapped.fontSubstituted(source, requested, effective);
        }

        @Override
        public void fontLoadingErrorAtAutoDetection(Object source, String fontURL, Exception e) {
            fontLoadingErrorCount++;
            wrapped.fontLoadingErrorAtAutoDetection(source, fontURL, e);
        }

        @Override
        public void glyphNotAvailable(Object source, char ch, String fontName) {
            wrapped.glyphNotAvailable(source, ch, fontName);
        }

        @Override
        public void fontDirectoryNotFound(Object source, String dir) {
            wrapped.fontDirectoryNotFound(source, dir);
        }

        @Override
        public void svgTextStrokedAsShapes(Object source, String fontFamily) {
            wrapped.svgTextStrokedAsShapes(source, fontFamily);
        }

    }

}
