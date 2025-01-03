package rozkladbot.services;

import rozkladbot.entities.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(long id);

    User getByName(String name);

    User save(User user);

    boolean existsById(long id);
}
