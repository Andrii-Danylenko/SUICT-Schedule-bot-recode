package rozkladbot.telegram.caching;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component("simpleCache")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public abstract class SimpleCache<K, V> {
    protected final Map<K, V> localCache = new HashMap<>();

    public V get(K key) {
        return localCache.get(key);
    }

    public void put(K key, V value) {
        localCache.put(key, value);
    }

    public void remove(K key) {
        localCache.remove(key);
    }

    public void clear() {
        localCache.clear();
    }

    public boolean existsByKey(K key) {
        return localCache.containsKey(key);
    }

    public Set<V> getAll() {
        return new HashSet<>(localCache.values());
    }

    public int size() {
        return localCache.size();
    }
}
