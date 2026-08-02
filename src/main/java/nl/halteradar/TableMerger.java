package nl.halteradar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.halteradar.writer.TableSink;

public final class TableMerger implements Collector<Table, Map<String, TableSink>, Path> {
    private final Path output;
    private final Path workdir;
    private final BiFunction<Table, Path, TableSink> makeWriter;

    public TableMerger(Path output, Path workdir, BiFunction<Table, Path, TableSink> makeWriter) throws IOException {
        this.output = output;
        this.makeWriter = makeWriter;
        this.workdir = workdir;

        workdir.toFile().mkdirs();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
    }

    @Override
    public Supplier<Map<String, TableSink>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<String, TableSink>, Table> accumulator() {
        return (writers, table) -> {
            TableSink writer;

            synchronized (writers) {
                writer = writers.get(table.name);

                if (writer == null) {
                    writer = makeWriter.apply(table, workdir);
                    writers.put(table.name, writer);
                }
            }

            try {
                writer.accept(table);
            } catch (IOException err) {
                throw new UncheckedIOException(err);
            }
        };
    }

    @Override
    public BinaryOperator<Map<String, TableSink>> combiner() {
        return (left, right) -> {
            left.putAll(right);
            return left;
        };
    }

    @Override
    public Function<Map<String, TableSink>, Path> finisher() {
        return writers -> {
            try (
                    var outputFile = Files.newOutputStream(output);
                    var buffered = new BufferedOutputStream(outputFile);
                    var zip = new ZipOutputStream(buffered)) {

                // Eerst alle bestanden volledig flushen en sluiten.
                for (var entry : writers.entrySet()) {
                    Path source = null;

                    try {
                        source = entry.getValue().finish();

                    } catch (IOException e) {
                        throw new IllegalStateException("Unable to close table writer", e);
                    }

                    if (source == null)
                        continue;

                    if (!Files.isRegularFile(source)) {
                        System.err.printf(
                                "skipping missing output file: %s%n",
                                source);
                        continue;
                    }

                    System.err.printf("adding %s to %s\n", source, output);

                    var zentry = new ZipEntry(entry.getKey() + ".txt");
                    zip.putNextEntry(zentry);

                    try (var input = Files.newInputStream(source)) {
                        input.transferTo(zip);
                    }

                    zip.closeEntry();
                }

                zip.finish();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create ZIP archive", e);
            }
            return output;
        };
    }
}
