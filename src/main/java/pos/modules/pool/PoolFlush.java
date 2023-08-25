package pos.modules.pool;

public interface PoolFlush<T> {
    void flush(int ID, T object);
}
