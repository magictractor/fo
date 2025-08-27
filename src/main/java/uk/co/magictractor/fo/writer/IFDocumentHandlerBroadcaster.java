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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.transform.Result;

import com.google.common.base.MoreObjects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.accessibility.StructureTreeEventHandler;
import org.apache.fop.fonts.FontInfo;
import org.apache.fop.render.intermediate.IFContext;
import org.apache.fop.render.intermediate.IFDocumentHandler;
import org.apache.fop.render.intermediate.IFDocumentHandlerConfigurator;
import org.apache.fop.render.intermediate.IFDocumentNavigationHandler;
import org.apache.fop.render.intermediate.IFException;
import org.apache.fop.render.intermediate.IFPainter;

public class IFDocumentHandlerBroadcaster implements IFDocumentHandler {

    private static final Log LOG = LogFactory.getLog(IFDocumentHandlerBroadcaster.class);

    private final List<IFDocumentHandler> handlers;

    private transient IFDocumentNavigationHandler documentNavigationHandler;
    private transient Optional<IFContext> ifContext;
    private transient IFDocumentHandlerConfigurator configurator;
    private transient String mimeType;

    // TODO! param check for size>=2, but short term size=1 might be handy during development
    public IFDocumentHandlerBroadcaster(List<IFDocumentHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public IFContext getContext() {
        if (ifContext == null) {
            ifContext = Optional.ofNullable(verifyCommonContext());
        }
        return ifContext.orElse(null);
    }

    private IFContext verifyCommonContext() {
        IFContext result = handlers.get(0).getContext();
        for (int i = 1; i < handlers.size(); i++) {
            if (handlers.get(i).getContext() != result) {
                throw new IllegalStateException();
            }
        }
        return result;
    }

    @Override
    public void setResult(Result result) throws IFException {
        // Result wraps the OutputStream for each handler.
        throw new UnsupportedOperationException("setResult() must be called for each IFDocumentHandler");
    }

    @Override
    public void setFontInfo(FontInfo fontInfo) {
        LOG.trace("setFontInfo");
        for (IFDocumentHandler handler : handlers) {
            handler.setFontInfo(fontInfo);
        }
    }

    @Override
    public FontInfo getFontInfo() {
        // Could concatenate? Would need to check for dups?
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultFontInfo(FontInfo fontInfo) {
        LOG.trace("setDefaultFontInfo");
        for (IFDocumentHandler handler : handlers) {
            handler.setDefaultFontInfo(fontInfo);
        }
    }

    @Override
    public IFDocumentHandlerConfigurator getConfigurator() {
        // PDFRendererConfigurator and DefaultRendererConfigurator
        // another Broadcaster??
        //        for (IFDocumentHandler handler : handlers) {
        //            LOG.info("configurator: " + handler.getConfigurator());
        //        }
        //        throw new UnsupportedOperationException();

        if (configurator == null) {
            configurator = createConfigurator();
        }
        return configurator;
    }

    private IFDocumentHandlerConfigurator createConfigurator() {
        ArrayList<IFDocumentHandlerConfigurator> configurators = new ArrayList<>(handlers.size());
        for (IFDocumentHandler handler : handlers) {
            configurators.add(handler.getConfigurator());
        }

        return new IFDocumentHandlerConfiguratorBroadcast(configurators);
    }

    @Override
    public StructureTreeEventHandler getStructureTreeEventHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFDocumentNavigationHandler getDocumentNavigationHandler() {
        if (documentNavigationHandler == null) {
            documentNavigationHandler = createDocumentNavigationHandler();
        }
        return documentNavigationHandler;
    }

    private IFDocumentNavigationHandler createDocumentNavigationHandler() {
        List<IFDocumentNavigationHandler> navigationHandlers = handlers.stream()
                .map(IFDocumentHandler::getDocumentNavigationHandler)
                .collect(Collectors.toList());

        return new IFDocumentNavigationHandlerBroadcaster(navigationHandlers);
    }

    @Override
    public boolean supportsPagesOutOfOrder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMimeType() {
        if (mimeType == null) {
            mimeType = createMimeType();
        }
        return mimeType;
    }

    private String createMimeType() {
        return handlers.stream()
                .map(IFDocumentHandler::getMimeType)
                .collect(Collectors.joining(","));
    }

    @Override
    public void startDocument() throws IFException {
        LOG.trace("startDocument()");
        for (IFDocumentHandler handler : handlers) {
            handler.startDocument();
        }
    }

    @Override
    public void endDocument() throws IFException {
        LOG.trace("endDocument()");
        for (IFDocumentHandler handler : handlers) {
            handler.endDocument();
        }
    }

    @Override
    public void setDocumentLocale(Locale locale) {
        LOG.trace("setDocumentLocale()");
        for (IFDocumentHandler handler : handlers) {
            handler.setDocumentLocale(locale);
        }
    }

    @Override
    public void startDocumentHeader() throws IFException {
        LOG.trace("startDocumentHeader()");
        for (IFDocumentHandler handler : handlers) {
            handler.startDocumentHeader();
        }
    }

    @Override
    public void endDocumentHeader() throws IFException {
        LOG.trace("endDocumentHeader()");
        for (IFDocumentHandler handler : handlers) {
            handler.endDocumentHeader();
        }
    }

    @Override
    public void startDocumentTrailer() throws IFException {
        LOG.trace("startDocumentTrailer()");
        for (IFDocumentHandler handler : handlers) {
            handler.startDocumentTrailer();
        }
    }

    @Override
    public void endDocumentTrailer() throws IFException {
        LOG.trace("endDocumentTrailer()");
        for (IFDocumentHandler handler : handlers) {
            handler.endDocumentTrailer();
        }
    }

    @Override
    public void startPageSequence(String id) throws IFException {
        LOG.trace("startPageSequence()");
        for (IFDocumentHandler handler : handlers) {
            handler.startPageSequence(id);
        }
    }

    @Override
    public void endPageSequence() throws IFException {
        LOG.trace("endPageSequence()");
        for (IFDocumentHandler handler : handlers) {
            handler.endPageSequence();
        }
    }

    @Override
    public void startPage(int index, String name, String pageMasterName, Dimension size) throws IFException {
        LOG.trace("startPage()");
        for (IFDocumentHandler handler : handlers) {
            handler.startPage(index, name, pageMasterName, size);
        }
    }

    @Override
    public void endPage() throws IFException {
        LOG.trace("endPage()");
        for (IFDocumentHandler handler : handlers) {
            handler.endPage();
        }
    }

    @Override
    public void startPageHeader() throws IFException {
        LOG.trace("startPageHeader()");
        for (IFDocumentHandler handler : handlers) {
            handler.startPageHeader();
        }
    }

    @Override
    public void endPageHeader() throws IFException {
        LOG.trace("endPageHeader()");
        for (IFDocumentHandler handler : handlers) {
            handler.endPageHeader();
        }
    }

    @Override
    public IFPainter startPageContent() throws IFException {
        List<IFPainter> painters = new ArrayList<>(handlers.size());

        LOG.trace("startPageContent()");
        for (IFDocumentHandler handler : handlers) {
            IFPainter painter = handler.startPageContent();
            painters.add(painter);
        }

        return new IFPainterBroadcaster(painters);
    }

    @Override
    public void endPageContent() throws IFException {
        LOG.trace("endPageContent()");
        for (IFDocumentHandler handler : handlers) {
            handler.endPageContent();
        }
    }

    @Override
    public void startPageTrailer() throws IFException {
        LOG.trace("startPageTrailer()");
        for (IFDocumentHandler handler : handlers) {
            handler.startPageTrailer();
        }
    }

    @Override
    public void endPageTrailer() throws IFException {
        LOG.trace("endPageTrailer()");
        for (IFDocumentHandler handler : handlers) {
            handler.endPageTrailer();
        }
    }

    @Override
    public void handleExtensionObject(Object extension) throws IFException {
        LOG.trace("handleExtensionObject()");
        for (IFDocumentHandler handler : handlers) {
            handler.handleExtensionObject(extension);
        }
    }

    public List<IFDocumentHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    @Override
    public String toString() {
        List<String> handlerClassNames = handlers.stream().map(r -> r.getClass().getSimpleName()).collect(Collectors.toList());
        return MoreObjects.toStringHelper(this)
                .add("handlers.className", handlerClassNames)
                .toString();
    }

}
