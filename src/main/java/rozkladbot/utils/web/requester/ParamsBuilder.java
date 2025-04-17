package rozkladbot.utils.web.requester;

import java.time.LocalDate;
import java.util.HashMap;

public interface ParamsBuilder {

    HashMap<String, String> createParams(
            long groupId,
            long course,
            String groupName,
            long facultyId,
            long institutionId,
            LocalDate startDate,
            LocalDate endDate);

    HashMap<String, String> buildFromValues(String... values);
}
