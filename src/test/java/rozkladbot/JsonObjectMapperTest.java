package rozkladbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.entities.Lesson;
import rozkladbot.utils.deserializers.LessonDeserializer;
import rozkladbot.utils.web.requester.RequesterImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Deque;
import java.util.HashMap;

@SpringBootTest
public class JsonObjectMapperTest {
    @Autowired
    private LessonDeserializer lessonDeserializer;
    @Autowired
    private RequesterImpl requester;
    private static final HashMap<String, String> params = getParams();

    @Test
    public void testJsonObjectMapper() throws IOException, URISyntaxException, InterruptedException {
        String json = requester.makeRequest(params, ApiEndpoints.API_SCHEDULE);
        Deque<Lesson> lessons = lessonDeserializer.deserialize(json);
        Assertions.assertFalse(lessons.isEmpty());
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
