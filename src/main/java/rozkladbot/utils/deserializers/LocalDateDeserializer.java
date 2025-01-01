package rozkladbot.utils.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;
import rozkladbot.utils.date.DateUtils;

import java.io.IOException;
import java.time.LocalDate;

@Component("localDateDeserializer")
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return LocalDate.parse(jsonParser.getText(), DateUtils.JSON_DATE_FORMATTER);
    }
}
