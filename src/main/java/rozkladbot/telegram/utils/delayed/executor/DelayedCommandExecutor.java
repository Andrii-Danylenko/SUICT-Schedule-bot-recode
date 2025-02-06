package rozkladbot.telegram.utils.delayed.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import rozkladbot.entities.DelayedCommand;
import rozkladbot.entities.User;
import rozkladbot.services.ScheduleService;
import rozkladbot.telegram.caching.UserCache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component("delayedCommandExecutor")
public class DelayedCommandExecutor {
    private final DelayedCommandCache delayedCommandCache;
    private final ScheduleService scheduleService;
    private final UserCache userCache;

    @Autowired
    public DelayedCommandExecutor(
            DelayedCommandCache delayedCommandCache,
            ScheduleService scheduleService,
            UserCache userCache) {
        this.delayedCommandCache = delayedCommandCache;
        this.scheduleService = scheduleService;
        this.userCache = userCache;
    }

    // TODO: Ну было бы неплохо это доделать
 // @Scheduled(cron = "0 */5 * * * *")
    @Async
    public void executeDelayedCommands() {
        Map<Long, Set<DelayedCommand>> delayedCommands = delayedCommandCache.getAll();
        for (Map.Entry<Long, Set<DelayedCommand>> entry : delayedCommands.entrySet()) {
            long chatId = entry.getKey();
            for (DelayedCommand delayedCommand : entry.getValue()) {
                User user = userCache.get(chatId);
                try {
                    switch (delayedCommand.getCommand()) {
                        case GET_TODAY_SCHEDULE -> scheduleService.getTodayLessons(user);
                        case GET_TOMORROW_SCHEDULE -> scheduleService.getTomorrowLessons(user);
                        case GET_THIS_WEEK_SCHEDULE -> scheduleService.getWeeklyLessons(user);
                        case GET_NEXT_WEEK_SCHEDULE -> scheduleService.getNextWeekLessons(user);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    delayedCommandCache.decrementCommandTTL(chatId, delayedCommand);
                }
            }
        }
    }
}
