package rozkladbot.telegram.startup.dataupdater;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.entities.Institute;
import rozkladbot.entities.json.response.InstituteJsonResponse;
import rozkladbot.json.deserializers.JsonDeserializer;
import rozkladbot.services.InstituteService;
import rozkladbot.services.web.requestservice.WebRequestService;
import rozkladbot.services.web.urlbuilder.QueryBuilder;

@Service
@Order(2)
@Slf4j
@ConditionalOnProperty(name = "bootstrapper.update.university-data", havingValue = "true")
public class UniversityDataUpdateService extends GenericDataUpdateService {

  private final JsonDeserializer<InstituteJsonResponse> instituteJsonResponseDeserializer;
  private final InstituteService instituteService;

  public UniversityDataUpdateService(
      WebRequestService webRequestService,
      QueryBuilder queryBuilder,
      JsonDeserializer<InstituteJsonResponse> instituteJsonResponseDeserializer,
      InstituteService instituteService) {
    super(webRequestService, queryBuilder);
    this.instituteJsonResponseDeserializer = instituteJsonResponseDeserializer;
    this.instituteService = instituteService;
  }


  @Override
  public void updateData() {
    try {
      List<Institute> institutes = instituteJsonResponseDeserializer.deserialize(
          webRequestService.makeRequest(
              queryBuilder.buildFromValues(),
              ApiEndpoints.API_INSTITUTIONS
          )
      ).stream().map(Institute::toInstituteFromJsonResponse).collect(
          Collectors.toCollection(LinkedList::new));
      instituteService.saveAll(institutes);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
