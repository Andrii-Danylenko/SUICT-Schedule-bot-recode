package rozkladbot.utils.web.requester;

import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.User;

import java.time.LocalDate;
import java.util.HashMap;

public interface ParamsBuilder {
    HashMap<String, String> buildFromUser(User user, LocalDate startDate, LocalDate endDate);

    HashMap<String, String> buildFromGroupName(String groupName, LocalDate startDate, LocalDate endDate);

    HashMap<String, String> buildFromGroupId(long groupId, LocalDate startDate, LocalDate endDate);

    default HashMap<String, String> buildFromValues(String... values) {
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
