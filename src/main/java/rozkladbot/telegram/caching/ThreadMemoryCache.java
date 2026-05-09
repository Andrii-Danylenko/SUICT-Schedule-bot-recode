package rozkladbot.telegram.caching;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component("threadCache")
public class ThreadMemoryCache extends SimpleMemoryCache<Long, CompletableFuture<Void>> {
    @Override
    public void remove(Long key) {
        localCache.get(key).cancel(true);
        localCache.remove(key);
    }
}
