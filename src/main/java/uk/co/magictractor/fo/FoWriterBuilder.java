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
package uk.co.magictractor.fo;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.event.LoggingEventListener;
import org.apache.fop.events.Event;
import org.apache.fop.events.EventListener;
import org.apache.fop.fo.FOTreeBuilder;
import org.apache.fop.render.AbstractRendererMaker;
import org.apache.fop.render.Renderer;
import org.apache.fop.render.intermediate.AbstractIFDocumentHandlerMaker;
import org.apache.fop.render.intermediate.IFContext;
import org.apache.fop.render.intermediate.IFDocumentHandler;
import org.apache.fop.render.intermediate.IFException;
import org.apache.fop.render.intermediate.IFRenderer;
import org.xml.sax.ContentHandler;

import uk.co.magictractor.fo.config.DefaultFoConfig;
import uk.co.magictractor.fo.config.FoConfig;
import uk.co.magictractor.fo.handler.ContentHandlerBroadcast;
import uk.co.magictractor.fo.handler.FoTransform;
import uk.co.magictractor.fo.handler.FoTransformOutputStreamFunction;
import uk.co.magictractor.fo.writer.IFDocumentHandlerBroadcaster;
import uk.co.magictractor.fo.writer.RendererBroadcaster;

public class FoWriterBuilder {

    private static final Log LOG = LogFactory.getLog(FoWriterBuilder.class);

    private static final EventListener DEFAULT_EVENT_LISTENER = new LoggingEventListener();

    private FoConfig foConfig;

    private final List<EventListener> eventListeners = new ArrayList<>();

    private final List<TransformInfo> transforms = new ArrayList<>();
    // private BiFunction<FoDocument, ContentHandler, ContentHandler> filterFunction;

    /**
     * Flag indicating whether the ContentHandler depends on the document. The
     * document could be used to change the OutputStream, such as including a
     * client reference number in the file name, or to change the content of
     * generated documents, such as putting the document title in the footer.
     */
    private boolean isDocumentDependent;
    // This ContentHandler should only be used when isDocumentDependent is false.
    private ContentHandler reusableContentHandler;

    public void addTransform(FoTransform transform, DocIO docIO) {
        transforms.add(new TransformInfo(transform, FoTransformOutputStreamFunction.forDocIO(docIO)));
    }

    public void addTransform(FoTransform transform, OutputStream out) {
        transforms.add(new TransformInfo(transform, FoTransformOutputStreamFunction.forOutputStream(out)));
    }

    // what is being filtered here?
    // ah, this was initially used for variable substitutions?
    //    public FoWriterBuilder addFilter(Function<ContentHandler, ContentHandler> filterFunction) {
    //        addFilter0((d, h) -> filterFunction.apply(h));
    //        return this;
    //    }
    //
    //    public FoWriterBuilder addFilter(BiFunction<FoDocument, ContentHandler, ContentHandler> filterFunction) {
    //        isDocumentDependent = true;
    //        addFilter0(filterFunction);
    //        return this;
    //    }
    //
    //    private void addFilter0(BiFunction<FoDocument, ContentHandler, ContentHandler> filterFunction) {
    //        if (this.filterFunction == null) {
    //            this.filterFunction = filterFunction;
    //        }
    //        else {
    //            //  this.filterFunction = this.filterFunction.andThen(filterFunction);
    //            this.filterFunction = (d, h) -> filterFunction.apply(d, this.filterFunction.apply(d, h));
    //        }
    //    }

    /**
     * <p>
     * Sets an EventListener which will be added to {@code FOUserAgent}s when
     * building {@code FoWriter}s.
     * </p>
     * <p>
     * If no {@code EventListener}s are added then a stub EventListener which
     * does nothing will be added rather than the {@code FOUserAgent} default of
     * using a {LoggingEventListener}.
     * </p>
     */
    public FoWriterBuilder addEventListener(EventListener eventListener) {
        this.eventListeners.add(eventListener);
        return this;
    }

    public FoWriter build() {
        verify();

        if (foConfig == null) {
            foConfig = DefaultFoConfig.getInstance();
        }

        Function<FoDocument, ContentHandler> contentHandlerFunction;
        if (isDocumentDependent) {
            contentHandlerFunction = this::buildContentHandler;
        }
        else {
            contentHandlerFunction = this::getReusableContentHandler;
        }

        //
        return new FoWriter(foConfig, contentHandlerFunction);
    }

    private void verify() {
        if (transforms.isEmpty()) {
            throw new IllegalStateException("No transforms have been added to the builder");
        }
    }

    private ContentHandler getReusableContentHandler(FoDocument foDocument) {
        if (reusableContentHandler == null) {
            reusableContentHandler = buildContentHandler(foDocument);
        }
        return reusableContentHandler;
    }

    private ContentHandler buildContentHandler(FoDocument foDocument) {
        try {
            return buildContentHandler0(foDocument);
        }
        catch (FOPException | IFException e) {
            throw new IllegalStateException(e);
        }
    }

    private ContentHandler buildContentHandler0(FoDocument foDocument) throws FOPException, IFException {
        FOUserAgent userAgent = buildUserAgent(foDocument);
        IFContext ifContext = new IFContext(userAgent);

        // ContentHandlers are attached to the XSL-FO XML.
        // Usually one to create the area tree, and maybe another to capture the XSL-FO.
        List<ContentHandler> contentHandlers = new ArrayList<>();
        // ContentHandlers are attached to the area tree.
        List<Renderer> preTransformRenderers = new ArrayList<>();
        List<Renderer> renderers = new ArrayList<>();
        List<OutputStream> rendererOutputStreams = new ArrayList<>();
        // ContentHandlers are attached to the intermediate format.
        List<IFDocumentHandler> preTransformDocumentHandlers = new ArrayList<>();
        List<IFDocumentHandler> documentHandlers = new ArrayList<>();

        for (TransformInfo transformInfo : transforms) {
            FoTransform transform = transformInfo.foTransform;
            OutputStream out = transformInfo.outputStreamFunction.newOutputStream(transformInfo.foTransform, foDocument);
            Object handler = transform.createHandler(out, userAgent);

            // TODO! pre transform needs a boolean on the transform
            if (handler instanceof AbstractIFDocumentHandlerMaker) {
                AbstractIFDocumentHandlerMaker maker = (AbstractIFDocumentHandlerMaker) handler;
                IFDocumentHandler documentHandler = maker.makeIFDocumentHandler(ifContext);
                documentHandler.setResult(new StreamResult(out));
                documentHandlers.add(documentHandler);
            }
            else if (handler instanceof IFDocumentHandler) {
                IFDocumentHandler documentHandler = (IFDocumentHandler) handler;
                documentHandler.setResult(new StreamResult(out));
                documentHandlers.add(documentHandler);
            }
            else if (handler instanceof Renderer) {
                Renderer renderer = (Renderer) handler;
                renderers.add(renderer);
                rendererOutputStreams.add(out);
            }
            else if (handler instanceof AbstractRendererMaker) {
                AbstractRendererMaker maker = (AbstractRendererMaker) handler;
                Renderer renderer = maker.makeRenderer(userAgent);
                maker.configureRenderer(userAgent, renderer);
                renderers.add(renderer);
                rendererOutputStreams.add(out);
            }
            else if (handler instanceof ContentHandler) {
                ContentHandler contentHandler = (ContentHandler) handler;
                contentHandlers.add(contentHandler);
            }
            else {
                String msg = "Cannot use a handler of type " + handler.getClass().getCanonicalName()
                        + " from " + transform.getClass().getSimpleName() + " .createHandler(UserAgent)";
                throw new IllegalStateException(msg);
            }
        }

        IFDocumentHandler documentHandler = null;
        if (documentHandlers.size() > 1) {
            documentHandler = new IFDocumentHandlerBroadcaster(documentHandlers);
        }
        else if (documentHandlers.size() == 1) {
            // TODO! do not wrap with broadcast, but it's handy during development
            // TODO! rename these to "Broadcaster"?
            // documentHandler = new IFDocumentHandlerBroadcast(documentHandlers);
            documentHandler = documentHandlers.get(0);
        }
        else {
            // Empty
            // Could get this if only capturing the area tree.
            LOG.debug("There are no IFDocumentHandlers");
        }

        if (documentHandler != null) {
            IFRenderer ifRenderer = new IFRenderer(userAgent);
            ifRenderer.setDocumentHandler(documentHandler);
            // TODO! mimic would be on IFSerializer.
            // ifRenderer.mim
            renderers.add(ifRenderer);
            rendererOutputStreams.add(null);
        }

        Renderer renderer = null;
        if (renderers.size() > 1) {
            renderer = new RendererBroadcaster(renderers, rendererOutputStreams);
        }
        else if (renderers.size() == 1) {
            renderer = renderers.get(0);
        }

        if (renderer != null) {
            // new FOTreeBuilder(outputFormat, foUserAgent, stream);
            userAgent.setRendererOverride(renderer);
            ContentHandler areaTreeHandler = new FOTreeBuilder(null, userAgent, null);
            contentHandlers.add(areaTreeHandler);
        }

        ContentHandler contentHandler;
        if (contentHandlers.size() == 1) {
            contentHandler = contentHandlers.get(0);
            LOG.debug("Single ContentHandler of type " + contentHandler.getClass().getSimpleName());
        }
        else if (contentHandlers.size() > 1) {
            contentHandler = new ContentHandlerBroadcast(contentHandlers);
            if (LOG.isDebugEnabled()) {
                // TODO! log the types too
                LOG.debug("Combined " + contentHandlers.size() + " ContentHandlers");
            }
        }
        else {
            throw new IllegalStateException("No ContentHandlers");
        }

        return contentHandler;
    }

    private FOUserAgent buildUserAgent(FoDocument foDocument) {
        FopFactory fopFactory = foConfig.getFopFactory();

        //fopFactory.getElementMappingRegistry().addElementMapping(new MTXElementMapping());

        FOUserAgent userAgent = fopFactory.newFOUserAgent();
        // Metadata is set on the DOM, allowing more metadata fields to be set.
        // Some DOM values would be overridden by values from the FOUserAgent,
        // including producer that has a non-null default which must be cleared
        // otherwise it will be used.
        if (foDocument.getMetadata().getProducer() != null) {
            userAgent.setProducer(null);
        }

        if (eventListeners.isEmpty()) {
            // Provide a default LoggingEventListener, otherwise FOP
            // will provide a default that is too noisy.
            userAgent.getEventBroadcaster().addEventListener(this::defaultEventListener);
        }
        else {
            eventListeners.forEach(userAgent.getEventBroadcaster()::addEventListener);
        }

        return userAgent;
    }

    /**
     * This method is added to the {@code FOUserAgent.addEventListener()} if no
     * event listeners have been added to this builder. This prevents
     * {@code FOUserAgent}'s fallback behaviour of using a
     * {LoggingEventListener}.
     * </p>
     */
    // https://lists.apache.org/thread/lsogbdrq1h9rwbg8y7w67b4374bpjg3n
    // https://xmlgraphics.apache.org/fop/2.1/events.html
    private void defaultEventListener(Event event) {
        // Do nothing.
        if ("endPage".equals(event.getEventKey())) {
            return;
        }

        // Continue to log everything else.
        //
        // This was previously too aggressive and hid issues with loading a font (Font Awesome 6 .otf files). See FontEventListener.
        // The font issue still exist with 2.11. Delete ~/.fop/fop-cache to see it.
        DEFAULT_EVENT_LISTENER.processEvent(event);
    }

    private static class TransformInfo {
        private final FoTransform foTransform;
        private final FoTransformOutputStreamFunction outputStreamFunction;

        private TransformInfo(FoTransform foTransform, FoTransformOutputStreamFunction outputStreamFunction) {
            this.foTransform = foTransform;
            this.outputStreamFunction = outputStreamFunction;
        }

        // public String toString() {
        //     return MoreObjec
        // }
    }

}
