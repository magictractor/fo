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
package uk.co.magictractor.fo.performance;

public class SingleRunTiming {

    private final String id;
    private final long startNanos;
    private final long endNanos;

    public SingleRunTiming(String id, long startNanos, long endNanos) {
        this.id = id;
        this.startNanos = startNanos;
        this.endNanos = endNanos;
    }

    public String getId() {
        return id;
    }

    public long getNanoseconds() {
        return endNanos - startNanos;
    }

    public int getMicroseconds() {
        return (int) ((500 + endNanos - startNanos) / 1000);
    }

    public int getMilliseconds() {
        return (int) ((500000 + endNanos - startNanos) / 1000000);
    }

}
