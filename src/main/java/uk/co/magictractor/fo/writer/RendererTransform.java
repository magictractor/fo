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
package uk.co.magictractor.fo.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.area.LineArea;
import org.apache.fop.area.OffDocumentItem;
import org.apache.fop.area.PageSequence;
import org.apache.fop.area.PageViewport;
import org.apache.fop.fonts.FontInfo;
import org.apache.fop.render.Graphics2DAdapter;
import org.apache.fop.render.ImageAdapter;
import org.apache.fop.render.Renderer;

/**
 *
 */
//TODO! better name than transform, that's already used for doc generation (transform from DomDocument)
public class RendererTransform implements Renderer {

    @Override
    public String getMimeType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void startRenderer(OutputStream outputStream) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopRenderer() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public FOUserAgent getUserAgent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setupFontInfo(FontInfo fontInfo) throws FOPException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean supportsOutOfOrder() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setDocumentLocale(Locale locale) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processOffDocumentItem(OffDocumentItem odi) {
        // TODO Auto-generated method stub

    }

    @Override
    public Graphics2DAdapter getGraphics2DAdapter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImageAdapter getImageAdapter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void preparePage(PageViewport page) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startPageSequence(LineArea seqTitle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startPageSequence(PageSequence pageSequence) {
        // TODO Auto-generated method stub

    }

    @Override
    public void renderPage(PageViewport page) throws IOException, FOPException {
        // TODO Auto-generated method stub

    }

}
