package rozkladbot.entities.dto;

import rozkladbot.entities.Faculty;

public class FacultyDTO {
    private Long id;
    private String facultyName;

    public FacultyDTO(Faculty faculty) {
        this.id = faculty.getFacultyId();
        this.facultyName = faculty.getFacultyName();
    }

    public Long getId() {
        return id;
    }

    public String getFacultyName() {
        return facultyName;
    }
}
