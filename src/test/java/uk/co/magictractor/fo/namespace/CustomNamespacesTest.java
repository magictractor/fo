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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CustomNamespacesTest {

    @Test
    public void testPutNamespace() {
        CustomNamespaces namespaces = new CustomNamespaces();
        namespaces.putNamespace("alt", Namespaces.NAMESPACE_URI_PDF);
        assertThat(namespaces.pdf().getPrefix()).isEqualTo("alt");
    }

    @Test
    public void testPutNamespace_duplicate() {
        CustomNamespaces namespaces = new CustomNamespaces();
        namespaces.putNamespace("alt", Namespaces.NAMESPACE_URI_PDF);
        namespaces.putNamespace("alt", Namespaces.NAMESPACE_URI_PDF);
        assertThat(namespaces.pdf().getPrefix()).isEqualTo("alt");
    }

    @Test
    public void testPutNamespace_change() {
        CustomNamespaces namespaces = new CustomNamespaces();
        namespaces.putNamespace("alt1", Namespaces.NAMESPACE_URI_PDF);
        namespaces.putNamespace("alt2", Namespaces.NAMESPACE_URI_PDF);
        assertThat(namespaces.pdf().getPrefix()).isEqualTo("alt2");
    }

    @Test
    public void testPutNamespace_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        namespaces.putNamespace("pdf", Namespaces.NAMESPACE_URI_PDF);
        assertThat(namespaces.pdf()).isSameAs(DefaultNamespaces.get().pdf());
    }

    @Test
    public void testPutNamespace_revert() {
        CustomNamespaces namespaces = new CustomNamespaces();
        namespaces.putNamespace("alt", Namespaces.NAMESPACE_URI_PDF);
        namespaces.putNamespace("pdf", Namespaces.NAMESPACE_URI_PDF);
        assertThat(namespaces.pdf()).isSameAs(DefaultNamespaces.get().pdf());
    }

    @Test
    public void testPutNamespace_unknown() {
        CustomNamespaces namespaces = new CustomNamespaces();
        String uri = "http://new.magictractor.co.uk/never/seen/before/1.0";
        namespaces.putNamespace("new", uri);
        assertThat(namespaces.forUri(uri).getPrefix()).isEqualTo("new");
    }

    @Test
    public void testOrFallback_withoutCustomNamespaces() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.orFallback()).isSameAs(DefaultNamespaces.get());
    }

    @Test
    public void testOrFallback_withCustomNamespace() {
        CustomNamespaces namespaces = new CustomNamespaces();
        namespaces.putNamespace("alt", Namespaces.NAMESPACE_URI_PDF);
        assertThat(namespaces.orFallback()).isSameAs(namespaces);
    }

    @Test
    public void testOrFallback_withCustomNamespaceRemoved() {
        CustomNamespaces namespaces = new CustomNamespaces();
        namespaces.putNamespace("alt", Namespaces.NAMESPACE_URI_PDF);
        namespaces.putNamespace("pdf", Namespaces.NAMESPACE_URI_PDF);
        assertThat(namespaces.orFallback()).isSameAs(DefaultNamespaces.get());
    }

    @Test
    public void testXml_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.xml()).isSameAs(DefaultNamespaces.get().xml());
    }

    @Test
    public void testXmlns_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.xmlns()).isSameAs(DefaultNamespaces.get().xmlns());
    }

    @Test
    public void testFo_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.fo()).isSameAs(DefaultNamespaces.get().fo());
    }

    @Test
    public void testDc_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.dc()).isSameAs(DefaultNamespaces.get().dc());
    }

    @Test
    public void testXmp_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.xmp()).isSameAs(DefaultNamespaces.get().xmp());
    }

    @Test
    public void testPdf_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.pdf()).isSameAs(DefaultNamespaces.get().pdf());
    }

    @Test
    public void testFox_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.fox()).isSameAs(DefaultNamespaces.get().fox());
    }

    @Test
    public void testX_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.x()).isSameAs(DefaultNamespaces.get().x());
    }

    @Test
    public void testRdf_default() {
        CustomNamespaces namespaces = new CustomNamespaces();
        assertThat(namespaces.rdf()).isSameAs(DefaultNamespaces.get().rdf());
    }

}
