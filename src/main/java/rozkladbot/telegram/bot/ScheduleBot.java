package rozkladbot.telegram.bot;

import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.telegram.caching.ThreadMemoryCache;
import rozkladbot.telegram.caching.UserMemoryCache;
import rozkladbot.telegram.router.Router;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component("scheduleBot")
public class ScheduleBot extends AbilityBot {

  private final Router router;
  private final ThreadMemoryCache threadCache;
  private final UserMemoryCache userCache;
  private final ExecutorService telegramBotExecutor;

  @Autowired
  public ScheduleBot(
      ThreadMemoryCache threadCache,
      Environment environment,
      UserMemoryCache userCache,
      @Lazy Router router,
      ExecutorService telegramBotExecutor) {
    super(environment.getProperty("telegram.bot.api.key"),
        environment.getProperty("telegram.bot.name"));
    this.router = router;
    this.threadCache = threadCache;
    this.userCache = userCache;
    this.telegramBotExecutor = telegramBotExecutor;
  }

  public Ability startBot() {
    return Ability
        .builder()
        .name("start")
        .info("ScheduleBot")
        .locality(ALL)
        .privacy(PUBLIC)
        .action(ctx -> {
          long chatId = ctx.chatId();
          CompletableFuture<?> existing = threadCache.get(chatId);
          if (existing != null && !existing.isDone()) {
            return;
          }
          threadCache.put(chatId, CompletableFuture.completedFuture(null));
          telegramBotExecutor.submit(() -> {
            try {
              router.route(ctx.update(), chatId);
            } finally {
              threadCache.remove(chatId);
            }
          });
        })
        .build();
  }

  public Reply replyToButtons() {

    BiConsumer<BaseAbilityBot, Update> action = (bot, upd) -> {
      long chatId = getChatId(upd);
      CompletableFuture<?> existing = threadCache.get(chatId);
      if (existing != null && !existing.isDone()) {
        return;
      }
      threadCache.put(chatId, CompletableFuture.completedFuture(null));
      telegramBotExecutor.submit(() -> {
        try {
          router.route(upd, chatId);
        } finally {
          threadCache.remove(chatId);
        }
      });
    };
    return Reply.of(
        action,
        upd -> {
          long chatId = getChatId(upd);
          return userCache.existsByKey(chatId)
              && (upd.hasMessage() || upd.hasCallbackQuery());
        }
    );
  }

  @Override
  public long creatorId() {
    return 1;
  }
}
