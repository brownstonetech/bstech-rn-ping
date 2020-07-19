package ca.bstech.ping;

public interface Consumer<T> {
    void accept(T result);
}
