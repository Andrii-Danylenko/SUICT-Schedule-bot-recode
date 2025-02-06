package rozkladbot.telegram.caching;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component("threadCache")
public class ThreadCache extends SimpleCache<Long, CompletableFuture<Void>> {
    @Override
    public void remove(Long key) {
        localCache.get(key).cancel(true);
        localCache.remove(key);
    }
}
