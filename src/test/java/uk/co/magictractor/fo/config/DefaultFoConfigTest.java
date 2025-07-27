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
package uk.co.magictractor.fo.config;

import org.apache.xmlgraphics.io.Resource;
import org.junit.jupiter.api.Test;

import uk.co.magictractor.fo.config.DefaultFoConfig;
import uk.co.magictractor.fo.config.FoConfig;

public class DefaultFoConfigTest {

    /**
     * With {@code <fo:external-graphic src="blah.png"/>} FOP uses
     * {@code InternalResourceResolver.getResource(String)}.
     */
    @Test
    public void testResourceResolver() throws Exception {
        // Java finds /images/bdc.svg
        // FOP finds images/bdc.svg (no slash)
        String stringUri = "images/bdc.svg";

        FoConfig config = DefaultFoConfig.getInstance();
        Resource resource = config.getFopFactory().getFontManager().getResourceResolver().getResource(stringUri);

        System.out.println(resource);
    }

}
