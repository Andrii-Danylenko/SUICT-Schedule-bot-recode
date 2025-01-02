package rozkladbot.web.requester;

import rozkladbot.entities.User;

import java.util.HashMap;

public interface ParamsBuilder {
    HashMap<String, String> build(User user);
}
