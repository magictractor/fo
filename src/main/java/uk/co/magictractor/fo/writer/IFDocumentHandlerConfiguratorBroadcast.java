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

import java.util.List;

import com.google.common.base.Splitter;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fonts.FontInfo;
import org.apache.fop.render.intermediate.IFDocumentHandler;
import org.apache.fop.render.intermediate.IFDocumentHandlerConfigurator;

public class IFDocumentHandlerConfiguratorBroadcast implements IFDocumentHandlerConfigurator {

    private final List<IFDocumentHandlerConfigurator> configurators;

    public IFDocumentHandlerConfiguratorBroadcast(List<IFDocumentHandlerConfigurator> configurators) {
        this.configurators = configurators;
    }

    @Override
    public void configure(IFDocumentHandler documentHandler) throws FOPException {
        if (!IFDocumentHandlerBroadcaster.class.equals(documentHandler.getClass())) {
            throw new IllegalArgumentException();
        }

        // IFDocumentHandlerBroadcast documentHandlerBroadcast = (IFDocumentHandlerBroadcast) documentHandler;
        List<IFDocumentHandler> handlers = ((IFDocumentHandlerBroadcaster) documentHandler).getHandlers();
        if (handlers.size() != configurators.size()) {
            throw new IllegalStateException();
        }

        for (int i = 0; i < configurators.size(); i++) {
            configurators.get(i).configure(handlers.get(i));
        }
    }

    @Override
    public void setupFontInfo(String mimeType, FontInfo fontInfo) throws FOPException {
        // throw new UnsupportedOperationException();
        List<String> mimeTypes = Splitter.on(',').splitToList(mimeType);
        if (mimeTypes.size() != configurators.size()) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < configurators.size(); i++) {
            configurators.get(i).setupFontInfo(mimeTypes.get(i), fontInfo);
        }
    }

}
