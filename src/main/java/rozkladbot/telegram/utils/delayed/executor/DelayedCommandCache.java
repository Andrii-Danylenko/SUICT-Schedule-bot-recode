package rozkladbot.telegram.utils.delayed.executor;

import org.springframework.stereotype.Component;
import rozkladbot.entities.DelayedCommand;
import rozkladbot.telegram.caching.SimpleCache;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component("delayedCommandCache")
public class DelayedCommandCache extends SimpleCache<Long, Set<DelayedCommand>> {
    public Map<Long, Set<DelayedCommand>> getAll() {
        return this.localCache;
    }

    public void add(long chatId, DelayedCommand delayedCommand) {
        if (this.localCache.containsKey(chatId)) {
            for (DelayedCommand command : this.localCache.get(chatId)) {
                if (Objects.equals(command, delayedCommand)) {
                    command.decrementAndSet();
                    return;
                }
            }
        } else {
            this.localCache.put(chatId, new HashSet<>() {
                {
                    add(delayedCommand);
                }
            });
        }
    }

    public void decrementCommandTTL(long id, DelayedCommand delayedCommand) {
        Set<DelayedCommand> commands = this.get(id);
        for (DelayedCommand command : commands) {
            if (Objects.equals(command, delayedCommand)) {
                if (command.getTTL() <= 0) {
                    this.remove(id);
                    return;
                }
                command.decrementAndSet();
            }
        }
    }
}
