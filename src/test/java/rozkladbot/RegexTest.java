package rozkladbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    @Test
    void testRegex() {
        String message = "/sendmessage -all \"Text to capture\"";
        String regex = "\"([^\"]+)\"";
        String quotedText = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            quotedText = matcher.group(1);
        }
        Assertions.assertNotNull(quotedText);
    }
}
