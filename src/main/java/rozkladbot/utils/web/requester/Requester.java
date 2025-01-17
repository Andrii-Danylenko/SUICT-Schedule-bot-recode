package rozkladbot.utils.web.requester;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface Requester {
    String makeRequest(Map<String, String> params) throws IOException, URISyntaxException, InterruptedException;
}