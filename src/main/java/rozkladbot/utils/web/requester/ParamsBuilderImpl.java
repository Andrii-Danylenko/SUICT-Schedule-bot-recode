package rozkladbot.utils.web.requester;

import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.utils.date.DateUtils;

import java.time.LocalDate;
import java.util.HashMap;

@Component
public class ParamsBuilderImpl implements ParamsBuilder {

    public HashMap<String, String> createParams(
            long groupId,
            long course,
            long facultyId,
            long institutionId,
            LocalDate startDate,
            LocalDate endDate) {

        return buildFromValues(
                "group", String.valueOf(groupId),
                "course", String.valueOf(course),
                "faculty", String.valueOf(facultyId),
                "institution", String.valueOf(institutionId),
                "dateFrom", DateUtils.getDateAsString(startDate),
                "dateTo", DateUtils.getDateAsString(endDate)
        );
    }

    @Override
    public HashMap<String, String> buildFromValues(String... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException(ErrorConstants.WRONG_AMOUNT_OF_QUERY_PARAMETERS);
        }
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }
}
