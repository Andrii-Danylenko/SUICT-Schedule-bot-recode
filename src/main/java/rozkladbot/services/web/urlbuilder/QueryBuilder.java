package rozkladbot.services.web.urlbuilder;

import java.time.LocalDate;
import java.util.HashMap;

public interface QueryBuilder {

  HashMap<String, String> buildQueryParams(
      long groupOfficialId,
      long groupId,
      long course,
      String groupName,
      long facultyId,
      long institutionId,
      LocalDate startDate,
      LocalDate endDate);

  HashMap<String, String> buildFromValues(String... values);
}
