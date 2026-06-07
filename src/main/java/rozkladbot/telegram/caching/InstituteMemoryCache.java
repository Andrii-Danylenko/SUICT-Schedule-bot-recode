package rozkladbot.telegram.caching;

import org.springframework.stereotype.Component;
import rozkladbot.services.ScheduleService;

@Component
public class InstituteMemoryCache extends SimpleMemoryCache<String, ScheduleService> {}
