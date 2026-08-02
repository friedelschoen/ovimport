package nl.halteradar.util;

import java.util.function.Function;
import java.util.stream.Stream;

final public record FromFile<T>(String filename, T value) {
    public static <F, T> Function<FromFile<F>, FromFile<T>> mapper(Function<F, T> map) {
        return w -> new FromFile<>(w.filename(), map.apply(w.value));
    }

    public static <F, T> Function<FromFile<F>, Stream<FromFile<T>>> mapperToStream(Function<F, Stream<T>> map) {
        return w -> map.apply(w.value).map(v -> new FromFile<>(w.filename(), v));
    }
}
