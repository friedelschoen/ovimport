package nl.halteradar.ovimport.table;

public class ApplesOrangesException extends RuntimeException {
    public ApplesOrangesException() {
    }

    public ApplesOrangesException(String message) {
        super(message);
    }

    public ApplesOrangesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplesOrangesException(Throwable cause) {
        super(cause);
    }
}
