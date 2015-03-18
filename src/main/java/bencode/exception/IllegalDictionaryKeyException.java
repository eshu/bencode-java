package bencode.exception;

public final class IllegalDictionaryKeyException extends BEncodeException {
    private static final long serialVersionUID = 1L;

    public IllegalDictionaryKeyException() {
        super();
    }

    public IllegalDictionaryKeyException(final String message) {
        super(message);
    }

    public IllegalDictionaryKeyException(final Throwable cause) {
        super(cause);
    }

    public IllegalDictionaryKeyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
