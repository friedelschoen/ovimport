package nl.halteradar;

import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Arrays;

import com.opencsv.CSVWriter;

public class App {
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

        new ImportPipeline().writeTables(output, workdir, files);
    }
}
