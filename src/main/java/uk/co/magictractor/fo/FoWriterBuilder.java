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
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.event.LoggingEventListener;
import org.apache.fop.events.Event;
import org.apache.fop.events.EventListener;
import org.xml.sax.ContentHandler;

import uk.co.magictractor.fo.config.DefaultFoConfig;
import uk.co.magictractor.fo.config.FoConfig;
import uk.co.magictractor.fo.handler.ContentHandlerBroadcast;
import uk.co.magictractor.fo.handler.FoTransform;
import uk.co.magictractor.fo.handler.FoTransformOutputStreamFunction;

public class FoWriterBuilder {

    private static final EventListener DEFAULT_EVENT_LISTENER = new LoggingEventListener();

    private FoConfig foConfig;

    private final List<EventListener> eventListeners = new ArrayList<>();

    private final List<TransformInfo> transforms = new ArrayList<>();
    private BiFunction<FoDocument, ContentHandler, ContentHandler> filterFunction;

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

    public FoWriterBuilder addFilter(Function<ContentHandler, ContentHandler> filterFunction) {
        addFilter0((d, h) -> filterFunction.apply(h));
        return this;
    }

    public FoWriterBuilder addFilter(BiFunction<FoDocument, ContentHandler, ContentHandler> filterFunction) {
        isDocumentDependent = true;
        addFilter0(filterFunction);
        return this;
    }

    private void addFilter0(BiFunction<FoDocument, ContentHandler, ContentHandler> filterFunction) {
        if (this.filterFunction == null) {
            this.filterFunction = filterFunction;
        }
        else {
            //  this.filterFunction = this.filterFunction.andThen(filterFunction);
            this.filterFunction = (d, h) -> filterFunction.apply(d, this.filterFunction.apply(d, h));
        }
    }

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
        FOUserAgent userAgent = buildUserAgent(foDocument);

        ContentHandler result;

        // verify() has already checked for empty
        if (transforms.size() == 1) {
            result = buildTransformHandler(transforms.get(0), foDocument, userAgent);
        }
        else {
            ContentHandlerBroadcast handlers = new ContentHandlerBroadcast();
            for (TransformInfo transformInfo : transforms) {
                ContentHandler handler = buildTransformHandler(transformInfo, foDocument, userAgent);
                handlers.addHandler(handler);
            }
            result = handlers;
        }

        if (filterFunction != null) {
            result = filterFunction.apply(foDocument, result);
        }

        return result;
    }

    private FOUserAgent buildUserAgent(FoDocument foDocument) {
        FopFactory fopFactory = foConfig.getFopFactory();

        FOUserAgent userAgent = fopFactory.newFOUserAgent();
        // Metadata is set on the DOM, allowing more metadata fields to be set.
        // Some DOM values would be overridden by values from the FOUserAgent,
        // including producer that has a non-null default which must be cleared
        // otherwise it will be used.
        if (foDocument.getMetadata().getProducer() != null) {
            userAgent.setProducer(null);
        }

        if (eventListeners.isEmpty()) {
            // TODO! revisit this. The default is too noisy, but this suppression is too aggressive
            // and hid issues with loading a font (Font Awesome 6 .otf files). See FontEventListener.
            //
            // Use a stub rather than the default LoggingEventListener.
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

        DEFAULT_EVENT_LISTENER.processEvent(event);
    }

    private ContentHandler buildTransformHandler(TransformInfo transformInfo, FoDocument foDocument, FOUserAgent userAgent) {
        OutputStream out = transformInfo.outputStreamFunction.newOutputStream(transformInfo.foTransform, foDocument);
        return transformInfo.foTransform.createHandler(out, foConfig.getFopFactory(), userAgent);
    }

    private static class TransformInfo {
        private final FoTransform foTransform;
        private final FoTransformOutputStreamFunction outputStreamFunction;

        private TransformInfo(FoTransform foTransform, FoTransformOutputStreamFunction outputStreamFunction) {
            this.foTransform = foTransform;
            this.outputStreamFunction = outputStreamFunction;
        }
    }

}
