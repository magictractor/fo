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
package uk.co.magictractor.fo.handler;

import java.io.OutputStream;

import org.apache.fop.apps.FOUserAgent;

/**
 *
 */
public interface FoTransform {

    // IFDocumentHandler can be used with IFUtil.setUpFonts() and IFSerrializer.mimicDocumentHandler()
    // IFDocumentHandler createDocumentHandler(OutputStream out, FopFactory fopFactory, FOUserAgent userAgent);
    // TUESDAY! maybe return an Object and check it's one of IFDocumentHandler or Renderer (via AreaTreeHandler)
    // but both could be pre or post transform
    // so want four similar observables that Renderers and IFDocumentHandlers can be hooked on to.
    // fewer than four (min two) if no transforms are being done.
    // First is a custom renderer attached to an AreaTreeModel.
    // Second
    // Last two are IFRenderers, can add IFDocumentHandlers to them.
    // TODO! how to configure the output for a Renderer. ah, need to call startRenderer()
    // TODO! and what closes the stream?
    // Ah! could also return a ContentHandler to view the input from the DOMDocument
    // TODO! not default, that's only for migration of existing transforms
    Object createHandler(OutputStream out, FOUserAgent userAgent);

    /**
     * <p>
     * A file extension typically associated with transformed output. Users may
     * choose to use a different extension.
     * </p>
     * <p>
     * The extension must include a leading dot.
     * </p>
     */
    String fileExtension();

}
