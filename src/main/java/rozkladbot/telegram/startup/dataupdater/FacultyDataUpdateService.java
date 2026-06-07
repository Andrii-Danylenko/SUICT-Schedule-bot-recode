package rozkladbot.telegram.startup.dataupdater;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import rozkladbot.constants.ApiEndpoints;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Institute;
import rozkladbot.entities.json.response.FacultyJsonResponse;
import rozkladbot.json.deserializers.JsonDeserializer;
import rozkladbot.services.FacultyService;
import rozkladbot.services.InstituteService;
import rozkladbot.services.web.requestservice.WebRequestService;
import rozkladbot.services.web.urlbuilder.QueryBuilder;

@Service
@Order(3)
@Slf4j
@ConditionalOnProperty(name = "bootstrapper.update.university-data", havingValue = "true")
public class FacultyDataUpdateService extends GenericDataUpdateService {

  private final InstituteService instituteService;
  private final FacultyService facultyService;
  private final JsonDeserializer<FacultyJsonResponse> facultyJsonDeserializer;

  public FacultyDataUpdateService(
      WebRequestService webRequestService,
      QueryBuilder queryBuilder,
      InstituteService instituteService,
      FacultyService facultyService,
      JsonDeserializer<FacultyJsonResponse> facultyJsonDeserializer) {
    super(webRequestService, queryBuilder);
    this.instituteService = instituteService;
    this.facultyService = facultyService;
    this.facultyJsonDeserializer = facultyJsonDeserializer;
  }


  @Override
  public void updateData() {
    try {
      List<Institute> institutes = instituteService.getAll();
      for (Institute inst : institutes) {
        List<Faculty> faculties = facultyJsonDeserializer.deserialize(
            webRequestService.makeRequest(
                queryBuilder.buildFromValues(),
                ApiEndpoints.API_FACULTIES.formatted(inst.getId())
            )
        ).stream().map(facultyJsonResponse -> {
              Faculty faculty = Faculty.toFacultyFromJsonResponse(facultyJsonResponse);
              faculty.setInstitute(inst);
              return faculty;
            }
        ).collect(Collectors.toCollection(ArrayList::new));
        facultyService.saveAll(faculties);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
