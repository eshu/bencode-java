package bencode.exception;

public final class UnexpectedCharacterException extends BEncodeException {
    private static final long serialVersionUID = 1L;

    public UnexpectedCharacterException() {
        super();
    }

    public UnexpectedCharacterException(final String message) {
        super(message);
    }

    public UnexpectedCharacterException(final Throwable cause) {
        super(cause);
    }

    public UnexpectedCharacterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
