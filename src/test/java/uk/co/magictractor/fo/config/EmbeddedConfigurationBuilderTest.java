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
package uk.co.magictractor.fo.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.apache.fop.configuration.DefaultConfiguration;
import org.junit.jupiter.api.Test;

import uk.co.magictractor.fo.config.EmbeddedConfigurationBuilder;

/**
 *
 */
public class EmbeddedConfigurationBuilderTest {

    @Test
    public void testWithFontDirectory() {
        EmbeddedConfigurationBuilder builder = new EmbeddedConfigurationBuilder();
        builder.withFontDirectory("c:\\foo\\bar.ttf");

        assertThat(toString(builder)).contains("<directory>c:\\foo\\bar.ttf</directory>");
    }

    @Test
    public void testGetPageSize_existing() {
        EmbeddedConfigurationBuilder builder = createBuilderFromXml();

        // <default-page-settings height="11.00in" width="8.50in"/>
        assertThat(builder.getDefaultPageHeight()).isEqualTo("11.00in");
        assertThat(builder.getDefaultPageWidth()).isEqualTo("8.50in");
    }

    @Test
    public void testGetPageSize_blank() {
        EmbeddedConfigurationBuilder builder = new EmbeddedConfigurationBuilder();

        assertThat(builder.getDefaultPageHeight()).isNull();
        assertThat(builder.getDefaultPageWidth()).isNull();
    }

    @Test
    public void testWithDefaultPageSettings() {
        EmbeddedConfigurationBuilder builder = new EmbeddedConfigurationBuilder()
                .withDefaultPageSettings("297mm", "210mm");

        assertThat(builder.getDefaultPageHeight()).isEqualTo("297mm");
        assertThat(builder.getDefaultPageWidth()).isEqualTo("210mm");
    }

    // Conf copied from https://github.com/apache/xmlgraphics-fop/blob/fop-2_9/fop/conf/fop.xconf
    private EmbeddedConfigurationBuilder createBuilderFromXml() {
        try (InputStream in = getClass().getResourceAsStream("example_fop.xconf")) {
            return new EmbeddedConfigurationBuilder(in);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String toString(EmbeddedConfigurationBuilder builder) {
        return DefaultConfiguration.toString(builder.getDocument());
    }

}
