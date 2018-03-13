import com.fasterxml.jackson.databind.util.LRUMap;
import play.cache.SyncCacheApi;

import javax.inject.Singleton;
import java.util.concurrent.Callable;

/**
 * Fake cache, so we do not use the real EhCache
 * Inspired by https://stackoverflow.com/a/48120583
 * @author Adrien Poupa
 */
@Singleton
public class FakeSyncCacheApi implements SyncCacheApi {

    private LRUMap cache = new LRUMap(0, 50);

    @Override
    public <T> T get(String key) {
        return (T) cache.get(key);
    }

    @Override
    public <T> T getOrElseUpdate(String key, Callable<T> block, int expiration) {
        return getOrElseUpdate(key, block);
    }

    @Override
    public <T> T getOrElseUpdate(String key, Callable<T> block) {
        T value = (T) cache.get(key);
        if (value == null) {
            try {
                value = block.call();
            } catch (Exception e) {

            }
            cache.put(key, value);
        }
        return value;
    }

    @Override
    public void set(String key, Object value, int expiration) {
        cache.put(key, value);
    }

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void remove(String key) {
        cache.clear();
    }
}