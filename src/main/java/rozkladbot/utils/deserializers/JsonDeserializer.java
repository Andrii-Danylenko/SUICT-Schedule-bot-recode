package rozkladbot.utils.deserializers;

import java.util.Deque;

public interface JsonDeserializer<T> {
    Deque<T> deserialize(String json);
}
