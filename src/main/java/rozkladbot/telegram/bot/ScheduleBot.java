package rozkladbot.telegram.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import rozkladbot.telegram.caching.ThreadCache;
import rozkladbot.telegram.router.Router;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component("scheduleBot")
public class ScheduleBot extends AbilityBot {
    private final Router router;
    private final ThreadCache threadCache;

    @Autowired
    public ScheduleBot(
            ThreadCache threadCache,
            Environment environment,
            @Lazy Router router) {
        super(environment.getProperty("telegram.bot.api.key"), environment.getProperty("telegram.bot.name"));
        this.router = router;
        this.threadCache = threadCache;
    }

    @Bean
    public SilentSender silentSender() {
        return silent;
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info("SUICT Schedule Bot")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    long chatId = ctx.chatId();
                    if (threadCache.existsByKey(chatId)) {
                        CompletableFuture<Void> existingFuture = threadCache.get(chatId);
                        if (!existingFuture.isDone()) {
                            return;
                        }
                        threadCache.remove(chatId);
                    }
                    CompletableFuture<Void> newFuture = CompletableFuture.runAsync(() -> router.route(ctx.update(), chatId));
                    threadCache.put(chatId, newFuture);
                    newFuture.whenComplete((result, throwable) -> threadCache.remove(chatId));
                })
                .build();
    }

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> {
            long chatId = getChatId(upd);
            if (threadCache.existsByKey(chatId)) {
                CompletableFuture<Void> existingFuture = threadCache.get(chatId);
                if (!existingFuture.isDone()) {
                    return;
                }
                threadCache.remove(chatId);
            }
            CompletableFuture<Void> newFuture = CompletableFuture.runAsync(() -> router.route(upd, chatId));
            threadCache.put(chatId, newFuture);
            newFuture.whenComplete((result, throwable) -> threadCache.remove(chatId));
        };

        return Reply.of(action, upd -> {
            long chatId = getChatId(upd);
            return router.isUserActive(chatId) && (upd.hasMessage() || upd.hasCallbackQuery());
        });
    }

    @Override
    public long creatorId() {
        return 1;
    }
}
