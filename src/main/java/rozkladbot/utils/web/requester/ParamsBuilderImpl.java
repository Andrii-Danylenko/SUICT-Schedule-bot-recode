package rozkladbot.utils.web.requester;

import org.springframework.stereotype.Component;
import rozkladbot.entities.User;

import java.util.HashMap;

@Component
public class ParamsBuilderImpl implements ParamsBuilder {
    @Override
    public HashMap<String, String> build(User user) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group", String.valueOf(user.getGroup().getId()));
        params.put("course", String.valueOf(user.getGroup().getCourse()));
        params.put("faculty", String.valueOf(user.getGroup().getFaculty().getId()));
        return params;
    }
}
