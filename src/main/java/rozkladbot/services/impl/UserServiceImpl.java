package rozkladbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.User;
import rozkladbot.exceptions.NoSuchEntityFoundException;
import rozkladbot.repos.UserRepo;
import rozkladbot.services.UserService;

import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<User> getAll() {
        return userRepo.findAll();
    }

    @Override
    public User getById(long id) {
        return userRepo.findById(id).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    @Override
    public User getByName(String name) {
        return userRepo.findByUsername(name).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public boolean existsById(long id) {
        return userRepo.existsById(id);
    }
}
