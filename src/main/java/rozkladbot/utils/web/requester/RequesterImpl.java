package rozkladbot.utils.web.requester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.exceptions.EmptyRequestParametersException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

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
    public String makeRequest(Map<String, String> params) throws IOException, URISyntaxException, InterruptedException {
        if (params.isEmpty()) {
            logger.error(ErrorConstants.PARAMS_ARE_EMPTY);
            throw new EmptyRequestParametersException(ErrorConstants.PARAMS_ARE_EMPTY);
        }
        String requestLink = buildLink(params);
        URL url = new URI(requestLink).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        return readRequest(connection);
    }

    private String buildLink(Map<String, String> params) {
        StringBuilder link = new StringBuilder(BASE_LINK);
        for (String key : params.keySet()) {
            link.append("&").append(key).append("=").append(params.get(key));
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