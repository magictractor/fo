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

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.xml.sax.ContentHandler;

/**
 *
 */
public class FoPdfTransform implements FoTransform {

    @Override
    public ContentHandler createHandler(OutputStream out, FopFactory fopFactory, FOUserAgent userAgent) {
        try {
            return fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out).getDefaultHandler();
        }
        catch (FOPException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String fileExtension() {
        return ".pdf";
    }

    @Override
    public String mimeType() {
        return MimeConstants.MIME_PDF;
    }

}
