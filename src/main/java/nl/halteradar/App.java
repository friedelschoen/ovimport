package nl.halteradar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import javax.xml.transform.stream.StreamSource;

import nl.halteradar.netex.NeTExTabler;
import nl.halteradar.util.FromFile;
import nl.halteradar.writer.CSVFileWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import nl.bisonnl.netex.PublicationDelivery;

public class App {
    private static void createNetex(Path output, Path workdir, String[] files) throws Exception {
        var ctx = JAXBContext.newInstance(PublicationDelivery.class);

        Arrays.stream(files).parallel()
                .map(f -> {
                    try (var instream = new FileInputStream(f); var zstream = new GZIPInputStream(instream)) {
                        return new FromFile<>(f, ctx.createUnmarshaller().unmarshal(new StreamSource(zstream),
                                PublicationDelivery.class));
                    } catch (Exception e) {
                        throw new IllegalStateException("failed to parse " + f, e);
                    }
                })
                .filter(Objects::nonNull)
                .map(FromFile.mapper(JAXBElement::getValue))
                .flatMap(FromFile.mapperToStream(pub -> pub.getDataObjects() == null
                        ? Stream.empty()
                        : pub.getDataObjects().getCompositeFrame().parallelStream()))
                .flatMap(new NeTExTabler())
                .collect(new TableMerger(output, workdir, CSVFileWriter::new));

    }

    public static void help(int exitcode) {
        System.out.println("ovimport - NeTEx to CSV\n"
                + "\n"
                + "Usage: ovimport [options] [--] INPUTFILES\n"
                + "\n"
                + "Options:\n"
                + " -o, --output FILE .. Writes the zip to FILE. [default: './netex.zip']\n"
                + " -w, --workdir PATH . Uses workdir at PATH. [default: './work']\n"
                + " -h, --help ......... Prints this message and exits.\n");

        System.exit(exitcode);
    }

    public static void main(String[] args) throws Exception {
        Path output = Path.of("./netex.zip");
        Path workdir = Path.of("./work");
        String[] files;

        int i;
        argLoop: for (i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o", "--output":
                    if (i + 1 >= args.length) {
                        System.out.println("--output requires an argument");
                        System.exit(1);
                        return;
                    }
                    output = Path.of(args[++i]);
                    break;

                case "-w", "--workdir":
                    if (i + 1 >= args.length) {
                        System.out.println("--workdir requires an argument");
                        help(1);
                        return;
                    }
                    workdir = Path.of(args[++i]);
                    break;

                case "-h", "--help":
                    help(0);
                    return;

                case "--":
                    i++;
                    break argLoop;

                default:
                    if (args[i].startsWith("-")) {
                        System.out.println("unknown option: " + args[i]);
                        help(1);
                        return;
                    }
                    break argLoop;
            }
        }
        files = Arrays.copyOfRange(args, i, args.length);
        if (files.length == 0) {
            System.err.println("no input files specified");
            help(2);
            return;
        }

        createNetex(output, workdir, files);
    }
}
