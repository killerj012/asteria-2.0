package com.asteria.util;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.asteria.engine.GameEngine;

/**
 * A thread-safe utility used for timing how long an operation takes. This class
 * is better to use than the {@link Stopwatch} for benchmarking purposes because
 * it contains functionality for benchmarking several different operations
 * separately. It also has functionality for printing off the results of the
 * benchmark to a text file. <b>Use the stopwatch for throttling general
 * gameplay, like food delays. This class is only used for testing purposes!</b><br>
 * <br>
 * An example of usage below:
 * 
 * 
 * <pre>
 * // Create the benchmark.
 * Benchmark b = new Benchmark(&quot;example-benchmark&quot;);
 * 
 * // Loop through the array.
 * for (Object obj : array) {
 * 
 *     // Do something and 'collect' how long it took.
 *     object.doSomething();
 *     b.collect();
 * }
 * 
 * // Print the results of the benchmark to a file.
 * b.print();
 * </pre>
 * 
 * @author lare96
 */
public final class Benchmark {

    // TODO: Sub-benchmarks for the 'collect()' method.

    /** The name of this benchmark. */
    private final String name;

    /** The collector that will collect benchmarks. */
    private final Stopwatch collector = new Stopwatch().reset();

    /** A list of benchmarks collected by the <code>collector</code>. */
    private final List<Long> benchmarks = new LinkedList<>();

    /** A timestamp for when this benchmark was started. */
    private final Date date = new Date();

    /**
     * Create a new {@link Benchmark}.
     * 
     * @param name
     *            the name of this benchmark.
     */
    public Benchmark(String name) {
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Calculates the elapsed time since the last call to <code>collect()</code>
     * or when this benchmark was constructed, and adds it to the list of
     * benchmarks.
     */
    public synchronized void collect() {

        // Add the benchmark and reset the collector.
        benchmarks.add(collector.elapsed());
        collector.reset();
    }

    /**
     * Writes all of the collected data to the designated text file. If the file
     * already exists from a previous benchmark the data will be written to the
     * end of the file.
     */
    public synchronized void print() {

        // Check if we have anything to write.
        if (benchmarks.size() == 0) {
            throw new IllegalStateException("No benchmarks to write!");
        }

        // No use doing this on the game thread, send it to the sequential pool.
        GameEngine.getSequentialPool().execute(new Runnable() {
            @Override
            public void run() {
                try (FileWriter writer = new FileWriter(new File(
                        "./benchmarks/" + name + ".txt"), true)) {

                    // Write all of the data to the beginning of the file.
                    writer.write("[" + name + "] " + date + "\n");
                    writer.write("[" + name + "] Benchmarks:\n[");
                    long collections = 0, total = 0;

                    for (long l : benchmarks) {
                        total += l;
                        collections++;
                        writer.write(Long.toString(l) + "ms");
                        writer.write(", ");
                    }
                    writer.write("]\n");
                    writer.write("[" + name + "] Average: " + (total / collections) + "ms\n");
                    writer.write("[" + name + "] Memory usage: " + (Math
                            .round((Runtime.getRuntime().maxMemory() - Runtime
                                    .getRuntime().freeMemory()) / 1.0 * Math
                                    .pow(10, -6) * 1000.0) / 1000.0) + "mb\n");
                    writer.write("[" + name + "] Processors: " + Runtime
                            .getRuntime().availableProcessors() + "\n\n\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Gets the name of this benchmark.
     * 
     * @return the name of this benchmark.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of collected benchmarks. The returned list is unmodifiable,
     * which means trying to add or remove elements from it will throw an
     * {@link UnsupportedOperationException}.
     * 
     * @return the list of collected benchmarks.
     */
    public List<Long> getBenchmarks() {
        return Collections.unmodifiableList(benchmarks);
    }
}
