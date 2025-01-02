package rozkladbot.services;

import rozkladbot.entities.Institute;

import java.util.List;

public interface InstituteService {
    List<Institute> getAll();
    Institute getById(long id);
    Institute getByName(String name);
    Institute save(Institute institute);
}
