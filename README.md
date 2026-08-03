netex2csv
============================================================

`netex2csv` converts Dutch NeTEx publications into a flat CSV representation suitable for bulk loading into PostgreSQL
or other relational databases.

The converter preserves the original NeTEx object model where practical and writes one CSV file per table. The resulting
archive is intended as an intermediate format for further processing, not as a public data model.

Building
------------------------------------------------------------

```sh
mvn package
```

Usage
------------------------------------------------------------

```sh
java -jar target/netex2csv.jar [options] INPUT...
```

Example:

```sh
java -jar target/netex2csv.jar \
    --output netex.zip \
    NeTEx_QBUZZ_*.xml.gz
```

### Options

```
-o, --output FILE    Write the generated ZIP archive.
-w, --workdir PATH   Directory for temporary CSV files.
-h, --help           Show this help message.
```

Output
------------------------------------------------------------

The generated ZIP archive contains one CSV file for each exported table.

License
------------------------------------------------------------

The converter source code is licensed under the zlib License.

Bundled NeTEx XSD files are third-party material and remain licensed under their original license. See `LICENSE` for
details.
