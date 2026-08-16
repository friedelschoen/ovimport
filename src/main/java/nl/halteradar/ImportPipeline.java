package nl.halteradar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import nl.bisonnl.netex.PublicationDelivery;
import nl.halteradar.chb.CHBTabler;
import nl.halteradar.netex.NeTExTabler;
import nl.halteradar.psa.PSATabler;
import nl.halteradar.table.CSVTable;
import nl.halteradar.table.CSVTableSink;
import nl.halteradar.table.CSVFileTableSink;
import nl.halteradar.table.Table;
import nl.halteradar.table.TableSinkCollector;
import nl.halteradar.table.TableDeduplicator;
import nl.halteradar.table.TableZipper;

final class ImportPipeline {
    private final JAXBContext context;

    private static final QName NETEX_ROOT = new QName("http://www.netex.org.uk/netex", "PublicationDelivery");
    // Some CXX NeTEx exports the default namespace incorrectly as `xmlns:None`
    // Therefore there is no default namespace given.
    private static final QName NETEX_ROOT_NO_NS = new QName("", "PublicationDelivery");

    private static final QName CHB_ROOT = new QName("http://bison.connekt.nl/tmi8/chb/msg", "export");

    private static final QName PSA_ROOT = new QName("", "export");

    ImportPipeline() throws JAXBException {
        context = JAXBContext.newInstance(
                PublicationDelivery.class,
                nl.bisonnl.chb.Export.class,
                nl.bisonnl.psa.Export.class);
    }

    private Stream<Table> toTableStream(String filename) {
        try (
                var input = new FileInputStream(filename);
                var gzip = new GZIPInputStream(input)) {
            XMLStreamReader reader = XMLInputFactory.newFactory()
                    .createXMLStreamReader(gzip);

            try {
                while (reader.hasNext()
                        && reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
                    reader.next();
                }

                if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
                    throw new IllegalArgumentException(
                            "XML document has no root element: "
                                    + filename);
                }

                QName root = reader.getName();

                Unmarshaller unmarshaller = context.createUnmarshaller();

                if (root.equals(NETEX_ROOT) || root.equals(NETEX_ROOT_NO_NS)) {
                    var element = unmarshaller.unmarshal(reader, PublicationDelivery.class);

                    var publication = element.getValue();

                    if (publication.getDataObjects() == null)
                        return Stream.empty();

                    /*
                     * Parallelism is already at file level.
                     */
                    return publication
                            .getDataObjects()
                            .getCompositeFrame()
                            .stream()
                            .flatMap(new NeTExTabler(publication));

                } else if (root.equals(CHB_ROOT)) {
                    var element = unmarshaller.unmarshal(reader, nl.bisonnl.chb.Export.class);

                    var export = element.getValue();

                    return new CHBTabler().apply(export);
                } else if (root.equals(PSA_ROOT)) {
                    var element = unmarshaller.unmarshal(reader, nl.bisonnl.psa.Export.class);

                    var export = element.getValue();

                    return new PSATabler().apply(export);
                } else {
                    throw new IllegalArgumentException(
                            "unsupported XML root " + root
                                    + " in " + filename);
                }
            } finally {
                reader.close();
            }

        } catch (IOException | XMLStreamException | FactoryConfigurationError |

                JAXBException e) {

            throw new IllegalStateException(
                    "failed to parse " + filename, e);
        }
    }

    public void writeTables(
            Path output,
            Path workdir,
            String[] files) throws IOException {

        Files.createDirectories(workdir);

        /*
         * Phase 1:
         *
         * Parse XML files in parallel and merge each logical table
         * into one raw intermediate CSV.
         */
        Collection<CSVFileTableSink> rawTables = Arrays.stream(files)
                .parallel()
                .flatMap(this::toTableStream)
                .collect(
                        new TableSinkCollector<>(
                                table -> {
                                    Path path = workdir.resolve(table.getName() + ".csv");

                                    try {
                                        /*
                                         * skipHeader=true:
                                         * schema is retained by the sink/Table,
                                         * so the intermediate file contains
                                         * rows only.
                                         */
                                        return new CSVFileTableSink(table, path, true);
                                    } catch (IOException e) {
                                        throw new UncheckedIOException(e);
                                    }
                                }));

        /*
         * Phase 2:
         *
         * One table at a time:
         *
         * raw CSV
         * -> CSVTableReader
         * -> deduplicate in memory
         * -> ZIP entry
         */
        Stream<Table> tables = rawTables.stream()
                .map(sink -> new CSVTable(sink.getPath(), sink.getTable().getName(), sink.getTable().getHeader()))
                .map(TableDeduplicator::new);

        new TableZipper(output, CSVTableSink::new).write(tables);
    }
}
