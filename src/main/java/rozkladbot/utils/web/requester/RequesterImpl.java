package rozkladbot.utils.web.requester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import static rozkladbot.constants.AppConstants.APPLICATION_JSON;
import static rozkladbot.constants.AppConstants.CONTENT_TYPE;
import static rozkladbot.constants.AppConstants.GROUP_NAME;
import static rozkladbot.constants.AppConstants.HTTP_METHOD_GET;
import static rozkladbot.constants.AppConstants.QUERY_PARAMETER_DELIMITER;
import static rozkladbot.constants.AppConstants.QUERY_PARAMETER_KEY_SEPARATOR;
import static rozkladbot.constants.LoggingConstants.OPENED_CONNECTION_BY_URL;

@Component("requestImpl")
public class RequesterImpl implements Requester {
    private static final Logger logger = LoggerFactory.getLogger(RequesterImpl.class);
    private final String BASE_LINK;
    private final int REQUEST_TIMEOUT;

    public RequesterImpl(@Value("${base.request.link}") String baseLink, @Value("${request.time.out}") int requestTimeout) {
        this.BASE_LINK = baseLink;
        this.REQUEST_TIMEOUT = requestTimeout;
    }

    @Override
    public String makeRequest(Map<String, String> params, String additionalPaths) throws IOException, URISyntaxException {
        URL url = new URI(buildLink(params, additionalPaths)).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        logger.info(OPENED_CONNECTION_BY_URL, url);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setRequestMethod(HTTP_METHOD_GET);
        connection.setConnectTimeout(REQUEST_TIMEOUT * 1000);
        connection.setReadTimeout(REQUEST_TIMEOUT * 1000);
        return readRequest(connection);
    }

    private String buildLink(Map<String, String> params, String additionalPaths) {
        StringBuilder link = new StringBuilder(BASE_LINK);
        if (additionalPaths != null) {
            link.append(additionalPaths);
        }
        for (String key : params.keySet()) {
            if (Objects.equals(GROUP_NAME, key)) {
                continue;
            }
            link.append(QUERY_PARAMETER_DELIMITER).append(key).append(QUERY_PARAMETER_KEY_SEPARATOR).append(params.get(key));
        }
        return link.toString();
    }

    private String readRequest(HttpsURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return content.toString();
    }
}