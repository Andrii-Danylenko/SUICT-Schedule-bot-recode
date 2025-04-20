package rozkladbot.utils.web.requester;

import org.springframework.stereotype.Component;
import rozkladbot.utils.date.DateUtils;
import java.time.LocalDate;
import java.util.HashMap;

import static rozkladbot.constants.AppConstants.COURSE;
import static rozkladbot.constants.AppConstants.FACULTY;
import static rozkladbot.constants.AppConstants.GROUP;
import static rozkladbot.constants.AppConstants.GROUP_NAME;
import static rozkladbot.constants.AppConstants.INSTITUTION;
import static rozkladbot.constants.AppConstants.QUERY_END_DATE;
import static rozkladbot.constants.AppConstants.QUERY_START_DATE;
import static rozkladbot.constants.ErrorConstants.WRONG_AMOUNT_OF_QUERY_PARAMETERS;

@Component
public class ParamsBuilderImpl implements ParamsBuilder {

    public HashMap<String, String> createParams(
            long groupId,
            long course,
            String groupName,
            long facultyId,
            long institutionId,
            LocalDate startDate,
            LocalDate endDate) {

        return buildFromValues(
                GROUP, String.valueOf(groupId),
                COURSE, String.valueOf(course),
                GROUP_NAME, groupName,
                FACULTY, String.valueOf(facultyId),
                INSTITUTION, String.valueOf(institutionId),
                QUERY_START_DATE, DateUtils.getDateAsString(startDate),
                QUERY_END_DATE, DateUtils.getDateAsString(endDate)
        );
    }

    @Override
    public HashMap<String, String> buildFromValues(String... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException(WRONG_AMOUNT_OF_QUERY_PARAMETERS);
        }
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }
}
