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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class RendererBroadcaster implements Renderer {

    private static final Log LOG = LogFactory.getLog(RendererBroadcaster.class);

    private final List<Renderer> renderers;
    private final List<OutputStream> outputStreams;

    private transient Boolean supportsOutOfOrder;
    private transient FOUserAgent userAgent;

    public RendererBroadcaster(List<Renderer> renderers, List<OutputStream> outputStreams) {
        if (renderers.size() != outputStreams.size()) {
            throw new IllegalArgumentException("Renderer and OutputStream Lists should be the same size, but renderers.size()="
                    + renderers.size() + " and outputStreams.size()=" + outputStreams.size());
        }
        this.renderers = renderers;
        this.outputStreams = outputStreams;
    }

    @Override
    public String getMimeType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startRenderer(OutputStream outputStream) throws IOException {
        if (outputStream != null) {
            throw new IllegalArgumentException("Expected OutputStream to be null");
        }
        // This gets hit from RenderPagesModel constructor.
        // This could become a no-op with startRenderer() explicitly called, but
        // then the renderer would be started before the fonts are set.
        //throw new UnsupportedOperationException("Renderers should be started individually.");

        for (int i = 0; i < renderers.size(); i++) {
            renderers.get(i).startRenderer(outputStreams.get(i));
        }
    }

    @Override
    public void stopRenderer() throws IOException {
        LOG.trace("stopRenderer()");
        for (Renderer renderer : renderers) {
            renderer.stopRenderer();
        }
    }

    @Override
    public FOUserAgent getUserAgent() {
        if (userAgent == null) {
            userAgent = verifyCommonUserAgent();
            if (userAgent == null) {
                throw new IllegalArgumentException();
            }
        }
        return userAgent;
    }

    private FOUserAgent verifyCommonUserAgent() {
        FOUserAgent result = renderers.get(0).getUserAgent();
        for (int i = 1; i < renderers.size(); i++) {
            if (renderers.get(i).getUserAgent() != result) {
                throw new IllegalStateException("Inconsistent FOUserAgents: "
                        + renderers.get(i).getUserAgent() + " and " + result);
            }
        }
        return result;
    }

    @Override
    public void setupFontInfo(FontInfo fontInfo) throws FOPException {
        LOG.trace("setupFontInfo()");
        // TODO! revisit this, it currently causes an error
        for (Renderer renderer : renderers) {
            renderer.setupFontInfo(fontInfo);
        }
    }

    @Override
    public boolean supportsOutOfOrder() {
        if (supportsOutOfOrder == null) {
            supportsOutOfOrder = verifyCommonSupportsOutOfOrder();
        }
        return supportsOutOfOrder;
    }

    private boolean verifyCommonSupportsOutOfOrder() {
        boolean result = renderers.get(0).supportsOutOfOrder();
        for (int i = 1; i < renderers.size(); i++) {
            if (renderers.get(0).supportsOutOfOrder() != result) {
                // TODO! this won't work in all cases, differences should be tolerated
                throw new IllegalStateException();
            }
        }
        return result;
    }

    @Override
    public void setDocumentLocale(Locale locale) {
        LOG.trace("setDocumentLocale()");
        for (Renderer renderer : renderers) {
            renderer.setDocumentLocale(locale);
        }
    }

    @Override
    public void processOffDocumentItem(OffDocumentItem odi) {
        LOG.trace("processOffDocumentItem()");
        for (Renderer renderer : renderers) {
            renderer.processOffDocumentItem(odi);
        }
    }

    @Override
    public Graphics2DAdapter getGraphics2DAdapter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImageAdapter getImageAdapter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void preparePage(PageViewport page) {
        LOG.trace("preparePage()");
        for (Renderer renderer : renderers) {
            renderer.preparePage(page);
        }
    }

    @Override
    public void startPageSequence(LineArea seqTitle) {
        //        LOG.trace("startPageSequence()");
        //        for (Renderer renderer : renderers) {
        //            renderer.startPageSequence(seqTitle);
        //        }
        throw new UnsupportedOperationException("Deprecated method");
    }

    @Override
    public void startPageSequence(PageSequence pageSequence) {
        LOG.trace("startPageSequence()");
        for (Renderer renderer : renderers) {
            renderer.startPageSequence(pageSequence);
        }
    }

    @Override
    public void renderPage(PageViewport page) throws IOException, FOPException {
        LOG.trace("renderPage()");
        for (Renderer renderer : renderers) {
            renderer.renderPage(page);
        }
    }

    @Override
    public String toString() {
        List<String> rendererClassNames = renderers.stream().map(r -> r.getClass().getSimpleName()).collect(Collectors.toList());
        return MoreObjects.toStringHelper(this)
                .add("renderers.className", rendererClassNames)
                .toString();
    }

}
