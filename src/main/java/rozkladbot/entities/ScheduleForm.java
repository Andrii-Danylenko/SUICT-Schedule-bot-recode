package rozkladbot.entities;

public class ScheduleForm {
    private Long institute;
    private Long faculty;
    private Long course;
    private Long group;

    public ScheduleForm(Long institute, Long faculty, Long course, Long group) {
        this.institute = institute;
        this.faculty = faculty;
        this.course = course;
        this.group = group;
    }

    public ScheduleForm() {
    }

    public Long getInstitute() {
        return institute;
    }

    public void setInstitute(Long institute) {
        this.institute = institute;
    }

    public Long getFaculty() {
        return faculty;
    }

    public void setFaculty(Long faculty) {
        this.faculty = faculty;
    }

    public Long getCourse() {
        return course;
    }

    public void setCourse(Long course) {
        this.course = course;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "ScheduleForm{" +
               "institute='" + institute + '\'' +
               ", faculty='" + faculty + '\'' +
               ", course='" + course + '\'' +
               ", group='" + group + '\'' +
               '}';
    }
}
