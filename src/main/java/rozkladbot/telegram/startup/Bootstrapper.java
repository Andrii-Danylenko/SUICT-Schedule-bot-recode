package rozkladbot.telegram.startup;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rozkladbot.constants.LoggingConstants;

import java.util.List;
import rozkladbot.telegram.startup.dataupdater.GenericDataUpdateService;

@Component
@RequiredArgsConstructor
public class Bootstrapper {

  private static final Logger logger = LoggerFactory.getLogger(Bootstrapper.class);
  private final List<GenericDataUpdateService> updateServices;

  @PostConstruct
  public void init() {
    logger.info(LoggingConstants.APPLICATION_INIT_JOB_STARTED);
    for (GenericDataUpdateService service : updateServices) {
      service.updateData();
    }
    logger.info(LoggingConstants.APPLICATION_INIT_JOB_FINISHED);
  }
}
