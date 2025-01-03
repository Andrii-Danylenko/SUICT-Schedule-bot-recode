package rozkladbot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.abilitybots.api.bot.AbilityBot;
import rozkladbot.telegram.handlers.RegistrationHandler;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RegistrationHandlerTest {
    @Autowired
    private RegistrationHandler registrationHandler;
    @Autowired
    private AbilityBot abilityBot;

    @Test
    void registrationHandlerTest() {

    }
}
