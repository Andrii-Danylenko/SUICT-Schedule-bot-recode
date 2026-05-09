package rozkladbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.services.web.requestservice.WebRequestServiceImpl;

import java.net.URISyntaxException;
import java.util.HashMap;

@SpringBootTest(classes = {WebRequestServiceImpl.class, RestTemplate.class})
public class WebRequestServiceImplTests {
    @Autowired
    private WebRequestServiceImpl requester;
    private static final HashMap<String, String> params = getParams();


    @Test
    void validRequestTest() throws URISyntaxException {
        String result = requester.makeRequest(params, ApiEndpoints.API_SCHEDULE);
        Assertions.assertFalse(result.isEmpty());
    }

    private static HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("faculty", "1");
        params.put("group", "1634");
        params.put("dateFrom", "03.03.2023");
        params.put("dateTo", "03.03.2023");
        params.put("course", "1");
        return params;
    }
}
