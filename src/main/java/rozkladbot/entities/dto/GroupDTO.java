package rozkladbot.entities.dto;

import rozkladbot.entities.Group;

public class GroupDTO {
    private Long id;
    private String name;
    private Long facultyId;
    private Long course;

    public GroupDTO(Group group) {
        this.id = group.getGroupId();
        this.name = group.getName();
        this.facultyId = group.getFaculty().getFacultyId();
        this.course = group.getCourse();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public Long getCourse() {
        return course;
    }

    public void setCourse(Long course) {
        this.course = course;
    }
}
