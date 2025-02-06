package rozkladbot.utils.web.requester;

import rozkladbot.entities.User;

import java.time.LocalDate;
import java.util.HashMap;

public interface ParamsBuilder {
    HashMap<String, String> buildFromUser(User user, LocalDate startDate, LocalDate endDate);
    HashMap<String, String> buildFromGroupName(String groupName, LocalDate startDate, LocalDate endDate);
    HashMap<String, String> buildFromGroupId(long groupId, LocalDate startDate, LocalDate endDate);
}
