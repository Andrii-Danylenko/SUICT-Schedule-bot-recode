package rozkladbot.utils.web.requester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.exceptions.EmptyRequestParametersException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

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
    public String makeRequest(HashMap<String, String> params) throws IOException, URISyntaxException {
        if (params.isEmpty()) {
            logger.error(ErrorConstants.PARAMS_ARE_EMPTY);
            throw new EmptyRequestParametersException(ErrorConstants.PARAMS_ARE_EMPTY);
        }
        String requestLink = buildLink(params);
        return makeRequest(requestLink);
    }

    private String buildLink(HashMap<String, String> params) {
        StringBuilder link = new StringBuilder(BASE_LINK);
        for (String key : params.keySet()) {
            link.append("&").append(key).append("=").append(params.get(key));
        }
        return link.toString();
    }

    private String makeRequest(String requestLink) throws IOException, URISyntaxException {
        HttpURLConnection connection = buildConnection(requestLink);
        return readResponse(connection);
    }

    private HttpURLConnection buildConnection(String requestLink) throws IOException, URISyntaxException {
        URL url = new URI(requestLink).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(REQUEST_TIMEOUT * 1000);
        connection.setReadTimeout(REQUEST_TIMEOUT * 1000);
        return connection;
    }

    // This shit is fast AF. I love buffers
    private String readResponse(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }
        return result.toString();
    }
}