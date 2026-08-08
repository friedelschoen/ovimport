package nl.halteradar.table;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class TableSinkCollector<T extends TableSink<T, R>, R> implements Collector<Table, Map<Table, T>, Stream<R>> {

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
    public Function<Map<Table, T>, Stream<R>> finisher() {
        return sinks -> sinks.values()
                .stream()
                .map(TableSink::finish);
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(
                Characteristics.CONCURRENT,
                Characteristics.UNORDERED);
    }
}
