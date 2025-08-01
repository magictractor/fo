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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import uk.co.magictractor.fo.DocIO;
import uk.co.magictractor.fo.FoDocument;

/**
 *
 */
@FunctionalInterface
public interface FoTransformOutputStreamFunction {

    OutputStream newOutputStream(FoTransform foTransform, FoDocument foDocument);

    public static FoTransformOutputStreamFunction forOutputStream(OutputStream out) {
        return (t, d) -> out;
    }

    public static FoTransformOutputStreamFunction forDocIO(DocIO docIO) {
        return (t, d) -> {
            try {
                return docIO.newOutputStream(t.fileExtension());
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

}
