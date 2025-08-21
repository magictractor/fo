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
package uk.co.magictractor.fo;

/**
 *
 */
public final class FoTemplates {

    private FoTemplates() {
    }

    private static FoTemplate template;

    public static FoTemplate getTemplate() {
        if (template == null) {
            template = buildTemplate();
        }
        return template;
    }

    private static FoTemplate buildTemplate() {
        return new FoDocumentBuilder("template.fo")
                .withVariableSubstitution("footer.left", "")
                .withVariableSubstitution("footer.middle", "")
                // TODO! would be nice to have footer.right and substitute in the page number
                .buildTemplate();
    }

}
