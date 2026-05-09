package rozkladbot.json.deserializers;

import java.util.Deque;

public interface JsonDeserializer<T> {
    Deque<T> deserialize(String json);
}
