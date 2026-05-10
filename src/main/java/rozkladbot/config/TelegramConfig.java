package rozkladbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.sender.SilentSender;

@Configuration
public class TelegramConfig {

  @Bean
  public SilentSender silentSender(AbilityBot abilityBot) {
    return abilityBot.silent();
  }
}
