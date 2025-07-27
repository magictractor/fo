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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

import uk.co.magictractor.fo.mockito.ContentAndLexicalHandler;
import uk.co.magictractor.fo.mockito.MockitoExtension;

public class ContentHandlerBroadcastTest {

    @RegisterExtension
    public MockitoExtension mockitoExtension = new MockitoExtension();

    @Test
    public void testLexicalHandlers_empty() throws Exception {
        ContentHandlerBroadcast handler = new ContentHandlerBroadcast();

        assertThat(handler.getLexicalHandler()).isNull();
    }

    @Test
    public void testLexicalHandlers_onlyContentHandler() throws Exception {
        ContentHandlerBroadcast handler = new ContentHandlerBroadcast();

        ContentHandler child1 = mockitoExtension.mock(ContentHandler.class);
        handler.addHandler(child1);

        assertThat(handler.getLexicalHandler()).isNull();
    }

    @Test
    public void testLexicalHandlers_one() throws Exception {
        ContentHandlerBroadcast handler = new ContentHandlerBroadcast();

        ContentAndLexicalHandler child1 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        handler.addHandler(child1);

        assertThat(handler.getLexicalHandler()).isSameAs(child1);

        char[] ch = "test".toCharArray();
        handler.getLexicalHandler().comment(ch, 0, ch.length);
        Mockito.verify(child1).comment(ch, 0, ch.length);
    }

    @Test
    public void testLexicalHandlers_two() throws Exception {
        ContentHandlerBroadcast handler = new ContentHandlerBroadcast();

        ContentAndLexicalHandler child1 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        handler.addHandler(child1);
        ContentAndLexicalHandler child2 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        handler.addHandler(child2);

        assertThat(handler.getLexicalHandler()).isExactlyInstanceOf(LexicalHandlerBroadcast.class);

        char[] ch = "test".toCharArray();
        handler.getLexicalHandler().comment(ch, 0, ch.length);
        Mockito.verify(child1).comment(ch, 0, ch.length);
        Mockito.verify(child2).comment(ch, 0, ch.length);
    }

    @Test
    public void testLexicalHandlers_three() throws Exception {
        ContentHandlerBroadcast handler = new ContentHandlerBroadcast();

        ContentAndLexicalHandler child1 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        handler.addHandler(child1);
        ContentAndLexicalHandler child2 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        handler.addHandler(child2);
        ContentAndLexicalHandler child3 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        handler.addHandler(child3);

        assertThat(handler.getLexicalHandler()).isExactlyInstanceOf(LexicalHandlerBroadcast.class);

        char[] ch = "test".toCharArray();
        handler.getLexicalHandler().comment(ch, 0, ch.length);
        Mockito.verify(child1).comment(ch, 0, ch.length);
        Mockito.verify(child2).comment(ch, 0, ch.length);
        Mockito.verify(child3).comment(ch, 0, ch.length);
    }

    @Test
    public void testLexicalHandlers_hasLexicalHandler() throws Exception {
        ContentHandlerBroadcast handler = new ContentHandlerBroadcast();

        ContentHandlerBroadcast child1 = new ContentHandlerBroadcast();

        ContentAndLexicalHandler grandChild1 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        child1.addHandler(grandChild1);

        // In this test child1 is added after HasLexicalHandler is not null.
        // Compare with the next test.
        handler.addHandler(child1);

        assertThat(handler).isInstanceOf(HasLexicalHandler.class);
        assertThat(handler).isNotInstanceOf(LexicalHandler.class);
        assertThat(handler.getLexicalHandler()).isSameAs(grandChild1);

        char[] ch = "test".toCharArray();
        handler.getLexicalHandler().comment(ch, 0, ch.length);
        Mockito.verify(grandChild1).comment(ch, 0, ch.length);
    }

    @Test
    public void testLexicalHandlers_hasLexicalHandlerDeferred() throws Exception {
        ContentHandlerBroadcast handler = new ContentHandlerBroadcast();

        ContentHandlerBroadcast child1 = new ContentHandlerBroadcast();
        handler.addHandler(child1);

        // Note the different order from the previous test.
        // child1 did not have a LexicalHandler when it was added, but now it does.
        ContentAndLexicalHandler grandChild1 = mockitoExtension.mock(ContentAndLexicalHandler.class);
        child1.addHandler(grandChild1);

        assertThat(handler.getLexicalHandler()).isSameAs(grandChild1);

        char[] ch = "test".toCharArray();
        handler.getLexicalHandler().comment(ch, 0, ch.length);
        Mockito.verify(grandChild1).comment(ch, 0, ch.length);
    }

}
