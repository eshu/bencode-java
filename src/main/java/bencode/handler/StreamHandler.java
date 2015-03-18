package bencode.handler;

public interface StreamHandler extends ListItemHandler {
    default void start() {}
    default void end() {}
}
