package rozkladbot.config;

import static rozkladbot.constants.AppConstants.APPLICATION_JSON;
import static rozkladbot.constants.AppConstants.CONTENT_TYPE;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder,
      @Value("${request.time.out}") long requestTimeout) {
    builder.readTimeout(Duration.ofSeconds(requestTimeout));
    builder.connectTimeout(Duration.ofSeconds(requestTimeout));
    builder.defaultHeader(CONTENT_TYPE, APPLICATION_JSON);
    return builder.build();
  }

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();
    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(1000);
    SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
    simpleRetryPolicy.setMaxAttempts(3);
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
    retryTemplate.setRetryPolicy(simpleRetryPolicy);
    return retryTemplate;
  }
}
