package rozkladbot.web.requester;

import java.util.HashMap;

public interface Requester {
    String makeRequest(HashMap<String, String> params);
}