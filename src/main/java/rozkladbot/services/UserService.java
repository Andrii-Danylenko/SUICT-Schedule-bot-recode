package rozkladbot.services;

import rozkladbot.entities.User;

import java.util.Set;

public interface UserService extends BasicCrudService<User> {
    User getByName(String name);

    boolean existsById(long id);

    Set<Long> getAllUserIds();
}
