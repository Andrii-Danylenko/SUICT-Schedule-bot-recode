package rozkladbot.utils.deserializers;

import java.util.List;

public interface JsonDeserializer<T> {
    List<T> deserialize(String json);
}
