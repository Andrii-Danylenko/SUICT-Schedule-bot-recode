package rozkladbot.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public Caffeine<Object, Object> caffeineConfig() {
    return Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .maximumSize(2000);
  }

  @Bean
  public CacheManager cacheManager(Caffeine<Object, Object>  caffeineConfig) {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("schedules");
    caffeineCacheManager.setCaffeine(caffeineConfig);
    return caffeineCacheManager;
  }
}
