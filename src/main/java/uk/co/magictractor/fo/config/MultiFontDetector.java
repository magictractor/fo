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

import java.util.Arrays;
import java.util.List;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontAdder;
import org.apache.fop.fonts.FontDetector;
import org.apache.fop.fonts.FontEventListener;
import org.apache.fop.fonts.FontManager;

/**
 * <p>
 * Allows both {@code ManifestFontDetector} and {@code FoWriterFontDetector} to
 * be used at the same time.
 * </p>
 */
public class MultiFontDetector implements FontDetector {

    private final List<FontDetector> delegates;

    public MultiFontDetector(FontDetector... delegates) {
        this.delegates = Arrays.asList(delegates);
    }

    @Override
    public void detect(FontManager fontManager, FontAdder fontAdder, boolean strict, FontEventListener eventListener, List<EmbedFontInfo> fontInfoList) throws FOPException {
        for (FontDetector delegate : delegates) {
            delegate.detect(fontManager, fontAdder, strict, eventListener, fontInfoList);
        }
    }

}
