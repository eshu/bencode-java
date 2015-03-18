package bencode.exception;

import java.io.IOException;

public class BEncodeException extends IOException {
    private static final long serialVersionUID = 1L;

    BEncodeException() {
        super();
    }

    BEncodeException(final String message) {
        super(message);
    }

    BEncodeException(final Throwable cause) {
        super(cause);
    }

    BEncodeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
