package nl.halteradar.table;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class TableSinkCollector<T extends TableSink<T>> implements Collector<Table, Map<Table, T>, Collection<T>> {
    private final Function<Table, T> creator;

    public TableSinkCollector(Function<Table, T> creator) {
        this.creator = creator;
    }

    @Override
    public Supplier<Map<Table, T>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Table, T>, Table> accumulator() {
        return (sinks, table) -> {
            final T sink;

            synchronized (sinks) {
                sink = sinks.computeIfAbsent(table, creator);
            }

            try {
                sink.accept(table.rows());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    @Override
    public BinaryOperator<Map<Table, T>> combiner() {
        return (left, right) -> {
            right.forEach((table, sink) -> left.merge(table, sink, TableSink::combine));

            return left;
        };
    }

    @Override
    public Function<Map<Table, T>, Collection<T>> finisher() {
        return sinks -> {
            for (T sink : sinks.values())
                try {
                    sink.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            return sinks.values();
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(
                Characteristics.CONCURRENT,
                Characteristics.UNORDERED);
    }
}
