package bencode.exception;

public final class IntegerFormatException extends BEncodeException {
    private static final long serialVersionUID = 1L;

    public IntegerFormatException() {
        super();
    }

    public IntegerFormatException(final String message) {
        super(message);
    }

    public IntegerFormatException(final Throwable cause) {
        super(cause);
    }

    public IntegerFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
