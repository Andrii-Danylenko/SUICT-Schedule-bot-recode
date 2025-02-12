package rozkladbot.utils.web.requester;

import org.springframework.stereotype.Component;
import rozkladbot.entities.Group;
import rozkladbot.entities.User;
import rozkladbot.services.GroupService;
import rozkladbot.utils.date.DateUtils;

import java.time.LocalDate;
import java.util.HashMap;

@Component
public class ParamsBuilderImpl implements ParamsBuilder {
    private final GroupService groupService;

    public ParamsBuilderImpl(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public HashMap<String, String> buildFromUser(User user, LocalDate startDate, LocalDate endDate) {
        return createParams(user.getGroup(), startDate, endDate);
    }

    @Override
    public HashMap<String, String> buildFromGroupName(String groupName, LocalDate startDate, LocalDate endDate) {
        return createParams(groupService.getByName(groupName), startDate, endDate);
    }

    @Override
    public HashMap<String, String> buildFromGroupId(long groupId, LocalDate startDate, LocalDate endDate) {
        return createParams(groupService.getById(groupId), startDate, endDate);
    }

    private HashMap<String, String> createParams(Group group, LocalDate startDate, LocalDate endDate) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group", String.valueOf(group.getGroupId()));
        params.put("course", String.valueOf(group.getCourse()));
        params.put("faculty", String.valueOf(group.getFaculty().getFacultyId()));
        params.put("dateFrom", DateUtils.getDateAsString(startDate));
        params.put("dateTo", DateUtils.getDateAsString(endDate));
        params.put("institution", String.valueOf(group.getFaculty().getInstitute().getId()));
        return params;
    }
}
