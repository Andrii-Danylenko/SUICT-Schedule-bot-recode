package rozkladbot.services;

import rozkladbot.entities.Faculty;

import java.util.List;

public interface FacultyService {
    List<Faculty> getAll();

    Faculty getById(long id);

    Faculty getByName(String name);

    Faculty save(Faculty institute);
}
