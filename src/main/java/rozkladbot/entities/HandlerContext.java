package rozkladbot.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@RequiredArgsConstructor
public class HandlerContext {
  private final User user;
  private final Update update;
  private final boolean overrideMessage;
}
