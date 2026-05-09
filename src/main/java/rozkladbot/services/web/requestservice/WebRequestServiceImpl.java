package rozkladbot.services.web.requestservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.client.RestTemplate;

import static rozkladbot.constants.AppConstants.GROUP_NAME;
import static rozkladbot.constants.AppConstants.QUERY_PARAMETER_DELIMITER;
import static rozkladbot.constants.AppConstants.QUERY_PARAMETER_KEY_SEPARATOR;
import static rozkladbot.constants.LoggingConstants.OPENED_CONNECTION_BY_URL;

@Component
public class WebRequestServiceImpl implements WebRequestService {

  private static final Logger logger = LoggerFactory.getLogger(WebRequestServiceImpl.class);
  private final String BASE_LINK;
  private final RestTemplate restTemplate;
  private final RetryTemplate retryTemplate;

  public WebRequestServiceImpl(
      @Value("${base.request.link}") String baseLink,
      RestTemplate restTemplate,
      RetryTemplate retryTemplate) {
    this.BASE_LINK = baseLink;
    this.restTemplate = restTemplate;
    this.retryTemplate = retryTemplate;
  }

  @Override
  public String makeRequest(Map<String, String> params, String additionalPaths)
      throws URISyntaxException {
    URI uri = buildLink(params, additionalPaths);
    logger.info(OPENED_CONNECTION_BY_URL, uri);
    return retryTemplate.execute(ctx -> restTemplate.getForObject(uri, String.class));
  }

  private URI buildLink(Map<String, String> params, String additionalPaths)
      throws URISyntaxException {
    StringBuilder link = new StringBuilder(BASE_LINK);
    if (additionalPaths != null) {
      link.append(additionalPaths);
    }
    for (String key : params.keySet()) {
      if (Objects.equals(GROUP_NAME, key)) {
        continue;
      }
      link.append(QUERY_PARAMETER_DELIMITER).append(key).append(QUERY_PARAMETER_KEY_SEPARATOR)
          .append(params.get(key));
    }
    return new URI(link.toString());
  }
}