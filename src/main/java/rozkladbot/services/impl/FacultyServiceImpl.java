package rozkladbot.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.Faculty;
import rozkladbot.exceptions.NoSuchEntityFoundException;
import rozkladbot.repos.FacultyRepo;
import rozkladbot.services.FacultyService;

import java.util.List;

@Service("facultyServiceImpl")
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepo facultyRepo;

    @Autowired
    public FacultyServiceImpl(FacultyRepo facultyRepo) {
        this.facultyRepo = facultyRepo;
    }

    public List<Faculty> getAll() {
        return facultyRepo.findAll();
    }

    public Faculty getById(long id) {
        return facultyRepo.findById(id).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    public Faculty getByName(String name) {
        return facultyRepo.findByFacultyName(name).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    public Faculty save(Faculty faculty) {
        return facultyRepo.save(faculty);
    }

    @Override
    public List<Faculty> getFacultiesByInstituteName(String instituteName) {
        return facultyRepo.getFacultyByInstituteName(instituteName);
    }
}
