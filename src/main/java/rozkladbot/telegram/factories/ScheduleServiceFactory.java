package rozkladbot.telegram.factories;

import java.util.List;
import org.springframework.stereotype.Component;
import rozkladbot.enums.ScheduleServiceType;
import rozkladbot.services.ScheduleService;

@Component
public class ScheduleServiceFactory {

  private final List<ScheduleService> scheduleServices;

  public ScheduleServiceFactory(
      List<ScheduleService> services) {
    this.scheduleServices = services;
  }

  public ScheduleService getScheduleService(ScheduleServiceType scheduleServiceType) {
    return scheduleServices.stream().filter(service -> service.supports(
        scheduleServiceType)).findFirst().orElseThrow(UnsupportedOperationException::new);
  }
}
