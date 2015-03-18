package bencode;

import java.util.Optional;

import bencode.handler.DictionaryEntryHandler;

public interface DictionaryParser {
    Optional<DictionaryEntryHandler> getHandler();
    void setHandler(DictionaryEntryHandler handler);
}
