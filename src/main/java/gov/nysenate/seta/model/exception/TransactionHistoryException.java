package gov.nysenate.seta.model.exception;

public class TransactionHistoryException extends Exception {
    public TransactionHistoryException() {
        super();
    }

    public TransactionHistoryException(String message) {
        super(message);
    }

    public TransactionHistoryException(String message, Throwable cause) {
        super(message, cause);
    }
}