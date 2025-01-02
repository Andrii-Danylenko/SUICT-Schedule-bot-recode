package rozkladbot.entities;

public class ScheduleForm {
    private String institute;
    private String faculty;
    private String course;
    private String group;

    public ScheduleForm(String institute, String faculty, String course, String group) {
        this.institute = institute;
        this.faculty = faculty;
        this.course = course;
        this.group = group;
    }

    public ScheduleForm() {
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
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
