package rozkladbot.telegram.caching;

import java.util.function.BiFunction;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component("simpleCache")
public abstract class SimpleMemoryCache<K, V> {

  protected final ConcurrentHashMap<K, V> localCache = new ConcurrentHashMap<>();

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

  public Set<V> getAllValues() {
    return new HashSet<>(localCache.values());
  }

  public int size() {
    return localCache.size();
  }

  public V compute(
      K key,
      BiFunction<K, V, V> remappingFunction) {
    return localCache.compute(key, remappingFunction);
  }

  public boolean removeIfSame(K key, V expectedValue) {
    return localCache.remove(key, expectedValue);
  }
}
