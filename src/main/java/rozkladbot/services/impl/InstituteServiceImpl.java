package rozkladbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.Institute;
import rozkladbot.exceptions.EntityAlreadyExistsException;
import rozkladbot.exceptions.NoSuchEntityFoundException;
import rozkladbot.repos.InstituteRepo;
import rozkladbot.services.InstituteService;

import java.util.Collection;
import java.util.List;

@Service("instituteServiceImpl")
public class InstituteServiceImpl implements InstituteService {
    private final InstituteRepo instituteRepo;

    @Autowired
    public InstituteServiceImpl(InstituteRepo instituteRepo) {
        this.instituteRepo = instituteRepo;
    }

    @Override
    public List<Institute> getAll() {
        return instituteRepo.findAll();
    }

    @Override
    public Institute getById(long id) {
        return instituteRepo.findById(id).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Institute getByName(String name) {
        return instituteRepo.findByName(name).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    @Override
    @Transactional
    public List<Institute> findAllEager() {
        return instituteRepo.findAllEager();
    }

    @Override
    @Transactional
    public Institute save(Institute institute) {
        if (instituteRepo.existsById(institute.getId())) {
            throw new EntityAlreadyExistsException();
        }
        return instituteRepo.save(institute);
    }

    @Override
    @Transactional
    public void saveAll(Collection<Institute> value) {
        instituteRepo.saveAllAndFlush(value);
    }
}
