package rozkladbot.services.web.requestservice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface WebRequestService {
    String makeRequest(Map<String, String> params, String additionalPaths)
            throws IOException, URISyntaxException, InterruptedException;
}