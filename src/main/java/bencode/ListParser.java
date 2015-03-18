package bencode;

import java.util.Optional;

import bencode.handler.ListItemHandler;

public interface ListParser {
    Optional<ListItemHandler> getHandler();
    void setHandler(ListItemHandler handler);
}
