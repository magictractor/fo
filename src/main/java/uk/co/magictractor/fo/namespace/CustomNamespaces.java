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
package uk.co.magictractor.fo.namespace;

import java.util.HashMap;
import java.util.Map;

public class CustomNamespaces implements Namespaces {

    private final Map<String, Namespace> uriMap = new HashMap<>();
    private final Namespaces fallback;

    public CustomNamespaces() {
        this(DefaultNamespaces.get());
    }

    public CustomNamespaces(Namespaces fallback) {
        this.fallback = fallback;
    }

    public void putNamespace(String prefix, String uri) {
        Namespace existingNamespace = uriMap.get(uri);
        if (existingNamespace != null && existingNamespace.getPrefix().equals(prefix)) {
            // Already using this custom Namespace. Do nothing.
            return;
        }

        Namespace fallbackNamespace = fallback.forUri(uri);
        if (fallbackNamespace != null && fallbackNamespace.getPrefix().equals(prefix)) {
            // Fallback is sufficient.
            if (existingNamespace != null) {
                uriMap.remove(uri);
            }
            return;
        }

        uriMap.put(uri, new Namespace(prefix, uri));
    }

    public Namespaces orFallback() {
        return uriMap.isEmpty() ? fallback : this;
    }

    @Override
    public Namespace forUri(String namespaceUri) {
        Namespace result = uriMap.get(namespaceUri);
        if (result == null) {
            result = fallback.forUri(namespaceUri);
        }
        return result;
    }

    @Override
    public Namespace xml() {
        // TODO! or ensure no override possible and return constant
        return forUri(NAMESPACE_URI_XML);
    }

    @Override
    public Namespace xmlns() {
        // TODO! or ensure no override possible and return constant
        return forUri(NAMESPACE_URI_XMLNS);
    }

    @Override
    public Namespace fo() {
        return forUri(NAMESPACE_URI_FO);
    }

    @Override
    public Namespace dc() {
        return forUri(NAMESPACE_URI_DC);
    }

    @Override
    public Namespace xmp() {
        return forUri(NAMESPACE_URI_XMP);
    }

    @Override
    public Namespace pdf() {
        return forUri(NAMESPACE_URI_PDF);
    }

    @Override
    public Namespace fox() {
        return forUri(NAMESPACE_URI_FOX);
    }

    @Override
    public Namespace x() {
        return forUri(NAMESPACE_URI_X);
    }

    @Override
    public Namespace rdf() {
        return forUri(NAMESPACE_URI_RDF);
    }

    @Override
    public Namespace mtx() {
        return forUri(NAMESPACE_URI_MTX);
    }

}
