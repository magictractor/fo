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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.common.base.Strings;

import org.apache.commons.io.output.NullPrintStream;

// https://www.reddit.com/r/javahelp/comments/1cuj9dn/best_or_popular_ways_to_benchmark_java_code/
public abstract class PerformanceBenchmark<PARAMS, RESULT> {

    private final List<ActionInfo> actions = new ArrayList<>();
    private int maxActionIdLength = 0;

    private int iterations = 6;

    protected PerformanceBenchmark() {
        addAction("NoOp", this::noOp);
    }

    public void addAction(String actionId, Function<PARAMS, RESULT> action) {
        actions.add(new ActionInfo(actionId, action));
        if (actionId.length() > maxActionIdLength) {
            maxActionIdLength = actionId.length();
        }
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void warmUp(PARAMS params) {
        for (ActionInfo actionInfo : actions) {
            actionInfo.action.apply(params);
        }
    }

    public void runBenchmarks() {
        int runs = iterations * actions.size();
        List<SingleRunTiming> timings = new ArrayList<>(runs);

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        // TODO! remove dependency on Apache for NullPrintStream
        // TODO! try/finally
        System.setOut(NullPrintStream.INSTANCE);
        System.setErr(NullPrintStream.INSTANCE);

        for (int i = 0; i < iterations; i++) {
            for (ActionInfo actionInfo : actions) {
                Function<PARAMS, RESULT> action = actionInfo.action;
                long startTime = System.nanoTime();
                singleRun(action);
                long endTime = System.nanoTime();
                // int elapsed = (int) ((500000 + endTime - startTime) / 1000000);
                SingleRunTiming timing = new SingleRunTiming(actionInfo.id, startTime, endTime);
                timings.add(timing);
            }
        }

        System.setOut(originalOut);
        System.setErr(originalErr);

        for (SingleRunTiming timing : timings) {
            System.out.println(Strings.padEnd(timing.getId(), maxActionIdLength + 2, ' ') + timing.getMilliseconds());
        }
    }

    public abstract void singleRun(Function<PARAMS, RESULT> action);

    /**
     * Does nothing. A run of {@code noOp()} calls may be timed in order to
     * adjust timings of real tests to remove times for loops and calls to the
     * consumer.
     */
    private RESULT noOp(PARAMS params) {
        // Do nothing.
        return null;
    }

    public class ActionInfo {
        private final String id;
        private final Function<PARAMS, RESULT> action;

        public ActionInfo(String id, Function<PARAMS, RESULT> action) {
            this.id = id;
            this.action = action;
        }
    }

}
