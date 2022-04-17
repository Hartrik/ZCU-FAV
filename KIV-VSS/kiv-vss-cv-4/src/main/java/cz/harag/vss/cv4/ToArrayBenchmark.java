package cz.harag.vss.cv4;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 8, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 8, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(1)
public class ToArrayBenchmark {

    public enum TestedCollection {
        ARRAY_LIST(ArrayList::new),
        LINKED_LIST(LinkedList::new),
        VECTOR(Vector::new);

        private Supplier<List<Integer>> supplier;

        TestedCollection(Supplier<List<Integer>> supplier) {
            this.supplier = supplier;
        }
    }

    @Param({"ARRAY_LIST", "LINKED_LIST", "VECTOR"})
    private TestedCollection testedCollection;

    @Param({"100000", "1000000", "10000000"})
    private int collectionSize;

    private List<Integer> collection;

    public static void main(String[] argv) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ToArrayBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void setup() {
        this.collection = createData(testedCollection.supplier);
    }

    private List<Integer> createData(Supplier<List<Integer>> supplier) {
        List<Integer> data = supplier.get();
        Random random = new Random();
        for (int i = 0; i < collectionSize; i++) {
            data.add(random.nextInt());
        }
        return data;
    }

    @Benchmark
    public void toArray(Blackhole bh) {
        Object[] objects = collection.toArray();
        bh.consume(objects);
    }

}