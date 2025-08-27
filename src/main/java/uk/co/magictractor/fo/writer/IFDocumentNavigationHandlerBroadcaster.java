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

import org.apache.fop.render.intermediate.IFDocumentNavigationHandler;
import org.apache.fop.render.intermediate.IFException;
import org.apache.fop.render.intermediate.extensions.AbstractAction;
import org.apache.fop.render.intermediate.extensions.BookmarkTree;
import org.apache.fop.render.intermediate.extensions.Link;
import org.apache.fop.render.intermediate.extensions.NamedDestination;

public class IFDocumentNavigationHandlerBroadcaster implements IFDocumentNavigationHandler {

    private final List<IFDocumentNavigationHandler> handlers;

    public IFDocumentNavigationHandlerBroadcaster(List<IFDocumentNavigationHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void renderNamedDestination(NamedDestination destination) throws IFException {
        for (IFDocumentNavigationHandler handler : handlers) {
            handler.renderNamedDestination(destination);
        }
    }

    @Override
    public void renderBookmarkTree(BookmarkTree tree) throws IFException {
        for (IFDocumentNavigationHandler handler : handlers) {
            handler.renderBookmarkTree(tree);
        }
    }

    @Override
    public void renderLink(Link link) throws IFException {
        for (IFDocumentNavigationHandler handler : handlers) {
            handler.renderLink(link);
        }
    }

    @Override
    public void addResolvedAction(AbstractAction action) throws IFException {
        for (IFDocumentNavigationHandler handler : handlers) {
            handler.addResolvedAction(action);
        }
    }

    @Override
    public int getPageIndex() {
        throw new UnsupportedOperationException();
    }

}
