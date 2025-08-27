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
import java.util.Locale;

import javax.xml.transform.Result;

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

// TODO! better name than transform, that's already used for doc generation (transform from DomDocument)
// Could extend IFDocumentHandlerProxy, but keep this and add logging?
public class IFDocumentHandlerTransform implements IFDocumentHandler {

    private static final Log LOG = LogFactory.getLog(IFDocumentHandlerTransform.class);

    private final IFDocumentHandler wrapped;

    public IFDocumentHandlerTransform(IFDocumentHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public IFContext getContext() {
        return wrapped.getContext();
    }

    @Override
    public void setResult(Result result) throws IFException {
        wrapped.setResult(result);
    }

    @Override
    public void setFontInfo(FontInfo fontInfo) {
        wrapped.setFontInfo(fontInfo);
    }

    @Override
    public FontInfo getFontInfo() {
        return wrapped.getFontInfo();
    }

    @Override
    public void setDefaultFontInfo(FontInfo fontInfo) {
        wrapped.setDefaultFontInfo(fontInfo);
    }

    @Override
    public IFDocumentHandlerConfigurator getConfigurator() {
        return wrapped.getConfigurator();
    }

    @Override
    public StructureTreeEventHandler getStructureTreeEventHandler() {
        return wrapped.getStructureTreeEventHandler();
    }

    @Override
    public IFDocumentNavigationHandler getDocumentNavigationHandler() {
        return wrapped.getDocumentNavigationHandler();
    }

    @Override
    public boolean supportsPagesOutOfOrder() {
        return wrapped.supportsPagesOutOfOrder();
    }

    @Override
    public String getMimeType() {
        LOG.trace("getMimeType()");
        return wrapped.getMimeType();
    }

    @Override
    public void startDocument() throws IFException {
        LOG.trace("startDocument()");
        wrapped.startDocument();
    }

    @Override
    public void endDocument() throws IFException {
        wrapped.endDocument();
    }

    @Override
    public void setDocumentLocale(Locale locale) {
        wrapped.setDocumentLocale(locale);
    }

    @Override
    public void startDocumentHeader() throws IFException {
        wrapped.startDocumentHeader();
    }

    @Override
    public void endDocumentHeader() throws IFException {
        wrapped.endDocumentHeader();
    }

    @Override
    public void startDocumentTrailer() throws IFException {
        wrapped.startDocumentTrailer();
    }

    @Override
    public void endDocumentTrailer() throws IFException {
        wrapped.endDocumentTrailer();
    }

    @Override
    public void startPageSequence(String id) throws IFException {
        wrapped.startPageSequence(id);
    }

    @Override
    public void endPageSequence() throws IFException {
        wrapped.endPageSequence();
    }

    @Override
    public void startPage(int index, String name, String pageMasterName, Dimension size) throws IFException {
        wrapped.startPage(index, name, pageMasterName, size);
    }

    @Override
    public void endPage() throws IFException {
        wrapped.endPage();
    }

    @Override
    public void startPageHeader() throws IFException {
        wrapped.startPageHeader();
    }

    @Override
    public void endPageHeader() throws IFException {
        wrapped.endPageHeader();
    }

    @Override
    public IFPainter startPageContent() throws IFException {
        return wrapped.startPageContent();
    }

    @Override
    public void endPageContent() throws IFException {
        wrapped.endPageContent();
    }

    @Override
    public void startPageTrailer() throws IFException {
        wrapped.startPageTrailer();
    }

    @Override
    public void endPageTrailer() throws IFException {
        wrapped.endPageTrailer();
    }

    @Override
    public void handleExtensionObject(Object extension) throws IFException {
        wrapped.handleExtensionObject(extension);
    }

}
