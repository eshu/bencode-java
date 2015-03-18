package bencode;

import java.io.IOException;
import java.util.Optional;

import bencode.handler.StreamHandler;

public interface Parser {
    void parse() throws IOException;
    Optional<StreamHandler> getHandler();
    void setHandler(StreamHandler handler);
}
