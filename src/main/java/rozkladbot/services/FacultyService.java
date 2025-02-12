package rozkladbot.services;

import rozkladbot.entities.Faculty;

import java.util.List;

public interface FacultyService extends BasicCrudService<Faculty> {
    Faculty getByName(String name);
    List<Faculty> getFacultiesByInstituteName(String instituteName);
    List<Faculty> findByInstituteId(Long instituteId);
    Faculty findByInstituteIdAndFacultyId(long instituteId, long facultyId);
}
