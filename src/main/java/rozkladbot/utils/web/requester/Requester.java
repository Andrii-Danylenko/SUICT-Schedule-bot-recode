package rozkladbot.utils.web.requester;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public interface Requester {
    String makeRequest(HashMap<String, String> params) throws IOException, URISyntaxException;
}