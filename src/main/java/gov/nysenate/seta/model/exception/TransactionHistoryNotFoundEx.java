package gov.nysenate.seta.model.exception;

public class TransactionHistoryNotFoundEx extends Exception {

    public TransactionHistoryNotFoundEx() {
        super();
    }

    public TransactionHistoryNotFoundEx(String message) {
        super(message);
    }

    public TransactionHistoryNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }
}

