package bencode.handler.impl;

public final class SimpleParserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SimpleParserException() {
        super();
    }

    public SimpleParserException(final String message) {
        super(message);
    }

    public SimpleParserException(final Throwable cause) {
        super(cause);
    }

    public SimpleParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
