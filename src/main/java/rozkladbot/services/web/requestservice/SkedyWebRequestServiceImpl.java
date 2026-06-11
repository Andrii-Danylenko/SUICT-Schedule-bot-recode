package rozkladbot.services.web.requestservice;

import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static rozkladbot.constants.AppConstants.GROUP_ID;
import static rozkladbot.constants.AppConstants.GROUP_NAME;
import static rozkladbot.constants.AppConstants.QUERY_END_DATE;
import static rozkladbot.constants.AppConstants.QUERY_START_DATE;
import static rozkladbot.constants.LoggingConstants.OPENED_CONNECTION_BY_URL;

@Component
public class SkedyWebRequestServiceImpl extends AbstractWebRequestService {

  private static final Set<String> PARAMETERS_TO_SKIP = Set.of(GROUP_NAME, GROUP_ID,
      QUERY_START_DATE, QUERY_END_DATE);

  private static final Logger logger = LoggerFactory.getLogger(SkedyWebRequestServiceImpl.class);
  private final String BASE_LINK;

  public SkedyWebRequestServiceImpl(
      @Value("${base.request.link}") String baseLink,
      RestTemplate restTemplate,
      RetryTemplate retryTemplate) {
    super(restTemplate, retryTemplate);
    this.BASE_LINK = baseLink;
  }

  @Override
  public String makeRequest(Map<String, String> params, String additionalPaths) {
    URI uri = buildLink(params, additionalPaths);
    logger.info(OPENED_CONNECTION_BY_URL, uri);
    return retryTemplate.execute(ctx -> restTemplate.getForObject(uri, String.class));
  }

  private URI buildLink(Map<String, String> params, String additionalPaths) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_LINK);
    if (additionalPaths != null) {
      builder.path(additionalPaths);
    }
    for (Entry<String, String> entry : params.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (PARAMETERS_TO_SKIP.contains(key)) {
        continue;
      }
      builder.queryParam(key, value);
    }
    return builder.build().encode().toUri();
  }
}