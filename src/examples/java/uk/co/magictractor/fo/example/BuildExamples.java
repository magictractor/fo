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
package uk.co.magictractor.fo.example;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.stream.Stream;

public class BuildExamples {

    private void buildAll() {
        String thisClassName = getClass().getSimpleName();
        String thisPackage = getClass().getPackage().getName();

        URL url = getClass().getResource(".");
        File dir = new File(url.getFile());
        Stream.of(dir.list())
                .map(file -> file.replace(".class", ""))
                .filter(exampleClassName -> !thisClassName.equals(exampleClassName))
                .map(className -> thisPackage + "." + className)
                .map(this::exampleClass)
                .forEach(this::runMain);
    }

    private Class<?> exampleClass(String exampleClassName) {
        try {
            return Class.forName(exampleClassName);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private void runMain(Class<?> exampleClass) {
        try {
            runMain0(exampleClass);
        }
        catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private void runMain0(Class<?> exampleClass)
            throws ReflectiveOperationException, SecurityException {
        Method mainMethod = exampleClass.getDeclaredMethod("main", String[].class);
        Object arg = new String[0];
        mainMethod.invoke(null, arg);
    }

    public static void main(String[] args) {
        new BuildExamples().buildAll();
    }

}
